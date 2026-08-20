# Graph Report - MiniCPM-V-demo-Android  (2026-08-20)

## Corpus Check
- 275 files · ~112,959 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 2773 nodes · 5545 edges · 187 communities (131 shown, 56 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 335 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `2ea6be24`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MainActivity
- build_multisource_dataset.py
- llama_jni.cpp
- KnowledgeBaseActivity
- RetrievalCalibrationKey
- RagPhase
- EncryptedFileStore
- LlamaEngine
- RagDatabase
- .plan
- train.py
- ModelDownloadService
- RagMigrations
- PendingImageStateMachine
- quality_gate.py
- PendingImageViewModel
- DocumentStatus
- ModelManagerActivity
- RetrievedChunk
- PdfOcrInstrumentedTest
- LlamaEngine.kt
- BlockStructure
- ConversationArchive
- ParsedBlock
- TtsActivity
- WorkManagerRagWorkCoordinator
- KnowledgeBaseEntity
- ImportCopyWorker.kt
- HybridRetriever
- LlamaState
- EmbeddingCorpusKey
- GroundednessVerdict
- Fixture
- RagGuardInstrumentedTest
- .submitPromptToModel
- RagQueryRouterTest
- TokenSpan
- E5Embedder
- fail
- ConversationStore
- VisualResponseDecision
- RagImportNotifications.kt
- AnswerabilityVerdict
- Result
- WordCounter
- HnswIndexMetadata.kt
- VisualContextPolicy
- ChatAdapter
- ImageSourceCache
- export_onnx.py
- OoxmlSecurityTest
- RagRetrievalRequest
- IOException
- Context
- ValueError
- Java_com_example_minicpm_1v_1demo_TtsEngine_nativeTtsGenerate
- RagReviewedGenerator
- ParserInput
- AppLanguage
- .retrieve
- ParserError
- XlsxParser
- AnswerabilityClassifier
- ChunkEmbeddingEntity
- CitationRef
- MiniCPMApplication
- RuntimeException
- FakeStateQueries
- ChatMessage
- LocalGuardReplyPolicy.kt
- CalibrationCategory
- format_model_input
- RagGuardClassifier.kt
- AnswerabilityModelManifestTest
- RagTempFileCleaner
- ContentSafetyPolicyTest
- Bounded Mobile RAG Context
- FtsMatchInfo
- ByteBuffer
- DocumentEntity
- RagPromptAssembler
- VisualContextPolicy.kt
- Always-On Graphify Guidance
- DetectedFileType
- OnnxRagGuardClassifier
- RankedChunkId
- BoundedXmlHandler
- SentenceWindowEvidenceReducer
- LazyAnswerabilityClassifier
- LlamaVisualCheckpointInstrumentedTest
- RagGuardModelManifest
- FileOutputStream
- .resolve
- ContentSafetyDecision
- OcrWorker.kt
- RAG Stage UI And Review Watchdog Implementation Plan
- DocumentImportQueue
- HnswIndexMetadataTest
- build_dataset
- WelcomeAction
- RagGuardInferenceContractTest
- ContentSafetyPolicy.kt
- ExifOrientationTransform
- .rank
- DocumentStatusTransitionPolicyTest
- FileTypeDetectorTest
- KnowledgeBaseNamePolicyTest
- FloatVectorCodec
- RAG Guard 中英文多来源训练集 v3
- ContentDisplayAction
- E5ModelSpec
- RagDocumentArtifactCleanerTest
- ImageDecodePolicyTest
- RagTurnLifecycleInstrumentedTest
- ConfirmationDecision
- IllegalContentCategory
- ImageDecodePolicy
- MessageTimelineAction
- RagEvidenceAcceptancePolicy
- .maskedMeanAndNormalize
- KnowledgeBaseDocumentPresentation
- RAG Large Vector Backend Implementation Plan
- KnowledgeBaseAdapter.kt
- WelcomeSuggestionMode
- ChunkPrerequisiteDecision
- E5EmbedderInstrumentedTest.kt
- EvidenceReducerTest
- ExactAnchorMatcherTest
- RagVisualGroundingPolicyTest
- RagWorkRecoveryPolicyTest
- RagTurnTransaction
- CheckpointTestHostActivity.kt
- LocalContentSafetyClassifier
- CpuFeatures
- VisualPromptDecision
- RAG 文档删除与失败提示 Implementation Plan
- RagQueryFeatureExtractor
- RagWorkContract
- LocalGuardReplyPolicyTest
- ModelDownloadPromptPolicyTest
- RagOutputReviewPolicyTest
- BuildDatasetTest
- MainActivityUiTest
- RAG Source Lifecycle Implementation Plan
- PrivacyDataType
- Utf8TokenOffsets
- CitationValidator
- CjkBigramEncoderTest
- RagLimitsTest
- RagTempFileCleanerTest
- E5PoolingTest
- FloatVectorCodecTest
- Utf8TokenOffsetsTest
- RagGuardModelManagerTest
- PdfPageSelectionTest
- RagTurnDeliveryPolicyTest
- RagWorkContractTest
- gradlew
- Ephemeral RAG Evidence
- Answerability Cascade
- Office Quality Gate
- Dual-Head Guard Training
- CameraFileProviderTest
- CheckpointTestHostActivityInstrumentedTest
- ExampleInstrumentedTest
- MarkdownEscape
- ModelDownloadPromptPolicy
- AiMessageEditAffordanceTest
- ExampleUnitTest
- ChunkIdentityTest
- NativeLogPrivacyTest
- Local RAG Threat Model
- Serialized Context Rebuild
- Atomic Conversation Archive
- Recoverable Indexing Worker Chain
- Synthetic Guard Dataset
- HorizontalSwipeDismissPolicyTest
- ChunkWorker.kt
- RagTurnFailure
- RAG Lifecycle Pressure Matrix Implementation Plan

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 99 edges
2. `LlamaEngine` - 89 edges
3. `RetrievedChunk` - 69 edges
4. `DocumentStatus` - 63 edges
5. `DocumentEntity` - 36 edges
6. `TtsActivity` - 35 edges
7. `MiniCPMApplication` - 34 edges
8. `KnowledgeBaseEntity` - 32 edges
9. `KnowledgeBaseActivity` - 29 edges
10. `PendingImageViewModel` - 29 edges

## Surprising Connections (you probably didn't know these)
- `Single Frozen v3 Evaluation` --semantically_similar_to--> `Guard v3 Training Result`  [INFERRED] [semantically similar]
  tools/rag_guard/MULTISOURCE_TRAINING_V3.md → docs/superpowers/plans/2026-08-18-minicpm-android-unified-progress-plan.md
- `Android Native CMake Configuration` --conceptually_related_to--> `Android Build and Installation Rules`  [INFERRED]
  app/src/main/cpp/CMakeLists.txt → AGENTS.md
- `Reviewed Generation Transaction` --implements--> `Grounded RAG Fallback Policy`  [INFERRED]
  docs/superpowers/plans/2026-08-18-minicpm-android-unified-progress-plan.md → README_MODIFIED_zh.md
- `Conservative Experimental Thresholds` --conceptually_related_to--> `Grounded RAG Fallback Policy`  [INFERRED]
  tools/rag_guard/MULTISOURCE_TRAINING_V3.md → README_MODIFIED_zh.md
- `Bounded Vector Backend` --implements--> `Bounded Mobile RAG Context`  [INFERRED]
  docs/superpowers/plans/2026-08-18-minicpm-android-unified-progress-plan.md → README_MODIFIED_zh.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Experimental Guarded RAG Release Boundary** — readme_modified_zh_guard_v3_release_boundary, docs_superpowers_plans_2026_08_18_minicpm_android_unified_progress_plan_reviewed_generation_transaction, tools_rag_guard_multisource_training_v3_conservative_experimental_thresholds [INFERRED 0.95]
- **Production RAG Guard Qualification** — minicpm_v_apps_minicpm_v_demo_android_docs_execution_evidence_rag_retrieval_calibration_20260817_answerability_cascade, minicpm_v_apps_minicpm_v_demo_android_tools_rag_guard_office_quality_gate_office_quality_gate, minicpm_v_apps_minicpm_v_demo_android_tools_rag_guard_training_quantized_onnx_export [INFERRED 0.85]
- **Local RAG Evidence Lifecycle** — minicpm_v_apps_minicpm_v_demo_android_docs_architecture_adr_001_local_rag_stack_local_rag_stack, minicpm_v_apps_minicpm_v_demo_android_docs_architecture_adr_001_local_rag_stack_ephemeral_rag_evidence, minicpm_v_apps_minicpm_v_demo_android_docs_superpowers_plans_2026_08_14_android_rag_low_latency_refactor_native_checkpoint_transaction [INFERRED 0.85]

## Communities (187 total, 56 thin omitted)

### Community 0 - "MainActivity"
Cohesion: 0.10
Nodes (15): Bitmap, Bundle, ImageView, Job, RecyclerView, TextInputEditText, TextView, Uri (+7 more)

### Community 1 - "build_multisource_dataset.py"
Cohesion: 0.07
Nodes (65): _balanced(), build_balanced_rows(), _capped_documents(), _capped_prompts(), _clean(), _conversation_rows(), CorpusExample, _deduplicate_examples() (+57 more)

### Community 2 - "llama_jni.cpp"
Cohesion: 0.08
Nodes (72): assistant_turn_prefix(), chat_add_and_format(), jint, JNIEnv, JNIEXPORT, jstring, string, T (+64 more)

### Community 3 - "KnowledgeBaseActivity"
Cohesion: 0.16
Nodes (10): Failed, ImportEnqueueOutcome, KnowledgeBaseActivity, Bundle, TextView, Queued, FailedImportNotice, Button (+2 more)

### Community 4 - "RetrievalCalibrationKey"
Cohesion: 0.10
Nodes (11): CalibratedEvidenceAcceptancePolicy, CurrentRetrievalCalibration, ExactAnchorMatcher, RetrievalCalibrationKey, RetrievalCalibrationProfile, RetrievalCalibrationMetrics, RetrievalCalibrationObservation, RetrievalCalibrationResult (+3 more)

### Community 5 - "RagPhase"
Cohesion: 0.07
Nodes (23): MonotonicClock, RagLatencyLogFormatter, RagLatencySnapshot, RagLatencyTrace, RagPhase, CHECKPOINT_RESTORE, CHECKPOINT_SAVE, DENSE (+15 more)

### Community 6 - "EncryptedFileStore"
Cohesion: 0.10
Nodes (13): ConsumerProbeException, FailingInputStream, ByteArray, Context, RagEncryptionTest, EncryptedFileStore, ByteArray, T (+5 more)

### Community 8 - "RagDatabase"
Cohesion: 0.14
Nodes (6): T, RetrievalCalibrationInstrumentedTest, RagDatabase, LexicalScore, RoomLexicalEvidenceRetriever, RoomDatabase

### Community 9 - ".plan"
Cohesion: 0.10
Nodes (14): Disabled, Failed, Indexing, ModelRequired, NoEvidence, NoRetrieval, NoSelection, RagPlanningStage (+6 more)

### Community 10 - "train.py"
Cohesion: 0.09
Nodes (26): device, no_grad, Optimizer, DualHeadRagGuard, Module, Tensor, Shared multilingual encoder with task-specific three-class output heads., DualHeadRagGuardTest (+18 more)

### Community 11 - "ModelDownloadService"
Cohesion: 0.11
Nodes (16): Cancelled, Completed, Failed, Idle, Context, Intent, Job, StateFlow (+8 more)

### Community 12 - "RagMigrations"
Cohesion: 0.10
Nodes (12): RagDatabaseMigrationTest, MigratedName, RagMigrations, KnowledgeBaseNameError, EMPTY, FORBIDDEN_CHARACTER, TOO_LONG, KnowledgeBaseNamePolicy (+4 more)

### Community 13 - "PendingImageStateMachine"
Cohesion: 0.08
Nodes (14): ChatInputControls, Empty, PendingImageCancellationDisplay, CLEARING, HIDDEN, PendingImageCancellationMode, CONTEXT_RESET, USER_REMOVE (+6 more)

### Community 14 - "quality_gate.py"
Cohesion: 0.16
Nodes (21): _answerability_metrics(), assert_document_isolation(), _binary_metrics(), evaluate_quality_gate(), _groundedness_metrics(), _load_document_ids(), load_scored_jsonl(), main() (+13 more)

### Community 15 - "PendingImageViewModel"
Cohesion: 0.11
Nodes (17): AndroidViewModel, Clearing, Empty, Error, ImageMetadata, Bitmap, Flow, Job (+9 more)

### Community 16 - "DocumentStatus"
Cohesion: 0.05
Nodes (25): DocumentStatus, CANCELLED, CHUNKING, COPYING, DELETING, EMBEDDING, FAILED, INDEXING (+17 more)

### Community 17 - "ModelManagerActivity"
Cohesion: 0.07
Nodes (20): RecyclerView, TextView, ViewGroup, ModelAdapter, ViewHolder, Bundle, LinearProgressIndicator, MaterialButton (+12 more)

### Community 18 - "RetrievedChunk"
Cohesion: 0.12
Nodes (8): RagGuardClassifier, LongArray, RagGuardInput, RetrievedChunk, RagGuardClassifier, RagGuardClassifier, CitationValidatorTest, RagPromptAssemblerTest

### Community 19 - "PdfOcrInstrumentedTest"
Cohesion: 0.23
Nodes (4): ByteArray, PdfOcrInstrumentedTest, OcrAwareDocumentParser, PdfDocumentParser

### Community 20 - "LlamaEngine.kt"
Cohesion: 0.12
Nodes (9): StateFlow, ModelHistoryRole, ASSISTANT, USER, NativeCheckpoint, NativeContextDebugSnapshot, EphemeralContextEngine, SharedPreferences (+1 more)

### Community 21 - "BlockStructure"
Cohesion: 0.14
Nodes (8): Handler, Attributes, CharArray, BlockStructure, CODE, HEADING, PARAGRAPH, TABLE_ROW

### Community 22 - "ConversationArchive"
Cohesion: 0.16
Nodes (4): ConversationArchive, ConversationArchiveDiskStore, ConversationArchiveCodecTest, ByteArray

### Community 23 - "ParsedBlock"
Cohesion: 0.25
Nodes (5): ChunkConfig, ChunkDraft, DocumentChunker, ParsedBlock, DocumentChunkerTest

### Community 24 - "TtsActivity"
Cohesion: 0.06
Nodes (24): AudioRecorder, ByteArray, Bundle, IntArray, Job, LinearProgressIndicator, MaterialButton, TextInputEditText (+16 more)

### Community 25 - "WorkManagerRagWorkCoordinator"
Cohesion: 0.20
Nodes (9): Context, Intent, RagImportCancelReceiver, Flow, RagWorkCoordinator, RagWorkUiState, WorkManagerRagWorkCoordinator, BroadcastReceiver (+1 more)

### Community 26 - "KnowledgeBaseEntity"
Cohesion: 0.06
Nodes (12): RagDatabaseDaoTest, RagSchemaV2DaoTest, RagWorkRecoveryTest, ConversationRagDao, KnowledgeBaseDao, ChunkFtsEntity, CitationEntity, ConversationKnowledgeBaseCrossRef (+4 more)

### Community 27 - "ImportCopyWorker.kt"
Cohesion: 0.11
Nodes (19): CopiedSource, DocumentImporter, DocumentImportError, CANCELLED, DECLARATION_MISMATCH, DUPLICATE_CONTENT, EMPTY_SOURCE, PERSIST_PERMISSION_DENIED (+11 more)

### Community 28 - "HybridRetriever"
Cohesion: 0.41
Nodes (6): HybridRetriever, LexicalEvidenceRetriever, LexicalRetrievedChunk, FakeDense, FakeLexical, HybridRetrieverTest

### Community 29 - "LlamaState"
Cohesion: 0.17
Nodes (12): Error, Generating, Initialized, Initializing, LlamaState, LoadingModel, ModelReady, PrefillingImage (+4 more)

### Community 30 - "EmbeddingCorpusKey"
Cohesion: 0.22
Nodes (6): EmbeddingCorpusKey, ExactVectorBuffer, ExactVectorBufferCache, FloatArray, ExactVectorBufferTest, FloatArray

### Community 31 - "GroundednessVerdict"
Cohesion: 0.17
Nodes (11): GroundednessVerdict, CurrentGroundednessCalibration, ExperimentalGroundednessCalibration, GroundednessCalibrationProfile, GroundednessClassifier, GroundednessReviewTimeoutException, WatchdogGroundednessClassifier, RagReviewedGenerationTest (+3 more)

### Community 32 - "Fixture"
Cohesion: 0.12
Nodes (9): RagPromptTokenCounter, RagContextBudgeter, RagPromptTokenCounter, RagEvidenceBudget, RagEvidenceBudgeter, Fixture, RagPromptTokenCounter, RagCoordinatorTest (+1 more)

### Community 34 - ".submitPromptToModel"
Cohesion: 0.13
Nodes (4): RagPromptTokenCounter, RagPromptTokenCounter, RevealResponse, SubmitPrompt

### Community 35 - "RagQueryRouterTest"
Cohesion: 0.17
Nodes (8): RagQueryRoute, COMPLEX_RETRIEVAL, NO_RETRIEVAL, SINGLE_RETRIEVAL, RagQueryRouter, RagRouteInput, RagQueryRouterTest, RouteCase

### Community 36 - "TokenSpan"
Cohesion: 0.18
Nodes (8): E5Tokenizer, TokenSpan, validatedTokenSpans(), CodePointTokenizer, E5Tokenizer, KnowledgeBaseEntityFactoryTest, E5Tokenizer, E5Tokenizer

### Community 37 - "E5Embedder"
Cohesion: 0.08
Nodes (13): E5Embedder, Encoded, AutoCloseable, E5Tokenizer, FloatArray, LongArray, EmbeddingModelManager, AutoCloseable (+5 more)

### Community 38 - "fail"
Cohesion: 0.19
Nodes (6): fail(), ParsedBlockCodec, ByteArray, SafeOoxmlReader, LocatedLine, StrictTextSource

### Community 39 - "ConversationStore"
Cohesion: 0.10
Nodes (5): Conversation, ConversationStore, ModelHistoryText, TimelineMutation, ConversationStoreTest

### Community 40 - "VisualResponseDecision"
Cohesion: 0.14
Nodes (10): RagVisualGroundingPolicy, VisualResponseAssertion, NON_VISUAL_RESPONSE, UNCERTAIN_VISUAL_ASSERTION, VISUAL_ASSERTION, VisualResponseDecision, ALLOW, BLOCK_UNCERTAIN_ASSERTION (+2 more)

### Community 41 - "RagImportNotifications.kt"
Cohesion: 0.60
Nodes (3): Context, RagImportNotifications, ForegroundInfo

### Community 42 - "AnswerabilityVerdict"
Cohesion: 0.16
Nodes (7): AnswerabilityLabel, PARTIAL, SUPPORTED, UNSUPPORTED, AnswerabilityVerdict, RagGuardContractTest, AnswerabilityClassifierTest

### Community 43 - "Result"
Cohesion: 0.20
Nodes (8): CancelImportWorker, CoroutineWorker, EmbedWorker, CoroutineWorker, Context, Uri, Result, VideoFrameExtractor

### Community 44 - "WordCounter"
Cohesion: 0.36
Nodes (3): RagPromptTokenCounter, RagContextBudgeterTest, WordCounter

### Community 45 - "HnswIndexMetadata.kt"
Cohesion: 0.11
Nodes (17): HnswIndexManager, DigestResult, hexToBytes(), HnswIndexAdmission, HnswIndexAdmissionPolicy, HnswIndexIntegrity, HnswIndexMetadata, HnswIndexMetadataCodec (+9 more)

### Community 46 - "VisualContextPolicy"
Cohesion: 0.15
Nodes (3): StateFlow, VisualContextPolicy, VisualContextPolicyTest

### Community 47 - "ChatAdapter"
Cohesion: 0.10
Nodes (14): AiMessageViewHolder, ChatAdapter, ImageView, LinearProgressIndicator, MaterialButton, RecyclerView, TextView, View (+6 more)

### Community 48 - "ImageSourceCache"
Cohesion: 0.18
Nodes (4): ImageSourceCache, Bitmap, StoredImageThumbnailLoader, ImageSourceCacheTest

### Community 49 - "export_onnx.py"
Cohesion: 0.17
Nodes (18): build_artifact_manifest(), _export_fp32(), _load_evaluation_rows(), _load_regression_rows(), _load_trained_model(), parse_args(), Namespace, Path (+10 more)

### Community 50 - "OoxmlSecurityTest"
Cohesion: 0.13
Nodes (7): DocxParser, Attributes, CharArray, PptxParser, SlideHandler, ByteArray, OoxmlSecurityTest

### Community 51 - "RagRetrievalRequest"
Cohesion: 0.16
Nodes (10): E5InputKind, PASSAGE, QUERY, Evidence, RagEvidenceRetriever, RagRetrievalOutcome, RagRetrievalRequest, VectorEmbeddingSource (+2 more)

### Community 52 - "IOException"
Cohesion: 0.24
Nodes (4): ConversationArchiveCodec, RagImportFailureClassifier, RagImportFailureClassifierTest, IOException

### Community 53 - "Context"
Cohesion: 0.19
Nodes (3): Context, Context, ModelInfo

### Community 54 - "ValueError"
Cohesion: 0.20
Nodes (17): validate_redacted_text(), _load_jsonl(), _load_manifest(), main(), _parse_args(), Namespace, Path, Score redacted office holdout rows with the pinned ONNX guard package. (+9 more)

### Community 55 - "Java_com_example_minicpm_1v_1demo_TtsEngine_nativeTtsGenerate"
Cohesion: 0.30
Nodes (14): jint, JNIEnv, JNIEXPORT, jstring, string, vector, Java_com_example_minicpm_1v_1demo_TtsEngine_nativeInitOmni(), Java_com_example_minicpm_1v_1demo_TtsEngine_nativeOmniFree() (+6 more)

### Community 56 - "RagReviewedGenerator"
Cohesion: 0.25
Nodes (7): Accepted, ClassifierIdentityMismatchException, EmptyVisibleAnswerException, FallbackToNormalGeneration, IllegalStateException, RagReviewedGenerator, ReviewedRagGeneration

### Community 57 - "ParserInput"
Cohesion: 0.09
Nodes (11): CsvParser, DocumentParser, ParserInput, HtmlParser, MarkdownParser, ParserRegistry, TextParser, CoroutineWorker (+3 more)

### Community 58 - "AppLanguage"
Cohesion: 0.31
Nodes (6): AppLanguage, EN, ZH, Activity, Context, LocaleManager

### Community 59 - ".retrieve"
Cohesion: 0.15
Nodes (12): Attempt, Failure, HybridRetrievalUnavailableException, IllegalStateException, T, Success, Accumulator, DenseRankedHit (+4 more)

### Community 60 - "ParserError"
Cohesion: 0.14
Nodes (14): ParserError, CANCELLED, INVALID_ENCODING, MALFORMED_DOCUMENT, OCR_FAILED, PDF_CORRUPT, PDF_PAGE_LIMIT, RECORD_TOO_LARGE (+6 more)

### Community 61 - "XlsxParser"
Cohesion: 0.21
Nodes (5): Attributes, CharArray, SharedStringsHandler, SheetHandler, XlsxParser

### Community 63 - "ChunkEmbeddingEntity"
Cohesion: 0.16
Nodes (5): ChunkDao, ChunkFtsMatchInfoRow, EmbeddingCorpusStamp, ChunkEmbeddingEntity, ChunkEntity

### Community 66 - "MiniCPMApplication"
Cohesion: 0.12
Nodes (17): HybridRetrieverInstrumentedTest, MiniCPMApplication, DatabaseRagTurnStateSource, IdentityRagEvidenceReducer, RagCoordinator, RagPromptBuilder, RagRetrievalMode, ADAPTIVE (+9 more)

### Community 67 - "RuntimeException"
Cohesion: 0.32
Nodes (4): FileSource, ByteArray, RaceWinner, RuntimeException

### Community 69 - "ChatMessage"
Cohesion: 0.12
Nodes (12): DiffCallback, Bitmap, AiMessage, ChatMessage, confirmedForSubmission(), RagGenerationStage, GENERATING, ORGANIZING (+4 more)

### Community 70 - "LocalGuardReplyPolicy.kt"
Cohesion: 0.18
Nodes (9): LocalGuardReplyKind, NO_VISUAL_CONTEXT, UNCERTAIN_VISUAL_REQUEST, LocalGuardReplyPolicy, LocalResponseStreamer, PromptDestination, LOCAL_ONLY, MODEL (+1 more)

### Community 71 - "CalibrationCategory"
Cohesion: 0.12
Nodes (14): CalibrationCategory, AMOUNT, CROSS_DOCUMENT, DATE, GREETING, IDENTIFIER, RELEVANT, SIMILAR_BUT_WRONG (+6 more)

### Community 72 - "format_model_input"
Cohesion: 0.25
Nodes (9): _softmax(), _task_metrics(), TrainingDataTest, expected_calibration_error(), format_model_input(), load_jsonl(), macro_f1(), Path (+1 more)

### Community 73 - "RagGuardClassifier.kt"
Cohesion: 0.18
Nodes (9): GroundednessLabel, GROUNDED, PARTIAL, UNGROUNDED, RagOutputReviewAction, ACCEPT, REGENERATE, REJECT_WITH_LOCAL_REPLY (+1 more)

### Community 74 - "AnswerabilityModelManifestTest"
Cohesion: 0.24
Nodes (4): AnswerabilityModelManifest, AnswerabilityModelPackageVerifier, CurrentAnswerabilityModel, AnswerabilityModelManifestTest

### Community 75 - "RagTempFileCleaner"
Cohesion: 0.15
Nodes (6): RagTempFileCleaner, RagDocumentRemovalService, Context, RagImportFailureHandler, RagDocumentRemovalServiceTest, ListenableWorker

### Community 77 - "Bounded Mobile RAG Context"
Cohesion: 0.18
Nodes (12): Bounded Vector Backend, Guard v3 Training Result, Reviewed Generation Transaction, Sentence and Token Budget, Bounded Mobile RAG Context, Grounded RAG Fallback Policy, Guard v3 Release Boundary, Local RAG Experimental Pipeline (+4 more)

### Community 78 - "FtsMatchInfo"
Cohesion: 0.22
Nodes (5): FtsMatchInfo, FtsMatchInfoFormatException, ByteArray, IllegalArgumentException, SafeFtsQuery

### Community 79 - "ByteBuffer"
Cohesion: 0.18
Nodes (4): ChunkIdentity, FtsMatchInfoTest, ByteArray, ByteBuffer

### Community 80 - "DocumentEntity"
Cohesion: 0.12
Nodes (6): DocumentDao, DocumentEntity, RagDocumentArtifactCleaner, RagImportFailureData, RagImportFailureDataTest, Data

### Community 81 - "RagPromptAssembler"
Cohesion: 0.24
Nodes (4): PromptLanguage, CHINESE, ENGLISH, RagPromptAssembler

### Community 82 - "VisualContextPolicy.kt"
Cohesion: 0.25
Nodes (7): NormalizedVisualText, VisualPromptIntent, NEED_VISUAL, TEXT_ONLY, UNCERTAIN, VisualRequestDetector, VisualTextNormalizer

### Community 83 - "Always-On Graphify Guidance"
Cohesion: 0.20
Nodes (11): Android Build and Installation Rules, Graphify Completion Check, Always-On Graphify Guidance, Graphify Incremental Update, Graphify Scoped Query Protocol, Graphify Semantic Refresh Requirement, Stable Application Signing, Android ABI Configuration (+3 more)

### Community 84 - "DetectedFileType"
Cohesion: 0.16
Nodes (12): DetectedFileType, EMPTY, JPEG, OOXML_ZIP, PDF, PNG, TEXT, UNSUPPORTED_BINARY (+4 more)

### Community 85 - "OnnxRagGuardClassifier"
Cohesion: 0.30
Nodes (4): AutoCloseable, FloatArray, RagGuardClassifier, OnnxRagGuardClassifier

### Community 86 - "RankedChunkId"
Cohesion: 0.17
Nodes (10): PartitionedExactVectorRanker, ExactVectorSearchBackend, VectorEmbeddingSource, VectorSearchBackend, VectorSearchRequest, RankedChunkId, FloatArray, VectorEmbeddingSource (+2 more)

### Community 87 - "BoundedXmlHandler"
Cohesion: 0.38
Nodes (3): BoundedXmlHandler, Attributes, DefaultHandler

### Community 90 - "LlamaVisualCheckpointInstrumentedTest"
Cohesion: 0.42
Nodes (3): ByteArray, Context, LlamaVisualCheckpointInstrumentedTest

### Community 91 - "RagGuardModelManifest"
Cohesion: 0.23
Nodes (5): CurrentRagGuardModel, RagGuardModelFile, RagGuardModelManifest, RagGuardModelPackageVerifier, RagGuardModelManifestTest

### Community 92 - "FileOutputStream"
Cohesion: 0.31
Nodes (5): CachedImageSource, ImageSourceTooLargeException, ImageSourceUnreadableException, FileOutputStream, InputStream

### Community 93 - ".resolve"
Cohesion: 0.39
Nodes (5): Available, CitationSourceResolution, CitationSourceResolver, Deleted, Unavailable

### Community 94 - "ContentSafetyDecision"
Cohesion: 0.22
Nodes (7): ContentSafetyAssessment, ContentSafetyDecision, ALLOW, BLOCK, REVIEW, WARNING, ContentSafetyPolicyEngine

### Community 95 - "OcrWorker.kt"
Cohesion: 0.15
Nodes (8): RagLimits, Exception, ParserException, PdfPageSelection, CoroutineWorker, T, OcrWorker, java

### Community 96 - "RAG Stage UI And Review Watchdog Implementation Plan"
Cohesion: 0.40
Nodes (4): RAG Stage UI And Review Watchdog Implementation Plan, Task 1: Add deterministic planning stages, Task 2: Render stages without persistence, Task 3: Bound Groundedness classification

### Community 97 - "DocumentImportQueue"
Cohesion: 0.50
Nodes (3): DocumentImportQueue, Uri, SourceMetadata

### Community 99 - "build_dataset"
Cohesion: 0.39
Nodes (8): _base_case(), build_dataset(), main(), Path, Build deterministic, privacy-safe synthetic corpora for the two RAG guard heads., _row(), _split(), _write_jsonl()

### Community 100 - "WelcomeAction"
Cohesion: 0.50
Nodes (4): PickMedia, SendPrompt, TakePhoto, WelcomeAction

### Community 102 - "ContentSafetyPolicy.kt"
Cohesion: 0.29
Nodes (6): ContentSafetyDisplayPolicy, PrivacyInputChoiceAction, DELETE, IGNORE, SUBMIT, PrivacyInputConfirmationPolicy

### Community 103 - "ExifOrientationTransform"
Cohesion: 0.32
Nodes (3): ExifOrientationPolicy, ExifOrientationTransform, ExifOrientationPolicyTest

### Community 104 - ".rank"
Cohesion: 0.29
Nodes (4): ExactVectorRanker, FloatArray, VectorCandidate, ExactVectorRankerTest

### Community 108 - "FloatVectorCodec"
Cohesion: 0.38
Nodes (3): FloatVectorCodec, ByteArray, FloatArray

### Community 109 - "RAG Guard 中英文多来源训练集 v3"
Cohesion: 0.25
Nodes (7): RAG Guard 中英文多来源训练集 v3, 当前规模, 数据安全与质量, 数据来源, 本轮结果与接入状态, 构造规则, 训练环境

### Community 110 - "ContentDisplayAction"
Cohesion: 0.29
Nodes (6): ContentDisplayAction, REQUEST_PRIVACY_CONFIRMATION, SHOW_CANDIDATE, SHOW_ILLEGAL_REFUSAL, SHOW_REVIEW_FALLBACK, SHOW_VISUAL_GUARD

### Community 111 - "E5ModelSpec"
Cohesion: 0.33
Nodes (3): E5ModelSpec, FinalizeIndexWorker, CoroutineWorker

### Community 114 - "RagTurnLifecycleInstrumentedTest"
Cohesion: 0.18
Nodes (6): Context, LlamaCheckpointInstrumentedTest, Context, Job, RagTurnLifecycleInstrumentedTest, ParcelFileDescriptor

### Community 115 - "ConfirmationDecision"
Cohesion: 0.33
Nodes (5): ConfirmationDecision, CONFIRM, DECLINE, INVALID, ExplicitConfirmationParser

### Community 116 - "IllegalContentCategory"
Cohesion: 0.33
Nodes (6): IllegalContentCategory, CREDENTIAL_THEFT, EXPLOSIVES, FORGED_DOCUMENTS, FRAUD, ILLEGAL_DRUGS

### Community 118 - "MessageTimelineAction"
Cohesion: 0.40
Nodes (4): MessageTimelineAction, DELETE, EDIT, MessageTimelineActionPolicy

### Community 119 - "RagEvidenceAcceptancePolicy"
Cohesion: 0.25
Nodes (6): BasicRagEvidenceAcceptancePolicy, RagEvidenceAcceptancePolicy, AnswerabilityCalibrationProfile, CascadedEvidenceAcceptancePolicy, CurrentAnswerabilityCalibration, ExperimentalAnswerabilityCalibration

### Community 120 - ".maskedMeanAndNormalize"
Cohesion: 0.47
Nodes (3): E5Pooling, FloatArray, LongArray

### Community 121 - "KnowledgeBaseDocumentPresentation"
Cohesion: 0.43
Nodes (4): Failure, KnowledgeBaseDocumentPresentation, Processing, Uploaded

### Community 122 - "RAG Large Vector Backend Implementation Plan"
Cohesion: 0.29
Nodes (6): RAG Large Vector Backend Implementation Plan, Task 1: Extract a unified exact backend, Task 2: Define and validate the HNSW sidecar envelope, Task 3: Add the pinned native HNSW implementation, Task 4: Build, switch, and recover indexes atomically, Task 5: Benchmark and close the phase

### Community 124 - "KnowledgeBaseAdapter.kt"
Cohesion: 0.19
Nodes (7): KnowledgeBaseAdapter, KnowledgeBaseListItem, TextView, View, ViewGroup, HorizontalSwipeDismissPolicy, BaseAdapter

### Community 125 - "WelcomeSuggestionMode"
Cohesion: 0.33
Nodes (5): WelcomeSuggestionMode, TEXT_PROMPTS, VISUAL_INPUT_ACTIONS, VISUAL_PROMPTS, WelcomeSuggestionPolicy

### Community 126 - "ChunkPrerequisiteDecision"
Cohesion: 0.20
Nodes (7): ChunkPrerequisiteDecision, MODEL_REQUIRED, READY, TOKENIZER_MISMATCH, ChunkWorkPolicy, TokenizerIdentity, ChunkWorkPolicyTest

### Community 132 - "RagTurnTransaction"
Cohesion: 0.32
Nodes (5): Context, RagConversationContextInstrumentedTest, RagTurnTransaction, FakeEphemeralContextEngine, RagTurnTransactionTest

### Community 133 - "CheckpointTestHostActivity.kt"
Cohesion: 0.60
Nodes (3): CheckpointTestHostActivity, Activity, Bundle

### Community 136 - "VisualPromptDecision"
Cohesion: 0.40
Nodes (4): VisualPromptDecision, ALLOW, BLOCK_NEEDS_VISUAL, BLOCK_UNCERTAIN

### Community 139 - "RAG 文档删除与失败提示 Implementation Plan"
Cohesion: 0.33
Nodes (5): RAG 文档删除与失败提示 Implementation Plan, Task 1: 固定安全清理和同名重传的数据行为, Task 2: Make failed imports self-cleaning and observable without a RAG document row, Task 3: Add long-press deletion and swipe-dismiss failure notices, Task 4: Verify build, security boundaries and persisted project graph

### Community 148 - "RAG Source Lifecycle Implementation Plan"
Cohesion: 0.40
Nodes (4): RAG Source Lifecycle Implementation Plan, Task 1: Resolve current and deleted sources, Task 2: Connect source chips to Room lifecycle state, Task 3: Synchronize active progress and Graphify

### Community 149 - "PrivacyDataType"
Cohesion: 0.50
Nodes (4): PrivacyDataType, CHINESE_ID_CARD, MOBILE_PHONE, POSTAL_ADDRESS

### Community 165 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 166 - "Ephemeral RAG Evidence"
Cohesion: 0.50
Nodes (4): Ephemeral RAG Evidence, Hybrid Retrieval, Android Local RAG Stack, Native Checkpoint Transaction

### Community 167 - "Answerability Cascade"
Cohesion: 0.50
Nodes (4): Answerability Cascade, BM25 Cross-Corpus Drift, Retrieval Calibration Evidence, Selective Query Routing

### Community 168 - "Office Quality Gate"
Cohesion: 0.50
Nodes (4): Office Quality Gate, Real Office Data Isolation, Public Office Guard Holdout, Public Guard Prequalification

### Community 169 - "Dual-Head Guard Training"
Cohesion: 0.50
Nodes (4): Pinned Export Dependencies, Pinned Training Dependencies, Dual-Head Guard Training, Quantized ONNX Guard Export

### Community 182 - "Local RAG Threat Model"
Cohesion: 0.67
Nodes (3): Fail-Closed Integrity Validation, Local RAG Threat Model, Untrusted Document Boundary

### Community 194 - "ChunkWorker.kt"
Cohesion: 0.24
Nodes (4): E5TokenizerRegistry, E5Tokenizer, ChunkWorker, CoroutineWorker

### Community 195 - "RagTurnFailure"
Cohesion: 0.33
Nodes (6): RagTurnFailure, EVIDENCE_PROCESSING_FAILED, PROMPT_BUILD_FAILED, RETRIEVAL_UNAVAILABLE, ROUTING_UNAVAILABLE, STATE_UNAVAILABLE

### Community 197 - "RAG Lifecycle Pressure Matrix Implementation Plan"
Cohesion: 0.33
Nodes (5): RAG Lifecycle Pressure Matrix Implementation Plan, Task 1: Expose checkpoint ownership safely, Task 2: Add deterministic success/cancellation pressure, Task 3: Run real Activity lifecycle conflicts, Task 4: Close the phase

## Knowledge Gaps
- **245 isolated node(s):** `RELEVANT`, `SIMILAR_BUT_WRONG`, `UNRELATED`, `GREETING`, `IDENTIFIER` (+240 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **56 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `MainActivity` to `CitationRef`, `.refreshInputControls`, `.submitPromptToModel`, `ChatMessage`, `ConversationStore`, `LlamaEngine`, `ChatAdapter`, `ImageSourceCache`, `PendingImageViewModel`, `RagTurnLifecycleInstrumentedTest`, `ModelManagerActivity`, `ConversationArchive`, `.rebuildActiveConversationContext`, `LlamaState`?**
  _High betweenness centrality (0.160) - this node is a cross-community bridge._
- **Why does `LlamaEngine` connect `LlamaEngine` to `MainActivity`, `RuntimeException`, `RagTurnTransaction`, `EncryptedFileStore`, `VisualPromptDecision`, `VisualResponseDecision`, `VisualContextPolicy`, `ModelManagerActivity`, `RagTurnLifecycleInstrumentedTest`, `LlamaEngine.kt`, `Context`, `LlamaVisualCheckpointInstrumentedTest`, `LlamaState`?**
  _High betweenness centrality (0.093) - this node is a cross-community bridge._
- **Why does `RetrievedChunk` connect `RetrievedChunk` to `MainActivity`, `EvidenceReducerTest`, `ExactAnchorMatcherTest`, `RagVisualGroundingPolicyTest`, `RetrievalCalibrationKey`, `RagDatabase`, `.plan`, `CitationValidator`, `HybridRetriever`, `GroundednessVerdict`, `Fixture`, `RagGuardInstrumentedTest`, `RagQueryRouterTest`, `VisualResponseDecision`, `AnswerabilityVerdict`, `WordCounter`, `RagRetrievalRequest`, `RagReviewedGenerator`, `AnswerabilityClassifier`, `MiniCPMApplication`, `RagGuardClassifier.kt`, `RagPromptAssembler`, `OnnxRagGuardClassifier`, `SentenceWindowEvidenceReducer`, `LazyAnswerabilityClassifier`, `RagGuardInferenceContractTest`, `RagEvidenceAcceptancePolicy`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `MainActivity` (e.g. with `ConversationArchiveDiskStore` and `ConversationStore`) actually correct?**
  _`MainActivity` has 3 INFERRED edges - model-reasoned connections that need verification._
- **Are the 15 inferred relationships involving `RetrievedChunk` (e.g. with `.retrieve()` and `.retrieve()`) actually correct?**
  _`RetrievedChunk` has 15 INFERRED edges - model-reasoned connections that need verification._
- **Are the 36 inferred relationships involving `ValueError` (e.g. with `build_dataset()` and `_balanced()`) actually correct?**
  _`ValueError` has 36 INFERRED edges - model-reasoned connections that need verification._
- **What connects `RELEVANT`, `SIMILAR_BUT_WRONG`, `UNRELATED` to the rest of the system?**
  _245 weakly-connected nodes found - possible documentation gaps or missing edges._