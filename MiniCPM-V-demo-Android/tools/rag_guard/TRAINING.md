# RAG guard training

The training tool fine-tunes one multilingual encoder with two independent three-class heads:

- Answerability: `SUPPORTED / PARTIAL / UNSUPPORTED`
- Groundedness: `GROUNDED / PARTIAL / UNGROUNDED`

The model input is `query + evidence` for Answerability and `query + evidence + answer` for
Groundedness. Raw text is never written to the training log.

Install the pinned dependencies into the selected Conda environment, then run:

```bash
python -m pip install -r tools/rag_guard/requirements-train.txt
python -m tools.rag_guard.train \
  --model intfloat/multilingual-e5-small \
  --data-dir tools/rag_guard/data/generated \
  --output-dir runs/rag-guard-dual-head \
  --epochs 4 \
  --batch-size 16 \
  --eval-batch-size 32 \
  --gradient-accumulation 2 \
  --max-length 256 \
  --learning-rate 2e-5 \
  --bf16
```

The primary selection score is the mean macro-F1 of both heads:

$$
S = \frac{F1_{answerability} + F1_{groundedness}}{2}
$$

When two checkpoints have the same score, the checkpoint with the lower mean expected
calibration error is retained. The output directory contains the Safetensors checkpoint,
tokenizer, encoder configuration, manifest, aggregate metrics, and no source documents.

The current generated corpus is suitable for validating the pipeline and label contract. A
perfect result on this structurally regular synthetic corpus is not evidence of production
quality; anonymized real-distribution regression data is still required before runtime enablement.

## Export the Android model package

Install the separate, pinned export dependencies into the same selected Conda environment:

```bash
python -m pip install -r tools/rag_guard/requirements-export.txt
python -m tools.rag_guard.export_onnx \
  --checkpoint-dir runs/rag-guard-dual-head \
  --base-model /path/to/multilingual-e5-small \
  --data-dir tools/rag_guard/data/generated \
  --regression-path tools/rag_guard/data/regression_seeds.jsonl \
  --output-dir runs/rag-guard-dual-head-onnx \
  --tokenizer-sha256 3396f311d68a8ee4351c0949ab2626543334c5566d7f8ea17b026952ac14d0fe
```

The exporter produces one shared-encoder, dual-head ONNX model. `task_ids=0` selects
Answerability and `task_ids=1` selects Groundedness. It dynamically quantizes `MatMul`, `Gemm`,
and `Gather` weights to per-tensor INT8, validates the ONNX I/O contract, compares PyTorch,
FP32 ONNX, and INT8 ONNX predictions, and writes `manifest.json` only when every quality gate
passes:

- INT8/FP32 label agreement is at least 99.5%.
- The largest macro-F1 drop is no more than 0.01.
- The INT8 file is no more than 40% of the FP32 file size.

The validated 2026-08-18 export is 118,169,267 bytes with SHA-256
`45d42125648c169a19697ce8b64f6883e63c2d8a45fd666c73bf163a3c59e097`. Its compression ratio
is 0.2513, label agreement is 0.9984, and the measured macro-F1 drop is 0.0 on the calibration,
test, and test-only regression sets. These numbers validate export equivalence, not production
accuracy; Android integration and anonymized real-distribution evaluation remain required.
