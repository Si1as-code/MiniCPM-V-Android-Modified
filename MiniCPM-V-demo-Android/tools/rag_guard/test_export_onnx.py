import hashlib
import tempfile
import unittest
from pathlib import Path

from tools.rag_guard.export_onnx import (
    PER_CHANNEL_QUANTIZATION,
    QUANTIZED_OP_TYPES,
    build_artifact_manifest,
    quantization_passes,
)


class ExportOnnxTest(unittest.TestCase):
    def test_quantization_uses_the_regression_safe_per_tensor_mode(self) -> None:
        self.assertFalse(PER_CHANNEL_QUANTIZATION)

    def test_quantization_includes_the_large_token_embedding_gather(self) -> None:
        self.assertIn("Gather", QUANTIZED_OP_TYPES)

    def test_manifest_pins_model_contract_size_and_sha256(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            model = Path(directory) / "model.int8.onnx"
            model.write_bytes(b"quantized-model")
            manifest = build_artifact_manifest(
                model_path=model,
                tokenizer_sha256="a" * 64,
                metrics={"agreement": 1.0},
                max_tokens=256,
            )

        self.assertEqual(manifest["files"]["model.int8.onnx"]["bytes"], 15)
        self.assertEqual(
            manifest["files"]["model.int8.onnx"]["sha256"],
            hashlib.sha256(b"quantized-model").hexdigest(),
        )
        self.assertEqual(
            manifest["inputs"],
            {
                "input_ids": "int64[batch,sequence]",
                "attention_mask": "int64[batch,sequence]",
                "task_ids": "int64[batch]",
            },
        )
        self.assertEqual(manifest["output"], {"logits": "float32[batch,3]"})

    def test_quantization_gate_requires_alignment_quality_and_size(self) -> None:
        self.assertTrue(
            quantization_passes(
                label_agreement=1.0,
                largest_macro_f1_drop=0.0,
                int8_bytes=120,
                fp32_bytes=480,
            )
        )
        self.assertFalse(
            quantization_passes(
                label_agreement=0.99,
                largest_macro_f1_drop=0.0,
                int8_bytes=120,
                fp32_bytes=480,
            )
        )
        self.assertFalse(
            quantization_passes(
                label_agreement=1.0,
                largest_macro_f1_drop=0.011,
                int8_bytes=120,
                fp32_bytes=480,
            )
        )


if __name__ == "__main__":
    unittest.main()
