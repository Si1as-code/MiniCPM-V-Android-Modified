"""Train a shared multilingual encoder with answerability and groundedness heads."""

from __future__ import annotations

import argparse
import json
import math
import random
from contextlib import nullcontext
from pathlib import Path
from typing import Iterable, Mapping, Sequence

import torch
from safetensors.torch import load_file, save_file
from torch import nn
from torch.utils.data import DataLoader, Dataset
from transformers import AutoModel, AutoTokenizer, get_linear_schedule_with_warmup

from tools.rag_guard.model import DualHeadRagGuard
from tools.rag_guard.training_data import (
    LABELS_BY_TASK,
    expected_calibration_error,
    format_model_input,
    load_jsonl,
    macro_f1,
)


TASK_IDS = {"answerability": 0, "groundedness": 1}


def is_better_checkpoint(
    *, score: float, ece: float, best_score: float, best_ece: float, tolerance: float = 1e-12
) -> bool:
    if score > best_score + tolerance:
        return True
    return abs(score - best_score) <= tolerance and ece < best_ece


class EncodedRows(Dataset[dict[str, object]]):
    def __init__(self, rows: Sequence[Mapping[str, str]], tokenizer: object, max_length: int) -> None:
        if not 32 <= max_length <= 1024:
            raise ValueError("max_length must be between 32 and 1024")
        texts = [format_model_input(row) for row in rows]
        self.encodings = tokenizer(texts, truncation=True, max_length=max_length, padding=False)
        self.task_ids = [TASK_IDS[row["task"]] for row in rows]
        self.labels = [LABELS_BY_TASK[row["task"]].index(row["label"]) for row in rows]

    def __len__(self) -> int:
        return len(self.labels)

    def __getitem__(self, index: int) -> dict[str, object]:
        return {
            "input_ids": self.encodings["input_ids"][index],
            "attention_mask": self.encodings["attention_mask"][index],
            "task_ids": self.task_ids[index],
            "labels": self.labels[index],
        }


def make_collator(tokenizer: object):
    def collate(rows: Sequence[Mapping[str, object]]) -> dict[str, torch.Tensor]:
        encoded = tokenizer.pad(
            {
                "input_ids": [row["input_ids"] for row in rows],
                "attention_mask": [row["attention_mask"] for row in rows],
            },
            padding=True,
            return_tensors="pt",
        )
        encoded["task_ids"] = torch.tensor([row["task_ids"] for row in rows], dtype=torch.long)
        encoded["labels"] = torch.tensor([row["labels"] for row in rows], dtype=torch.long)
        return encoded

    return collate


def train_epoch(
    *,
    model: nn.Module,
    batches: Iterable[Mapping[str, torch.Tensor]],
    optimizer: torch.optim.Optimizer,
    scheduler: object | None,
    device: torch.device,
    gradient_accumulation: int,
    use_bf16: bool,
) -> float:
    if gradient_accumulation < 1:
        raise ValueError("gradient_accumulation must be positive")
    model.train()
    optimizer.zero_grad(set_to_none=True)
    total_loss = 0.0
    batch_count = len(batches)  # type: ignore[arg-type]
    for batch_index, batch in enumerate(batches):
        moved = {key: value.to(device, non_blocking=True) for key, value in batch.items()}
        autocast = (
            torch.autocast(device_type="cuda", dtype=torch.bfloat16)
            if use_bf16 and device.type == "cuda"
            else nullcontext()
        )
        with autocast:
            logits = model(moved["input_ids"], moved["attention_mask"], moved["task_ids"])
            loss = torch.nn.functional.cross_entropy(logits, moved["labels"])
        total_loss += float(loss.detach().cpu())
        (loss / gradient_accumulation).backward()
        should_step = (batch_index + 1) % gradient_accumulation == 0 or batch_index + 1 == batch_count
        if should_step:
            torch.nn.utils.clip_grad_norm_(model.parameters(), max_norm=1.0)
            optimizer.step()
            if scheduler is not None:
                scheduler.step()
            optimizer.zero_grad(set_to_none=True)
    if batch_count == 0:
        raise ValueError("training batches must be non-empty")
    return total_loss / batch_count


