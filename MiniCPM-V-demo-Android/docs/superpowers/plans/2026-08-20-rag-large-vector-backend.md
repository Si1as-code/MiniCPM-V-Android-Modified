# RAG Large Vector Backend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Keep exact retrieval as the correctness oracle while adding a bounded, recoverable HNSW backend for knowledge bases larger than 5,000 chunks.

**Architecture:** `RoomDenseEvidenceRetriever` owns query embedding and before/after corpus-stamp validation, then delegates ranking to a `VectorSearchBackend`. The initial backend preserves the current cached exact and paged exact paths. A later selector uses an encrypted HNSW sidecar only when its header, model SHA, corpus key, hash, generation, and RSS budget are valid; every invalid or unavailable sidecar falls back to paged exact search and schedules rebuild.

**Tech Stack:** Kotlin, Coroutines, Room/SQLCipher, JNI/C++17, pinned hnswlib 0.9.0, AES-256-GCM file storage, WorkManager, JUnit 4, Android instrumentation.

> **Starting state 2026-08-20:** `ExactVectorBuffer`, a 5,000-chunk cache, 1,000-row paged exact ranking, stable score/chunk-ID ordering, and before/after `EmbeddingCorpusStamp` validation already exist. The unified backend contract, HNSW sidecar, atomic generation switch, corruption recovery, and 1k/5k/20k benchmark do not yet exist.

> **Implementation status 2026-08-20:** Task 1 and the validation portion of Task 2 are complete. `RoomDenseEvidenceRetriever` now delegates ranking through `VectorSearchBackend`; `ExactVectorSearchBackend` preserves the 5,000-chunk cache and 1,000-row paged fallback. `HnswIndexMetadataCodec`, hashed managed paths, single-pass length/SHA-256 verification, strict UTF-8 decoding, corpus admission, and the 10% RSS gate are implemented and tested. No HNSW dependency or sidecar file has been added yet; authenticated atomic publication remains grouped with Task 4 so metadata and payload cannot diverge.

---

### Task 1: Extract a unified exact backend

**Files:**
- Create: `app/src/main/java/com/example/minicpm_v_demo/rag/index/VectorSearchBackend.kt`
- Create: `app/src/test/java/com/example/minicpm_v_demo/rag/index/VectorSearchBackendTest.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/rag/retrieval/RoomDenseEvidenceRetriever.kt`

- [x] **Step 1: Write RED contract tests**

Define tests against the wished-for API:

```kotlin
data class VectorSearchRequest(
    val corpusKey: EmbeddingCorpusKey,
    val query: FloatArray,
    val limit: Int,
)

interface VectorEmbeddingSource {
    suspend fun loadAll(): List<ChunkEmbeddingEntity>
    suspend fun loadPage(offset: Int, pageSize: Int): List<ChunkEmbeddingEntity>
}

interface VectorSearchBackend {
    suspend fun search(
        request: VectorSearchRequest,
        source: VectorEmbeddingSource,
    ): List<RankedChunkId>
}
```

The small-corpus test must load all vectors once and reuse the exact cache. The oversized-corpus test must never call `loadAll`, must page in stable chunk-ID order, and must equal `ExactVectorBuffer` top-k including tie ordering.

- [x] **Step 2: Run RED**

Run `./gradlew :app:testDebugUnitTest --tests '*VectorSearchBackendTest' -x buildGgmlCpu_v86`. Expected: compilation fails because the contract does not exist.

- [x] **Step 3: Implement exact search behind the contract**

Create `ExactVectorSearchBackend` with `maximumCachedChunks = 5_000` and `partitionChunks = 1_000`. Validate positive limits, query dimension through `ExactVectorBuffer`, page offsets monotonically, and merge partitions with `PartitionedExactVectorRanker`.

- [x] **Step 4: Route Room retrieval through the backend**

Keep `findReadyEmbeddingStamp()` before and after search in `RoomDenseEvidenceRetriever`. Build a DAO-backed `VectorEmbeddingSource`; do not move stale-generation acceptance into the backend.

- [x] **Step 5: Run GREEN**

Run the focused backend tests, all retrieval tests, and the full JVM suite.

### Task 2: Define and validate the HNSW sidecar envelope

**Files:**
- Create: `app/src/main/java/com/example/minicpm_v_demo/rag/index/HnswIndexMetadata.kt`
- Create: `app/src/main/java/com/example/minicpm_v_demo/rag/index/HnswIndexManager.kt`
- Create: `app/src/test/java/com/example/minicpm_v_demo/rag/index/HnswIndexMetadataTest.kt`

- [x] **Step 1: Write RED metadata tests**

Cover magic, format version, dimension `384`, model SHA-256, sorted knowledge-base IDs, corpus version, embedding count, maximum update time, chunk-ID sum, plaintext index length, plaintext SHA-256, and build generation. Reject truncation, extra bytes, integer overflow, non-finite sizes, mismatched corpus keys, and paths outside `noBackupFilesDir/rag/index`.

