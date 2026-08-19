import json
import tempfile
import unittest
from pathlib import Path

from tools.rag_guard.training_data import (
    LABELS_BY_TASK,
    expected_calibration_error,
    format_model_input,
    load_jsonl,
    macro_f1,
)


class TrainingDataTest(unittest.TestCase):
    def test_formats_each_task_without_adding_an_empty_answer(self) -> None:
        answerability = {
            "task": "answerability",
            "question": "差旅上限是多少？",
            "evidence": "差旅报销上限为 800 元。",
            "answer": "",
        }
        groundedness = {
            **answerability,
            "task": "groundedness",
            "answer": "上限为 800 元。",
        }

        self.assertEqual(
            format_model_input(answerability),
            "query: 差旅上限是多少？\nevidence: 差旅报销上限为 800 元。",
        )
        self.assertEqual(
            format_model_input(groundedness),
            "query: 差旅上限是多少？\nevidence: 差旅报销上限为 800 元。\nanswer: 上限为 800 元。",
        )

    def test_loader_rejects_a_label_from_the_other_task(self) -> None:
        row = {
            "id": "bad-1",
            "task": "answerability",
            "label": "GROUNDED",
            "question": "问题",
            "evidence": "证据",
            "answer": "",
            "document_id": "doc-1",
            "split": "train",
            "language": "zh",
        }
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "bad.jsonl"
            path.write_text(json.dumps(row, ensure_ascii=False) + "\n", encoding="utf-8")

            with self.assertRaisesRegex(ValueError, "invalid label"):
                load_jsonl(path, expected_task="answerability", expected_split="train")

    def test_metrics_are_macro_averaged_and_calibrated(self) -> None:
        labels = LABELS_BY_TASK["answerability"]
        self.assertAlmostEqual(macro_f1([0, 1, 2], [0, 1, 1], len(labels)), 5 / 9)
        self.assertAlmostEqual(
            expected_calibration_error(
                probabilities=[[0.8, 0.1, 0.1], [0.2, 0.7, 0.1]],
                targets=[0, 1],
                bins=2,
            ),
            0.25,
        )


if __name__ == "__main__":
    unittest.main()