@torch.no_grad()
def evaluate(
    model: nn.Module,
    batches: Iterable[Mapping[str, torch.Tensor]],
    device: torch.device,
) -> dict[str, dict[str, float]]:
    model.eval()
    collected: dict[int, dict[str, list[object]]] = {
        0: {"targets": [], "predictions": [], "probabilities": []},
        1: {"targets": [], "predictions": [], "probabilities": []},
    }
    for batch in batches:
        moved = {key: value.to(device, non_blocking=True) for key, value in batch.items()}
        probabilities = torch.softmax(
            model(moved["input_ids"], moved["attention_mask"], moved["task_ids"]), dim=-1
        ).cpu()
        targets = moved["labels"].cpu()
        task_ids = moved["task_ids"].cpu()
        for task_id in (0, 1):
            mask = task_ids.eq(task_id)
            if mask.any():
                selected = probabilities[mask]
                collected[task_id]["probabilities"].extend(selected.tolist())
                collected[task_id]["targets"].extend(targets[mask].tolist())
                collected[task_id]["predictions"].extend(selected.argmax(dim=-1).tolist())
    result: dict[str, dict[str, float]] = {}
    for task, task_id in TASK_IDS.items():
        targets = collected[task_id]["targets"]
        predictions = collected[task_id]["predictions"]
        probabilities = collected[task_id]["probabilities"]
        if not targets:
            raise ValueError(f"evaluation has no rows for {task}")
        accuracy = sum(t == p for t, p in zip(targets, predictions)) / len(targets)
        result[task] = {
            "accuracy": accuracy,
            "macro_f1": macro_f1(targets, predictions, 3),
            "ece": expected_calibration_error(probabilities, targets, bins=10),
            "count": float(len(targets)),
        }
    return result


def _load_split(data_dir: Path, split: str) -> list[dict[str, str]]:
    rows: list[dict[str, str]] = []
    for task in TASK_IDS:
        rows.extend(
            load_jsonl(
                data_dir / f"{task}_{split}.jsonl",
                expected_task=task,
                expected_split=split,
            )
        )
    return rows


