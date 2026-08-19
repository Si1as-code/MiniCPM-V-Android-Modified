"""Export, dynamically quantize, and verify the dual-head RAG guard model."""

from __future__ import annotations

import argparse
import hashlib
import json
import math
import re
from pathlib import Path
from typing import Mapping, Sequence

from tools.rag_guard.training_data import (
    LABELS_BY_TASK,
    expected_calibration_error,
    format_model_input,
    load_jsonl,
    macro_f1,
)


TASK_IDS = {"answerability": 0, "groundedness": 1}
QUANTIZED_OP_TYPES = ("MatMul", "Gemm", "Gather")
PER_CHANNEL_QUANTIZATION = False
SAFE_FILE_NAME = re.compile(r"[A-Za-z0-9._-]{1,128}")
SHA256 = re.compile(r"[0-9a-f]{64}")


def _sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.resolve().open("rb") as source:
        for block in iter(lambda: source.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def build_artifact_manifest(
    *,
    model_path: Path,
    tokenizer_sha256: str,
    metrics: Mapping[str, object],
    max_tokens: int,
) -> dict[str, object]:
    model_path = model_path.resolve()
    if not model_path.is_file() or not SAFE_FILE_NAME.fullmatch(model_path.name):
        raise ValueError("model_path must be a safe, existing file")
    if not SHA256.fullmatch(tokenizer_sha256):
        raise ValueError("tokenizer_sha256 must be lowercase SHA-256")
    if not 1 <= max_tokens <= 256:
        raise ValueError("max_tokens must be between 1 and 256")
    return {
        "schema_version": 1,
        "architecture": "shared_encoder_dual_three_class_heads",
        "max_tokens": max_tokens,
        "task_ids": TASK_IDS,
        "labels_by_task": LABELS_BY_TASK,
        "inputs": {
            "input_ids": "int64[batch,sequence]",
            "attention_mask": "int64[batch,sequence]",
            "task_ids": "int64[batch]",
        },
        "output": {"logits": "float32[batch,3]"},
        "external_tokenizer_sha256": tokenizer_sha256,
        "files": {
            model_path.name: {
                "bytes": model_path.stat().st_size,
                "sha256": _sha256(model_path),
            }
        },
        "quality": dict(metrics),
    }


def quantization_passes(
    *,
    label_agreement: float,
    largest_macro_f1_drop: float,
    int8_bytes: int,
    fp32_bytes: int,
) -> bool:
    return (
        label_agreement >= 0.995
        and largest_macro_f1_drop <= 0.01
        and 0 < int8_bytes <= fp32_bytes * 0.40
    )


def _write_json(path: Path, value: object) -> None:
    temporary = path.with_suffix(path.suffix + ".tmp")
    temporary.write_text(
        json.dumps(value, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    temporary.replace(path)


def _load_regression_rows(path: Path) -> list[dict[str, str]]:
    rows: list[dict[str, str]] = []
    with path.resolve().open("r", encoding="utf-8") as source:
        for line_number, line in enumerate(source, start=1):
            row = json.loads(line)
            if not isinstance(row, dict) or row.get("task") not in TASK_IDS:
                raise ValueError(f"invalid regression row on line {line_number}")
            if row.get("split") != "test" or row.get("label") not in LABELS_BY_TASK[row["task"]]:
                raise ValueError(f"invalid regression label on line {line_number}")
            format_model_input(row)
            rows.append(row)
    if not rows:
        raise ValueError("regression dataset is empty")
    return rows


def _load_evaluation_rows(data_dir: Path, regression_path: Path) -> dict[str, list[dict[str, str]]]:
    result: dict[str, list[dict[str, str]]] = {}
    for split in ("calibration", "test"):
        rows: list[dict[str, str]] = []
        for task in TASK_IDS:
            rows.extend(
                load_jsonl(
                    data_dir / f"{task}_{split}.jsonl",
                    expected_task=task,
                    expected_split=split,
                )
            )
        result[split] = rows
    result["regression"] = _load_regression_rows(regression_path)
    return result


def _load_trained_model(checkpoint_dir: Path, base_model: Path):
    import torch
    from safetensors.torch import load_file
    from transformers import AutoModel, AutoTokenizer

    from tools.rag_guard.model import DualHeadRagGuard

    tokenizer = AutoTokenizer.from_pretrained(base_model, local_files_only=True, use_fast=True)
    encoder = AutoModel.from_pretrained(base_model, local_files_only=True)
    model = DualHeadRagGuard(encoder, hidden_size=int(encoder.config.hidden_size), dropout=0.0)
    model.load_state_dict(load_file(str(checkpoint_dir / "model.safetensors"), device="cpu"))
    model.eval()
    return torch, tokenizer, model


def _export_fp32(torch: object, model: object, output_path: Path, max_tokens: int) -> None:
    sample_ids = torch.ones((2, min(max_tokens, 16)), dtype=torch.long)
    sample_mask = torch.ones_like(sample_ids)
    sample_tasks = torch.tensor([0, 1], dtype=torch.long)
    torch.onnx.export(
        model,
        (sample_ids, sample_mask, sample_tasks),
        str(output_path),
        input_names=["input_ids", "attention_mask", "task_ids"],
        output_names=["logits"],
        dynamic_axes={
            "input_ids": {0: "batch", 1: "sequence"},
            "attention_mask": {0: "batch", 1: "sequence"},
            "task_ids": {0: "batch"},
            "logits": {0: "batch"},
        },
        opset_version=17,
        do_constant_folding=True,
    )


def _quantize(fp32_path: Path, int8_path: Path) -> None:
    from onnxruntime.quantization import QuantType, quantize_dynamic

    quantize_dynamic(
        model_input=str(fp32_path),
        model_output=str(int8_path),
        per_channel=PER_CHANNEL_QUANTIZATION,
        reduce_range=False,
        weight_type=QuantType.QInt8,
        op_types_to_quantize=list(QUANTIZED_OP_TYPES),
        extra_options={"MatMulConstBOnly": True},
    )


def _validate_onnx(path: Path) -> None:
    import onnx
    import onnxruntime as ort

    model = onnx.load(str(path), load_external_data=True)
    onnx.checker.check_model(model, full_check=True)
    session = ort.InferenceSession(str(path), providers=["CPUExecutionProvider"])
    inputs = {item.name: item.type for item in session.get_inputs()}
    outputs = {item.name: item.type for item in session.get_outputs()}
    if inputs != {
        "input_ids": "tensor(int64)",
        "attention_mask": "tensor(int64)",
        "task_ids": "tensor(int64)",
    }:
        raise RuntimeError(f"unexpected ONNX inputs: {inputs}")
    if outputs != {"logits": "tensor(float)"}:
        raise RuntimeError(f"unexpected ONNX outputs: {outputs}")


def _session_logits(session: object, tokenizer: object, rows: Sequence[Mapping[str, str]], max_tokens: int):
    import numpy as np

    all_logits: list[object] = []
    for start in range(0, len(rows), 32):
        batch = rows[start : start + 32]
        encoded = tokenizer(
            [format_model_input(row) for row in batch],
            padding=True,
            truncation=True,
            max_length=max_tokens,
            return_tensors="np",
        )
        logits = session.run(
            ["logits"],
            {
                "input_ids": encoded["input_ids"].astype(np.int64, copy=False),
                "attention_mask": encoded["attention_mask"].astype(np.int64, copy=False),
                "task_ids": np.asarray([TASK_IDS[row["task"]] for row in batch], dtype=np.int64),
            },
        )[0]
        all_logits.append(logits)
    return np.concatenate(all_logits, axis=0)


def _pytorch_logits(torch: object, model: object, tokenizer: object, rows: Sequence[Mapping[str, str]], max_tokens: int):
    import numpy as np

    all_logits: list[object] = []
    for start in range(0, len(rows), 32):
        batch = rows[start : start + 32]
        encoded = tokenizer(
            [format_model_input(row) for row in batch],
            padding=True,
            truncation=True,
            max_length=max_tokens,
            return_tensors="pt",
        )
        task_ids = torch.tensor([TASK_IDS[row["task"]] for row in batch], dtype=torch.long)
        with torch.no_grad():
            logits = model(encoded["input_ids"], encoded["attention_mask"], task_ids)
        all_logits.append(np.asarray(logits.cpu(), dtype=np.float32))
    return np.concatenate(all_logits, axis=0)


def _softmax(logits):
    import numpy as np

    shifted = logits - logits.max(axis=1, keepdims=True)
    values = np.exp(shifted)
    return values / values.sum(axis=1, keepdims=True)


def _task_metrics(rows: Sequence[Mapping[str, str]], logits) -> dict[str, dict[str, float]]:
    import numpy as np

    probabilities = _softmax(logits)
    result: dict[str, dict[str, float]] = {}
    for task in TASK_IDS:
        indices = [index for index, row in enumerate(rows) if row["task"] == task]
        targets = [LABELS_BY_TASK[task].index(rows[index]["label"]) for index in indices]
        selected = probabilities[np.asarray(indices)]
        predictions = selected.argmax(axis=1).tolist()
        result[task] = {
            "count": float(len(indices)),
            "accuracy": sum(a == b for a, b in zip(targets, predictions)) / len(targets),
            "macro_f1": macro_f1(targets, predictions, 3),
            "ece": expected_calibration_error(selected.tolist(), targets, bins=10),
        }
    return result


def run_export(arguments: argparse.Namespace) -> dict[str, object]:
    import numpy as np
    import onnxruntime as ort

    checkpoint_dir = arguments.checkpoint_dir.resolve()
    base_model = arguments.base_model.resolve()
    output_dir = arguments.output_dir.resolve()
    output_dir.mkdir(parents=True, exist_ok=True)
    torch, tokenizer, model = _load_trained_model(checkpoint_dir, base_model)
    fp32_path = output_dir / "model.fp32.onnx"
    int8_path = output_dir / "model.int8.onnx"
    _export_fp32(torch, model, fp32_path, arguments.max_tokens)
    _validate_onnx(fp32_path)
    _quantize(fp32_path, int8_path)
    _validate_onnx(int8_path)

    fp32_session = ort.InferenceSession(str(fp32_path), providers=["CPUExecutionProvider"])
    int8_session = ort.InferenceSession(str(int8_path), providers=["CPUExecutionProvider"])
    evaluation_rows = _load_evaluation_rows(arguments.data_dir.resolve(), arguments.regression_path.resolve())
    all_fp32: list[object] = []
    all_int8: list[object] = []
    split_metrics: dict[str, object] = {}
    largest_macro_f1_drop = 0.0
    for split, rows in evaluation_rows.items():
        fp32_logits = _session_logits(fp32_session, tokenizer, rows, arguments.max_tokens)
        int8_logits = _session_logits(int8_session, tokenizer, rows, arguments.max_tokens)
        fp32_metrics = _task_metrics(rows, fp32_logits)
        int8_metrics = _task_metrics(rows, int8_logits)
        for task in TASK_IDS:
            largest_macro_f1_drop = max(
                largest_macro_f1_drop,
                fp32_metrics[task]["macro_f1"] - int8_metrics[task]["macro_f1"],
            )
        split_metrics[split] = {"fp32": fp32_metrics, "int8": int8_metrics}
        all_fp32.append(fp32_logits)
        all_int8.append(int8_logits)

    fp32_logits = np.concatenate(all_fp32, axis=0)
    int8_logits = np.concatenate(all_int8, axis=0)
    label_agreement = float((fp32_logits.argmax(axis=1) == int8_logits.argmax(axis=1)).mean())
    logit_delta = np.abs(fp32_logits - int8_logits)
    regression_rows = evaluation_rows["regression"]
    pytorch_logits = _pytorch_logits(torch, model, tokenizer, regression_rows, arguments.max_tokens)
    regression_fp32 = _session_logits(fp32_session, tokenizer, regression_rows, arguments.max_tokens)
    fp32_pytorch_max_abs = float(np.abs(pytorch_logits - regression_fp32).max())
    gate_passed = quantization_passes(
        label_agreement=label_agreement,
        largest_macro_f1_drop=largest_macro_f1_drop,
        int8_bytes=int8_path.stat().st_size,
        fp32_bytes=fp32_path.stat().st_size,
    ) and fp32_pytorch_max_abs <= 1e-4
    metrics = {
        "gate_passed": gate_passed,
        "fp32_pytorch_max_abs": fp32_pytorch_max_abs,
        "int8_fp32_label_agreement": label_agreement,
        "int8_fp32_max_abs_logit_delta": float(logit_delta.max()),
        "int8_fp32_mean_abs_logit_delta": float(logit_delta.mean()),
        "largest_macro_f1_drop": largest_macro_f1_drop,
        "fp32_bytes": fp32_path.stat().st_size,
        "int8_bytes": int8_path.stat().st_size,
        "compression_ratio": int8_path.stat().st_size / fp32_path.stat().st_size,
        "splits": split_metrics,
        "versions": {
            "torch": torch.__version__,
            "onnxruntime": ort.__version__,
        },
    }
    _write_json(output_dir / "quantization_metrics.json", metrics)
    if not gate_passed:
        raise RuntimeError("quantized model failed alignment or quality gates")
    manifest = build_artifact_manifest(
        model_path=int8_path,
        tokenizer_sha256=arguments.tokenizer_sha256,
        metrics={
            "int8_fp32_label_agreement": label_agreement,
            "largest_macro_f1_drop": largest_macro_f1_drop,
            "fp32_pytorch_max_abs": fp32_pytorch_max_abs,
        },
        max_tokens=arguments.max_tokens,
    )
    _write_json(output_dir / "manifest.json", manifest)
    return metrics


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--checkpoint-dir", type=Path, required=True)
    parser.add_argument("--base-model", type=Path, required=True)
    parser.add_argument("--data-dir", type=Path, required=True)
    parser.add_argument("--regression-path", type=Path, required=True)
    parser.add_argument("--output-dir", type=Path, required=True)
    parser.add_argument("--tokenizer-sha256", required=True)
    parser.add_argument("--max-tokens", type=int, default=256)
    arguments = parser.parse_args()
    if not 1 <= arguments.max_tokens <= 256:
        parser.error("max-tokens must be between 1 and 256")
    if not SHA256.fullmatch(arguments.tokenizer_sha256):
        parser.error("tokenizer-sha256 must be lowercase SHA-256")
    return arguments


if __name__ == "__main__":
    result = run_export(parse_args())
    print(json.dumps(result, ensure_ascii=False, sort_keys=True))