- [ ] **Step 2: Implement a bounded binary envelope.** [Validation complete] The bounded codec and strict reader are complete; authenticated `.part` publication is intentionally deferred to Task 4 so metadata and payload switch as one generation.

Use fixed-width big-endian integers and bounded UTF-8 fields. The metadata file and encrypted HNSW payload must be written to same-directory `.part` files, `fsync`ed, verified, then atomically renamed. Room vectors remain the source of truth.

- [x] **Step 3: Add RSS admission policy**

Estimate HNSW resident bytes before opening. Admit HNSW only when the estimate is at most $10\%$ of the application memory budget; otherwise return paged-exact fallback without opening the sidecar.

- [x] **Step 4: Run GREEN**

Run metadata, path-boundary, truncation, hash, and budget tests.

### Task 3: Add the pinned native HNSW implementation

**Files:**
- Create: `app/src/main/cpp/third_party/hnswlib/`
- Create: `app/src/main/cpp/rag_hnsw_jni.cpp`
- Create: `app/src/main/java/com/example/minicpm_v_demo/rag/index/HnswIndex.kt`
- Modify: `app/src/main/cpp/CMakeLists.txt`
- Create: `app/src/androidTest/java/com/example/minicpm_v_demo/rag/index/HnswIndexInstrumentedTest.kt`

- [ ] **Step 1: Vendor and verify hnswlib**

Pin hnswlib 0.9.0 to an audited upstream commit, retain LICENSE/NOTICE, and record the source archive SHA-256. Do not download or update it implicitly during Gradle builds.

- [ ] **Step 2: Write native RED tests**

Cover create/add/search/save/load/close, duplicate and negative labels, wrong dimension, NaN/Infinity, truncated files, closed handles, double close, concurrent search/close, and top-k deterministic tie handling.

- [ ] **Step 3: Implement the JNI boundary**

Use cosine space with `M=16`, `efConstruction=100`, and `efSearch=48` as the first measured profile. Validate every handle, array length, finite float, label, top-k, and dedicated-directory canonical path. Catch every C++ exception and translate it to a stable Java exception; no exception may cross JNI.

- [ ] **Step 4: Verify recall and memory safety**

Compare HNSW against exact top-k and require $\mathrm{Recall@10} \ge 0.95$. Run repeated open/search/close and corruption cases; no leaked handle or stale label is allowed.

### Task 4: Build, switch, and recover indexes atomically

**Files:**
- Modify: `app/src/main/java/com/example/minicpm_v_demo/rag/work/FinalizeIndexWorker.kt`
- Create: `app/src/main/java/com/example/minicpm_v_demo/rag/work/VectorIndexWorker.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/rag/work/RagWorkCoordinator.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/rag/retrieval/RoomDenseEvidenceRetriever.kt`
- Create: `app/src/androidTest/java/com/example/minicpm_v_demo/rag/index/VectorIndexRecoveryInstrumentedTest.kt`

- [ ] **Step 1: Build from a frozen corpus stamp**

Read embeddings in chunk-ID pages, build a new sidecar generation, then re-read the stamp. If the stamp changed, discard the new files and retry later; never publish a mixed generation.

- [ ] **Step 2: Publish atomically**

Encrypt and verify the new sidecar, atomically rename payload and metadata, then mark the document READY only after every embedding and the published index generation agree.

- [ ] **Step 3: Fail safely during query**

On missing, stale, corrupt, oversized, or memory-rejected HNSW, use paged exact search for that request and enqueue one uniquely named rebuild. Never return results from an old generation.

- [ ] **Step 4: Run recovery matrix**

Force-stop during build, encryption, rename, metadata publish, and finalization. Repeated enqueue must converge to one valid generation with no plaintext sidecar left behind.

### Task 5: Benchmark and close the phase

**Files:**
- Create: `app/src/androidTest/java/com/example/minicpm_v_demo/rag/index/VectorSearchBackendInstrumentedTest.kt`
- Modify: `docs/superpowers/plans/2026-08-18-minicpm-android-unified-progress-plan.md`
- Modify: `docs/architecture/ADR-001-local-rag-stack.md`
- Update: `graphify-out/`

- [ ] **Step 1: Generate deterministic 1k/5k/20k corpora**

Use normalized 384-dimensional vectors with fixed seeds, duplicated-score ties, clustered near neighbours, and unrelated distractors. Exact search is the oracle.

- [ ] **Step 2: Measure quality and cost**

Record $\mathrm{Recall@10}$, P50/P95 latency, index build time, encrypted file size, and RSS for exact-cache, paged-exact, and HNSW modes.

- [ ] **Step 3: Enforce release gates**

Require $\mathrm{Recall@10} \ge 0.95$, no stale-generation result, no plaintext sidecar, no handle leak, and successful paged-exact fallback for every rejected sidecar.

- [ ] **Step 4: Run full verification and update Graphify**

Run JVM tests, native build, focused instrumentation, Debug APK assembly, installation-signature verification, and `graphify update .`.