def _write_json(path: Path, value: object) -> None:
    temporary = path.with_suffix(path.suffix + ".tmp")
    temporary.write_text(
        json.dumps(value, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    temporary.replace(path)


def _state_dict_on_cpu(model: nn.Module) -> dict[str, torch.Tensor]:
    return {name: tensor.detach().cpu().contiguous() for name, tensor in model.state_dict().items()}


def run_training(arguments: argparse.Namespace) -> dict[str, object]:
    random.seed(arguments.seed)
    torch.manual_seed(arguments.seed)
    torch.cuda.manual_seed_all(arguments.seed)
    torch.backends.cuda.matmul.allow_tf32 = True
    torch.backends.cudnn.allow_tf32 = True

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    if device.type != "cuda" and not arguments.allow_cpu:
        raise RuntimeError("CUDA is required unless --allow-cpu is explicitly set")
    data_dir = arguments.data_dir.resolve()
    output_dir = arguments.output_dir.resolve()
    output_dir.mkdir(parents=True, exist_ok=True)

    train_rows = _load_split(data_dir, "train")
    calibration_rows = _load_split(data_dir, "calibration")
    test_rows = _load_split(data_dir, "test")
    tokenizer = AutoTokenizer.from_pretrained(arguments.model, use_fast=True)
    encoder = AutoModel.from_pretrained(arguments.model)
    hidden_size = int(encoder.config.hidden_size)
    model = DualHeadRagGuard(encoder, hidden_size=hidden_size, dropout=arguments.dropout).to(device)

    collator = make_collator(tokenizer)
    generator = torch.Generator().manual_seed(arguments.seed)
    train_loader = DataLoader(
        EncodedRows(train_rows, tokenizer, arguments.max_length),
        batch_size=arguments.batch_size,
        shuffle=True,
        collate_fn=collator,
        generator=generator,
        pin_memory=device.type == "cuda",
    )
    calibration_loader = DataLoader(
        EncodedRows(calibration_rows, tokenizer, arguments.max_length),
        batch_size=arguments.eval_batch_size,
        shuffle=False,
        collate_fn=collator,
        pin_memory=device.type == "cuda",
    )
    test_loader = DataLoader(
        EncodedRows(test_rows, tokenizer, arguments.max_length),
        batch_size=arguments.eval_batch_size,
        shuffle=False,
        collate_fn=collator,
        pin_memory=device.type == "cuda",
    )
    optimizer = torch.optim.AdamW(
        model.parameters(), lr=arguments.learning_rate, weight_decay=arguments.weight_decay
    )
    optimizer_steps_per_epoch = math.ceil(len(train_loader) / arguments.gradient_accumulation)
    total_steps = optimizer_steps_per_epoch * arguments.epochs
    warmup_steps = int(total_steps * arguments.warmup_ratio)
    scheduler = get_linear_schedule_with_warmup(optimizer, warmup_steps, total_steps)

    best_score = -1.0
    best_ece = math.inf
    history: list[dict[str, object]] = []
    checkpoint_path = output_dir / "model.safetensors"
    for epoch in range(1, arguments.epochs + 1):
        loss = train_epoch(
            model=model,
            batches=train_loader,
            optimizer=optimizer,
            scheduler=scheduler,
            device=device,
            gradient_accumulation=arguments.gradient_accumulation,
            use_bf16=arguments.bf16,
        )
        calibration = evaluate(model, calibration_loader, device)
        score = sum(metrics["macro_f1"] for metrics in calibration.values()) / len(calibration)
        calibration_ece = sum(metrics["ece"] for metrics in calibration.values()) / len(calibration)
        epoch_result = {
            "epoch": epoch,
            "train_loss": loss,
            "calibration": calibration,
            "score": score,
            "calibration_ece": calibration_ece,
        }
        history.append(epoch_result)
        print(json.dumps(epoch_result, ensure_ascii=False, sort_keys=True), flush=True)
        if is_better_checkpoint(
            score=score,
            ece=calibration_ece,
            best_score=best_score,
            best_ece=best_ece,
        ):
            best_score = score
            best_ece = calibration_ece
            temporary_checkpoint = checkpoint_path.with_suffix(".safetensors.tmp")
            save_file(_state_dict_on_cpu(model), str(temporary_checkpoint))
            temporary_checkpoint.replace(checkpoint_path)

    model.load_state_dict(load_file(str(checkpoint_path), device=str(device)))
    final_metrics = {
        "best_calibration_macro_f1": best_score,
        "best_calibration_ece": best_ece,
        "calibration": evaluate(model, calibration_loader, device),
        "test": evaluate(model, test_loader, device),
        "history": history,
    }
    tokenizer.save_pretrained(output_dir / "tokenizer")
    encoder.config.save_pretrained(output_dir / "encoder_config")
    manifest = {
        "architecture": "shared_encoder_dual_three_class_heads",
        "base_model": arguments.model,
        "labels_by_task": LABELS_BY_TASK,
        "task_ids": TASK_IDS,
        "max_length": arguments.max_length,
        "hidden_size": hidden_size,
        "seed": arguments.seed,
        "versions": {
            "torch": torch.__version__,
            "transformers": __import__("transformers").__version__,
        },
    }
    _write_json(output_dir / "manifest.json", manifest)
    _write_json(output_dir / "metrics.json", final_metrics)
    return final_metrics


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--model", default="intfloat/multilingual-e5-small")
    parser.add_argument("--data-dir", type=Path, required=True)
    parser.add_argument("--output-dir", type=Path, required=True)
    parser.add_argument("--epochs", type=int, default=4)
    parser.add_argument("--batch-size", type=int, default=16)
    parser.add_argument("--eval-batch-size", type=int, default=32)
    parser.add_argument("--gradient-accumulation", type=int, default=2)
    parser.add_argument("--max-length", type=int, default=256)
    parser.add_argument("--learning-rate", type=float, default=2e-5)
    parser.add_argument("--weight-decay", type=float, default=0.01)
    parser.add_argument("--warmup-ratio", type=float, default=0.1)
    parser.add_argument("--dropout", type=float, default=0.1)
    parser.add_argument("--seed", type=int, default=42)
    parser.add_argument("--bf16", action=argparse.BooleanOptionalAction, default=True)
    parser.add_argument("--allow-cpu", action="store_true")
    arguments = parser.parse_args()
    if arguments.epochs < 1 or arguments.batch_size < 1 or arguments.eval_batch_size < 1:
        parser.error("epochs and batch sizes must be positive")
    if arguments.gradient_accumulation < 1 or not 0.0 <= arguments.warmup_ratio < 1.0:
        parser.error("invalid gradient accumulation or warmup ratio")
    return arguments


if __name__ == "__main__":
    run_training(parse_args())
