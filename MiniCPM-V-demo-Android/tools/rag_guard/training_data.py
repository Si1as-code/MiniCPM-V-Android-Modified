"""Validated input formatting and dependency-free metrics for RAG guard training."""

from __future__ import annotations

import json
import math
from pathlib import Path
from typing import Iterable, Mapping, Sequence


LABELS_BY_TASK: dict[str, tuple[str, str, str]] = {
    "answerability": ("SUPPORTED", "PARTIAL", "UNSUPPORTED"),
    "groundedness": ("GROUNDED", "PARTIAL", "UNGROUNDED"),
}

_REQUIRED_TEXT_FIELDS = (
    "id",
    "task",
    "label",
    "question",
    "evidence",
    "answer",
    "document_id",
    "split",
    "language",
)


def format_model_input(row: Mapping[str, str]) -> str:
    task = row.get("task")
    if task not in LABELS_BY_TASK:
        raise ValueError(f"unsupported task: {task!r}")
    question = row.get("question", "").strip()
    evidence = row.get("evidence", "").strip()
    if not question or not evidence:
        raise ValueError("question and evidence must be non-empty")
    parts = [f"query: {question}", f"evidence: {evidence}"]
    if task == "groundedness":
        answer = row.get("answer", "").strip()
        if not answer:
            raise ValueError("groundedness answer must be non-empty")
        parts.append(f"answer: {answer}")
    return "\n".join(parts)


def load_jsonl(path: Path, *, expected_task: str, expected_split: str) -> list[dict[str, str]]:
    if expected_task not in LABELS_BY_TASK:
        raise ValueError(f"unsupported task: {expected_task!r}")
    rows: list[dict[str, str]] = []
    with path.resolve().open("r", encoding="utf-8") as source:
        for line_number, line in enumerate(source, start=1):
            try:
                row = json.loads(line)
            except json.JSONDecodeError as error:
                raise ValueError(f"invalid JSON on line {line_number}") from error
            if not isinstance(row, dict):
                raise ValueError(f"line {line_number} must contain an object")
            if any(not isinstance(row.get(field), str) for field in _REQUIRED_TEXT_FIELDS):
                raise ValueError(f"line {line_number} has missing or non-string fields")
            if row["task"] != expected_task:
                raise ValueError(f"unexpected task on line {line_number}")
            if row["split"] != expected_split:
                raise ValueError(f"unexpected split on line {line_number}")
            if row["label"] not in LABELS_BY_TASK[expected_task]:
                raise ValueError(f"invalid label on line {line_number}")
            format_model_input(row)
            rows.append(row)
    if not rows:
        raise ValueError(f"dataset is empty: {path}")
    return rows


def macro_f1(targets: Sequence[int], predictions: Sequence[int], class_count: int) -> float:
    if len(targets) != len(predictions) or not targets or class_count < 2:
        raise ValueError("targets and predictions must be non-empty and aligned")
    scores: list[float] = []
    for label in range(class_count):
        true_positive = sum(t == label and p == label for t, p in zip(targets, predictions))
        false_positive = sum(t != label and p == label for t, p in zip(targets, predictions))
        false_negative = sum(t == label and p != label for t, p in zip(targets, predictions))
        denominator = 2 * true_positive + false_positive + false_negative
        scores.append(0.0 if denominator == 0 else (2 * true_positive) / denominator)
    return sum(scores) / class_count


def expected_calibration_error(
    probabilities: Sequence[Sequence[float]],
    targets: Sequence[int],
    *,
    bins: int = 10,
) -> float:
    if len(probabilities) != len(targets) or not targets or bins < 1:
        raise ValueError("probabilities and targets must be non-empty and aligned")
    grouped: list[list[tuple[float, bool]]] = [[] for _ in range(bins)]
    for row, target in zip(probabilities, targets):
        if not row or any(not math.isfinite(value) or value < 0.0 or value > 1.0 for value in row):
            raise ValueError("probabilities must be finite values in [0, 1]")
        prediction = max(range(len(row)), key=row.__getitem__)
        confidence = row[prediction]
        index = min(int(confidence * bins), bins - 1)
        grouped[index].append((confidence, prediction == target))
    total = len(targets)
    error = 0.0
    for bucket in grouped:
        if bucket:
            average_confidence = sum(item[0] for item in bucket) / len(bucket)
            average_accuracy = sum(item[1] for item in bucket) / len(bucket)
            error += (len(bucket) / total) * abs(average_accuracy - average_confidence)
    return error
