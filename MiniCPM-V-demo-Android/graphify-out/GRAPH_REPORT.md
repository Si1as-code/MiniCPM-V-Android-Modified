# Graph Report - MiniCPM-V-demo-Android  (2026-08-20)

## Corpus Check
- 263 files · ~106,713 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 2626 nodes · 5229 edges · 193 communities (122 shown, 71 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 323 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b2a79c26`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MainActivity
- ValueError
- llama_jni.cpp
- KnowledgeBaseActivity
- .syntheticOfficeSuiteProducesVersionedThresholdsOnRealE5AndFts
- RagPhase
- EncryptedFileStore
- LlamaEngine
- ImportCopyWorker.kt
- RagCoordinator.kt
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
- RagTurnTransaction
- OoxmlSecurityTest
- ConversationArchive
- ParsedBlock
- TtsActivity
- WorkManagerRagWorkCoordinator
- KnowledgeBaseEntity
- PptxParser
- HybridRetriever
- LlamaState
- AnswerabilityVerdict
- GroundednessVerdict
- Fixture
- RagGuardInstrumentedTest
- .refreshInputControls
- RagQueryRouterTest
- RagImportCancelReceiver.kt
- E5Embedder
- fail
- ConversationStore
- VisualResponseDecision
- RagImportNotifications.kt
- RagRetrievalRequest
- ParserInput
- RagContextBudgeter
- TtsEngine
- VisualContextPolicy
- ChatAdapter
- ImageSourceCache
- export_onnx.py
- .retrieve
- IOException
- ConversationArchiveCodec
- Context
- score_office_holdout.py
- Java_com_example_minicpm_1v_1demo_TtsEngine_nativeTtsGenerate
- Result
- BlockStructure
- AppLanguage
- RagReviewedGenerator
- ParserError
- XlsxParser
- AnswerabilityClassifier
- ChunkDao
- ChunkWorker.kt
- .onCreate
- MiniCPMApplication
- RuntimeException
- FakeStateQueries
- ChatMessage
- LocalGuardReplyPolicy.kt
- TokenSpan
- format_model_input
- RagOutputReviewAction
- AnswerabilityModelManifestTest
- RagDocumentRemovalService
- ContentSafetyPolicyTest
- Bounded Mobile RAG Context
- AudioRecorder
- OriginalImageViewerActivity.kt
- DocumentEntity
- RagPromptAssembler
- VisualContextPolicy.kt
- Always-On Graphify Guidance
- FtsMatchInfo
- OnnxRagGuardClassifier
- .detect
- BoundedXmlHandler
- SentenceWindowEvidenceReducer
- LazyAnswerabilityClassifier
- LlamaVisualCheckpointInstrumentedTest
- RagGuardModelManifest
- ChunkPrerequisiteDecision
- LlamaEngine.kt
- ContentSafetyDecision
- CalibrationCategory
- VisualResponseAssertion
- DocumentImportQueue
- DetectedFileType
- StatusBarVisibleActivity
- FloatVectorCodec
- SyntheticOfficeCalibrationCorpus.kt
- ContentSafetyPolicy.kt
- ExifOrientationTransform
- .rank
- DocumentStatusTransitionPolicyTest
- FileTypeDetectorTest
- KnowledgeBaseNamePolicyTest
- FtsMatchInfoTest
- RAG Guard 中英文多来源训练集 v3
- ContentDisplayAction
- CascadedEvidenceAcceptancePolicy
- .initEngine
- ImageDecodePolicyTest
- LlamaCheckpointInstrumentedTest
- ConfirmationDecision
- IllegalContentCategory
- ImageDecodePolicy
- MessageTimelineAction
- ByteBuffer
- .maskedMeanAndNormalize
- KnowledgeBaseDocumentPresentation
- ExactVectorBufferTest
- .createWelcomeMessage
- KnowledgeBaseAdapter.kt
- WelcomeSuggestionMode
- RagGuardInferenceContractTest
- FinalizeIndexWorker.kt
- EvidenceReducerTest
- ExactAnchorMatcherTest
- RagVisualGroundingPolicyTest
- RagWorkRecoveryPolicyTest
- .readyEngine
- CheckpointTestHostActivity.kt
- LocalContentSafetyClassifier
- CpuFeatures
- VisualPromptDecision
- RagDocumentArtifactCleanerTest
- SafeFtsQuery
- RAG 文档删除与失败提示 Implementation Plan
- RagQueryFeatureExtractor
- RagWorkContract
- LocalGuardReplyPolicyTest
- ModelDownloadPromptPolicyTest
- RagOutputReviewPolicyTest
- RagDaos.kt
- BuildDatasetTest
- MainActivityUiTest
- EmbedWorker.kt
- PrivacyDataType
- CjkBigramEncoder
- Utf8TokenOffsets
- KnowledgeBaseDocumentPresentationTest
- CitationValidator
- WelcomeAction
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
- CancelImportWorker.kt
- MarkdownEscape
- ModelDownloadPromptPolicy
- CitationValidatorTest
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
- RagDocumentStageResourcesTest

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 95 edges
2. `LlamaEngine` - 86 edges
3. `RetrievedChunk` - 68 edges
4. `DocumentStatus` - 61 edges
5. `TtsActivity` - 35 edges
6. `MiniCPMApplication` - 34 edges
7. `KnowledgeBaseEntity` - 32 edges
8. `DocumentEntity` - 32 edges
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

## Communities (193 total, 71 thin omitted)

### Community 0 - "MainActivity"
Cohesion: 0.10
Nodes (14): Bitmap, ImageView, Job, RecyclerView, TextInputEditText, TextView, Uri, View (+6 more)

### Community 1 - "ValueError"
Cohesion: 0.06
Nodes (74): _base_case(), build_dataset(), main(), Path, Build deterministic, privacy-safe synthetic corpora for the two RAG guard heads., _row(), _split(), _write_jsonl() (+66 more)

### Community 2 - "llama_jni.cpp"
Cohesion: 0.08
Nodes (71): assistant_turn_prefix(), chat_add_and_format(), jint, JNIEnv, JNIEXPORT, jstring, string, T (+63 more)

### Community 3 - "KnowledgeBaseActivity"
Cohesion: 0.16
Nodes (10): Failed, ImportEnqueueOutcome, KnowledgeBaseActivity, Bundle, TextView, Queued, FailedImportNotice, Button (+2 more)

### Community 4 - ".syntheticOfficeSuiteProducesVersionedThresholdsOnRealE5AndFts"
Cohesion: 0.08
Nodes (13): T, RetrievalCalibrationInstrumentedTest, CalibratedEvidenceAcceptancePolicy, CurrentRetrievalCalibration, ExactAnchorMatcher, RetrievalCalibrationKey, RetrievalCalibrationProfile, RetrievalCalibrationMetrics (+5 more)

### Community 5 - "RagPhase"
Cohesion: 0.07
Nodes (23): MonotonicClock, RagLatencyLogFormatter, RagLatencySnapshot, RagLatencyTrace, RagPhase, CHECKPOINT_RESTORE, CHECKPOINT_SAVE, DENSE (+15 more)

### Community 6 - "EncryptedFileStore"
Cohesion: 0.07
Nodes (17): ConsumerProbeException, FailingInputStream, ByteArray, Context, RagEncryptionTest, EncryptedFileStore, ByteArray, T (+9 more)

### Community 8 - "ImportCopyWorker.kt"
Cohesion: 0.11
Nodes (19): CopiedSource, DocumentImporter, DocumentImportError, CANCELLED, DECLARATION_MISMATCH, DUPLICATE_CONTENT, EMPTY_SOURCE, PERSIST_PERMISSION_DENIED (+11 more)

### Community 9 - "RagCoordinator.kt"
Cohesion: 0.07
Nodes (26): BasicRagEvidenceAcceptancePolicy, Disabled, Failed, IdentityRagEvidenceReducer, Indexing, ModelRequired, NoEvidence, NoRetrieval (+18 more)

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
Cohesion: 0.15
Nodes (22): _answerability_metrics(), assert_document_isolation(), _binary_metrics(), evaluate_quality_gate(), _groundedness_metrics(), _load_document_ids(), load_scored_jsonl(), main() (+14 more)

### Community 15 - "PendingImageViewModel"
Cohesion: 0.13
Nodes (15): AndroidViewModel, Clearing, Empty, Error, Flow, Job, StateFlow, Uri (+7 more)

### Community 16 - "DocumentStatus"
Cohesion: 0.06
Nodes (23): DocumentStatus, CANCELLED, CHUNKING, COPYING, DELETING, EMBEDDING, FAILED, INDEXING (+15 more)

### Community 17 - "ModelManagerActivity"
Cohesion: 0.14
Nodes (11): RecyclerView, TextView, ViewGroup, ModelAdapter, ViewHolder, Bundle, LinearProgressIndicator, MaterialButton (+3 more)

### Community 18 - "RetrievedChunk"
Cohesion: 0.12
Nodes (9): GroundednessLabel, GROUNDED, PARTIAL, UNGROUNDED, RagGuardClassifier, LongArray, RagGuardInput, RetrievedChunk (+1 more)

### Community 20 - "RagTurnTransaction"
Cohesion: 0.18
Nodes (8): ModelHistoryRole, ASSISTANT, USER, NativeCheckpoint, EphemeralContextEngine, RagTurnTransaction, FakeEphemeralContextEngine, RagTurnTransactionTest

### Community 21 - "OoxmlSecurityTest"
Cohesion: 0.25
Nodes (3): DocxParser, ByteArray, OoxmlSecurityTest

### Community 22 - "ConversationArchive"
Cohesion: 0.16
Nodes (4): ConversationArchive, ConversationArchiveDiskStore, ConversationArchiveCodecTest, ByteArray

### Community 23 - "ParsedBlock"
Cohesion: 0.26
Nodes (5): ChunkConfig, ChunkDraft, DocumentChunker, ParsedBlock, DocumentChunkerTest

### Community 24 - "TtsActivity"
Cohesion: 0.15
Nodes (10): IntArray, Job, LinearProgressIndicator, MaterialButton, TextInputEditText, TextView, View, TtsActivity (+2 more)

### Community 25 - "WorkManagerRagWorkCoordinator"
Cohesion: 0.21
Nodes (6): Flow, RagWorkCoordinator, RagWorkUiState, WorkManagerRagWorkCoordinator, RagWorkRecovery, Operation

### Community 26 - "KnowledgeBaseEntity"
Cohesion: 0.05
Nodes (14): RagDatabaseDaoTest, RagSchemaV2DaoTest, RagWorkRecoveryTest, ConversationRagDao, KnowledgeBaseDao, RagDatabase, ChunkFtsEntity, CitationEntity (+6 more)

### Community 27 - "PptxParser"
Cohesion: 0.27
Nodes (4): Attributes, CharArray, PptxParser, SlideHandler

### Community 28 - "HybridRetriever"
Cohesion: 0.28
Nodes (10): Attempt, Failure, HybridRetriever, T, LexicalEvidenceRetriever, LexicalRetrievedChunk, Success, FakeDense (+2 more)

### Community 29 - "LlamaState"
Cohesion: 0.17
Nodes (12): Error, Generating, Initialized, Initializing, LlamaState, LoadingModel, ModelReady, PrefillingImage (+4 more)

### Community 30 - "AnswerabilityVerdict"
Cohesion: 0.13
Nodes (9): AnswerabilityLabel, PARTIAL, SUPPORTED, UNSUPPORTED, AnswerabilityVerdict, RagGuardClassifier, RagGuardContractTest, RagGuardClassifier (+1 more)

### Community 31 - "GroundednessVerdict"
Cohesion: 0.19
Nodes (9): GroundednessVerdict, CurrentGroundednessCalibration, ExperimentalGroundednessCalibration, GroundednessCalibrationProfile, GroundednessClassifier, RagReviewedGenerationTest, GroundednessClassifier, GroundednessClassifier (+1 more)

### Community 32 - "Fixture"
Cohesion: 0.15
Nodes (6): RagEvidenceBudget, SourceCountRagEvidenceBudgeter, Fixture, RagPromptTokenCounter, RagCoordinatorTest, RagPromptTokenCounter

### Community 34 - ".refreshInputControls"
Cohesion: 0.15
Nodes (3): RagPromptTokenCounter, RagPromptTokenCounter, RevealResponse

### Community 35 - "RagQueryRouterTest"
Cohesion: 0.17
Nodes (8): RagQueryRoute, COMPLEX_RETRIEVAL, NO_RETRIEVAL, SINGLE_RETRIEVAL, RagQueryRouter, RagRouteInput, RagQueryRouterTest, RouteCase

### Community 36 - "RagImportCancelReceiver.kt"
Cohesion: 0.53
Nodes (4): Context, Intent, RagImportCancelReceiver, BroadcastReceiver

### Community 37 - "E5Embedder"
Cohesion: 0.07
Nodes (16): E5Embedder, E5InputKind, PASSAGE, QUERY, Encoded, AutoCloseable, E5Tokenizer, FloatArray (+8 more)

### Community 38 - "fail"
Cohesion: 0.20
Nodes (6): fail(), ParsedBlockCodec, ByteArray, SafeOoxmlReader, LocatedLine, StrictTextSource

### Community 39 - "ConversationStore"
Cohesion: 0.09
Nodes (6): CitationRef, Conversation, ConversationStore, ModelHistoryText, TimelineMutation, ConversationStoreTest

### Community 40 - "VisualResponseDecision"
Cohesion: 0.25
Nodes (5): RagVisualGroundingPolicy, VisualResponseDecision, ALLOW, BLOCK_UNCERTAIN_ASSERTION, BLOCK_VISUAL_ASSERTION

### Community 41 - "RagImportNotifications.kt"
Cohesion: 0.60
Nodes (3): Context, RagImportNotifications, ForegroundInfo

### Community 42 - "RagRetrievalRequest"
Cohesion: 0.15
Nodes (12): EmbeddingCorpusKey, ExactVectorBuffer, ExactVectorBufferCache, FloatArray, PartitionedExactVectorRanker, Evidence, RagEvidenceRetriever, RagRetrievalOutcome (+4 more)

### Community 43 - "ParserInput"
Cohesion: 0.08
Nodes (13): CsvParser, DocumentParser, ParserInput, HtmlParser, MarkdownParser, ParserRegistry, OcrAwareDocumentParser, PdfDocumentParser (+5 more)

### Community 44 - "RagContextBudgeter"
Cohesion: 0.20
Nodes (7): RagPromptTokenCounter, RagContextBudgeter, RagPromptTokenCounter, RagEvidenceBudgeter, RagPromptTokenCounter, RagContextBudgeterTest, WordCounter

### Community 45 - "TtsEngine"
Cohesion: 0.15
Nodes (10): Error, Generating, Initializing, Context, StateFlow, LoadingModel, Ready, TtsEngine (+2 more)

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

### Community 50 - ".retrieve"
Cohesion: 0.20
Nodes (8): HybridRetrievalUnavailableException, IllegalStateException, Accumulator, DenseRankedHit, FusedRankedHit, LexicalRankedHit, ReciprocalRankFusion, ReciprocalRankFusionTest

### Community 51 - "IOException"
Cohesion: 0.17
Nodes (9): CachedImageSource, ImageSourceTooLargeException, ImageSourceUnreadableException, ImageMetadata, Bitmap, RagImportFailureClassifier, RagImportFailureClassifierTest, FileOutputStream (+1 more)

### Community 53 - "Context"
Cohesion: 0.17
Nodes (3): Context, Context, ModelInfo

### Community 54 - "score_office_holdout.py"
Cohesion: 0.20
Nodes (15): _load_jsonl(), _load_manifest(), main(), _parse_args(), Namespace, Path, Score redacted office holdout rows with the pinned ONNX guard package., score_rows() (+7 more)

### Community 55 - "Java_com_example_minicpm_1v_1demo_TtsEngine_nativeTtsGenerate"
Cohesion: 0.30
Nodes (14): jint, JNIEnv, JNIEXPORT, jstring, string, vector, Java_com_example_minicpm_1v_1demo_TtsEngine_nativeInitOmni(), Java_com_example_minicpm_1v_1demo_TtsEngine_nativeOmniFree() (+6 more)

### Community 56 - "Result"
Cohesion: 0.47
Nodes (4): Context, Uri, Result, VideoFrameExtractor

### Community 57 - "BlockStructure"
Cohesion: 0.14
Nodes (8): Handler, Attributes, CharArray, BlockStructure, CODE, HEADING, PARAGRAPH, TABLE_ROW

### Community 58 - "AppLanguage"
Cohesion: 0.31
Nodes (6): AppLanguage, EN, ZH, Activity, Context, LocaleManager

### Community 59 - "RagReviewedGenerator"
Cohesion: 0.25
Nodes (7): Accepted, ClassifierIdentityMismatchException, EmptyVisibleAnswerException, FallbackToNormalGeneration, IllegalStateException, RagReviewedGenerator, ReviewedRagGeneration

### Community 60 - "ParserError"
Cohesion: 0.09
Nodes (19): RagLimits, Exception, ParserError, CANCELLED, INVALID_ENCODING, MALFORMED_DOCUMENT, OCR_FAILED, PDF_CORRUPT (+11 more)

### Community 61 - "XlsxParser"
Cohesion: 0.21
Nodes (5): Attributes, CharArray, SharedStringsHandler, SheetHandler, XlsxParser

### Community 63 - "ChunkDao"
Cohesion: 0.22
Nodes (3): ChunkDao, ChunkEmbeddingEntity, ChunkEntity

### Community 64 - "ChunkWorker.kt"
Cohesion: 0.15
Nodes (5): RagTempFileCleaner, E5TokenizerRegistry, E5Tokenizer, ChunkWorker, CoroutineWorker

### Community 66 - "MiniCPMApplication"
Cohesion: 0.13
Nodes (12): E5EmbedderInstrumentedTest, HybridRetrieverInstrumentedTest, MiniCPMApplication, DatabaseRagTurnStateSource, RagCoordinator, RagPromptBuilder, RagRunIdFactory, RoomRagStateQueries (+4 more)

### Community 67 - "RuntimeException"
Cohesion: 0.32
Nodes (4): FileSource, ByteArray, RaceWinner, RuntimeException

### Community 69 - "ChatMessage"
Cohesion: 0.19
Nodes (8): DiffCallback, Bitmap, AiMessage, ChatMessage, confirmedForSubmission(), UserMessage, WelcomeCard, DiffUtil

### Community 70 - "LocalGuardReplyPolicy.kt"
Cohesion: 0.18
Nodes (9): LocalGuardReplyKind, NO_VISUAL_CONTEXT, UNCERTAIN_VISUAL_REQUEST, LocalGuardReplyPolicy, LocalResponseStreamer, PromptDestination, LOCAL_ONLY, MODEL (+1 more)

### Community 71 - "TokenSpan"
Cohesion: 0.18
Nodes (8): E5Tokenizer, TokenSpan, validatedTokenSpans(), CodePointTokenizer, E5Tokenizer, KnowledgeBaseEntityFactoryTest, E5Tokenizer, E5Tokenizer

### Community 72 - "format_model_input"
Cohesion: 0.25
Nodes (9): _softmax(), _task_metrics(), TrainingDataTest, expected_calibration_error(), format_model_input(), load_jsonl(), macro_f1(), Path (+1 more)

### Community 73 - "RagOutputReviewAction"
Cohesion: 0.33
Nodes (5): RagOutputReviewAction, ACCEPT, REGENERATE, REJECT_WITH_LOCAL_REPLY, RagOutputReviewPolicy

### Community 74 - "AnswerabilityModelManifestTest"
Cohesion: 0.24
Nodes (4): AnswerabilityModelManifest, AnswerabilityModelPackageVerifier, CurrentAnswerabilityModel, AnswerabilityModelManifestTest

### Community 75 - "RagDocumentRemovalService"
Cohesion: 0.23
Nodes (5): RagDocumentRemovalService, Context, RagImportFailureHandler, RagDocumentRemovalServiceTest, ListenableWorker

### Community 77 - "Bounded Mobile RAG Context"
Cohesion: 0.18
Nodes (12): Bounded Vector Backend, Guard v3 Training Result, Reviewed Generation Transaction, Sentence and Token Budget, Bounded Mobile RAG Context, Grounded RAG Fallback Policy, Guard v3 Release Boundary, Local RAG Experimental Pipeline (+4 more)

### Community 78 - "AudioRecorder"
Cohesion: 0.33
Nodes (3): AudioRecorder, ByteArray, AudioRecord

### Community 79 - "OriginalImageViewerActivity.kt"
Cohesion: 0.29
Nodes (6): Bitmap, Bundle, Context, Intent, OriginalImageViewerActivity, ImageButton

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

### Community 84 - "FtsMatchInfo"
Cohesion: 0.36
Nodes (4): FtsMatchInfo, FtsMatchInfoFormatException, ByteArray, IllegalArgumentException

### Community 85 - "OnnxRagGuardClassifier"
Cohesion: 0.30
Nodes (4): AutoCloseable, FloatArray, RagGuardClassifier, OnnxRagGuardClassifier

### Community 86 - ".detect"
Cohesion: 0.38
Nodes (3): FileTypeDetection, FileTypeDetector, ByteArray

### Community 87 - "BoundedXmlHandler"
Cohesion: 0.38
Nodes (3): BoundedXmlHandler, Attributes, DefaultHandler

### Community 90 - "LlamaVisualCheckpointInstrumentedTest"
Cohesion: 0.42
Nodes (3): ByteArray, Context, LlamaVisualCheckpointInstrumentedTest

### Community 91 - "RagGuardModelManifest"
Cohesion: 0.23
Nodes (5): CurrentRagGuardModel, RagGuardModelFile, RagGuardModelManifest, RagGuardModelPackageVerifier, RagGuardModelManifestTest

### Community 92 - "ChunkPrerequisiteDecision"
Cohesion: 0.20
Nodes (7): ChunkPrerequisiteDecision, MODEL_REQUIRED, READY, TOKENIZER_MISMATCH, ChunkWorkPolicy, TokenizerIdentity, ChunkWorkPolicyTest

### Community 93 - "LlamaEngine.kt"
Cohesion: 0.22
Nodes (5): Flow, StateFlow, NativeContextDebugSnapshot, SharedPreferences, URL

### Community 94 - "ContentSafetyDecision"
Cohesion: 0.22
Nodes (7): ContentSafetyAssessment, ContentSafetyDecision, ALLOW, BLOCK, REVIEW, WARNING, ContentSafetyPolicyEngine

### Community 95 - "CalibrationCategory"
Cohesion: 0.22
Nodes (9): CalibrationCategory, AMOUNT, CROSS_DOCUMENT, DATE, GREETING, IDENTIFIER, RELEVANT, SIMILAR_BUT_WRONG (+1 more)

### Community 96 - "VisualResponseAssertion"
Cohesion: 0.25
Nodes (5): VisualResponseAssertion, NON_VISUAL_RESPONSE, UNCERTAIN_VISUAL_ASSERTION, VISUAL_ASSERTION, VisualResponseDetector

### Community 97 - "DocumentImportQueue"
Cohesion: 0.50
Nodes (3): DocumentImportQueue, Uri, SourceMetadata

### Community 98 - "DetectedFileType"
Cohesion: 0.22
Nodes (9): DetectedFileType, EMPTY, JPEG, OOXML_ZIP, PDF, PNG, TEXT, UNSUPPORTED_BINARY (+1 more)

### Community 99 - "StatusBarVisibleActivity"
Cohesion: 0.33
Nodes (3): Bundle, StatusBarVisibleActivity, AppCompatActivity

### Community 100 - "FloatVectorCodec"
Cohesion: 0.38
Nodes (3): FloatVectorCodec, ByteArray, FloatArray

### Community 101 - "SyntheticOfficeCalibrationCorpus.kt"
Cohesion: 0.43
Nodes (4): SyntheticCalibrationCase, SyntheticCalibrationCorpus, SyntheticCalibrationDocument, SyntheticOfficeCalibrationCorpus

### Community 102 - "ContentSafetyPolicy.kt"
Cohesion: 0.29
Nodes (6): ContentSafetyDisplayPolicy, PrivacyInputChoiceAction, DELETE, IGNORE, SUBMIT, PrivacyInputConfirmationPolicy

### Community 103 - "ExifOrientationTransform"
Cohesion: 0.32
Nodes (3): ExifOrientationPolicy, ExifOrientationTransform, ExifOrientationPolicyTest

### Community 104 - ".rank"
Cohesion: 0.29
Nodes (4): ExactVectorRanker, FloatArray, VectorCandidate, ExactVectorRankerTest

### Community 109 - "RAG Guard 中英文多来源训练集 v3"
Cohesion: 0.25
Nodes (7): RAG Guard 中英文多来源训练集 v3, 当前规模, 数据安全与质量, 数据来源, 本轮结果与接入状态, 构造规则, 训练环境

### Community 110 - "ContentDisplayAction"
Cohesion: 0.29
Nodes (6): ContentDisplayAction, REQUEST_PRIVACY_CONFIRMATION, SHOW_CANDIDATE, SHOW_ILLEGAL_REFUSAL, SHOW_REVIEW_FALLBACK, SHOW_VISUAL_GUARD

### Community 111 - "CascadedEvidenceAcceptancePolicy"
Cohesion: 0.43
Nodes (4): AnswerabilityCalibrationProfile, CascadedEvidenceAcceptancePolicy, CurrentAnswerabilityCalibration, ExperimentalAnswerabilityCalibration

### Community 115 - "ConfirmationDecision"
Cohesion: 0.33
Nodes (5): ConfirmationDecision, CONFIRM, DECLINE, INVALID, ExplicitConfirmationParser

### Community 116 - "IllegalContentCategory"
Cohesion: 0.33
Nodes (6): IllegalContentCategory, CREDENTIAL_THEFT, EXPLOSIVES, FORGED_DOCUMENTS, FRAUD, ILLEGAL_DRUGS

### Community 118 - "MessageTimelineAction"
Cohesion: 0.40
Nodes (4): MessageTimelineAction, DELETE, EDIT, MessageTimelineActionPolicy

### Community 120 - ".maskedMeanAndNormalize"
Cohesion: 0.47
Nodes (3): E5Pooling, FloatArray, LongArray

### Community 121 - "KnowledgeBaseDocumentPresentation"
Cohesion: 0.43
Nodes (4): Failure, KnowledgeBaseDocumentPresentation, Processing, Uploaded

### Community 124 - "KnowledgeBaseAdapter.kt"
Cohesion: 0.19
Nodes (7): KnowledgeBaseAdapter, KnowledgeBaseListItem, TextView, View, ViewGroup, HorizontalSwipeDismissPolicy, BaseAdapter

### Community 125 - "WelcomeSuggestionMode"
Cohesion: 0.33
Nodes (5): WelcomeSuggestionMode, TEXT_PROMPTS, VISUAL_INPUT_ACTIONS, VISUAL_PROMPTS, WelcomeSuggestionPolicy

### Community 127 - "FinalizeIndexWorker.kt"
Cohesion: 0.40
Nodes (3): E5ModelSpec, FinalizeIndexWorker, CoroutineWorker

### Community 133 - "CheckpointTestHostActivity.kt"
Cohesion: 0.60
Nodes (3): CheckpointTestHostActivity, Activity, Bundle

### Community 136 - "VisualPromptDecision"
Cohesion: 0.40
Nodes (4): VisualPromptDecision, ALLOW, BLOCK_NEEDS_VISUAL, BLOCK_UNCERTAIN

### Community 139 - "RAG 文档删除与失败提示 Implementation Plan"
Cohesion: 0.33
Nodes (5): RAG 文档删除与失败提示 Implementation Plan, Task 1: 固定安全清理和同名重传的数据行为, Task 2: Make failed imports self-cleaning and observable without a RAG document row, Task 3: Add long-press deletion and swipe-dismiss failure notices, Task 4: Verify build, security boundaries and persisted project graph

### Community 149 - "PrivacyDataType"
Cohesion: 0.50
Nodes (4): PrivacyDataType, CHINESE_ID_CARD, MOBILE_PHONE, POSTAL_ADDRESS

### Community 154 - "WelcomeAction"
Cohesion: 0.33
Nodes (4): PickMedia, SendPrompt, TakePhoto, WelcomeAction

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

## Knowledge Gaps
- **223 isolated node(s):** `RELEVANT`, `SIMILAR_BUT_WRONG`, `UNRELATED`, `GREETING`, `IDENTIFIER` (+218 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **71 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `MainActivity` to `.onCreate`, `.refreshInputControls`, `StatusBarVisibleActivity`, `ChatMessage`, `ConversationStore`, `LlamaEngine`, `ChatAdapter`, `ImageSourceCache`, `PendingImageViewModel`, `OriginalImageViewerActivity.kt`, `ConversationArchive`, `WelcomeAction`, `.createWelcomeMessage`, `LlamaState`?**
  _High betweenness centrality (0.187) - this node is a cross-community bridge._
- **Why does `RetrievedChunk` connect `RetrievedChunk` to `MainActivity`, `EvidenceReducerTest`, `ExactAnchorMatcherTest`, `RagVisualGroundingPolicyTest`, `.syntheticOfficeSuiteProducesVersionedThresholdsOnRealE5AndFts`, `RagCoordinator.kt`, `CitationValidator`, `HybridRetriever`, `AnswerabilityVerdict`, `GroundednessVerdict`, `Fixture`, `RagGuardInstrumentedTest`, `RagQueryRouterTest`, `VisualResponseDecision`, `RagRetrievalRequest`, `RagContextBudgeter`, `CitationValidatorTest`, `RagReviewedGenerator`, `AnswerabilityClassifier`, `MiniCPMApplication`, `RagPromptAssembler`, `OnnxRagGuardClassifier`, `SentenceWindowEvidenceReducer`, `LazyAnswerabilityClassifier`, `CascadedEvidenceAcceptancePolicy`, `RagGuardInferenceContractTest`?**
  _High betweenness centrality (0.134) - this node is a cross-community bridge._
- **Why does `LlamaEngine` connect `LlamaEngine` to `MainActivity`, `RuntimeException`, `.readyEngine`, `EncryptedFileStore`, `VisualPromptDecision`, `VisualResponseDecision`, `VisualContextPolicy`, `ModelManagerActivity`, `LlamaCheckpointInstrumentedTest`, `LlamaState`, `RagTurnTransaction`, `Context`, `LlamaVisualCheckpointInstrumentedTest`, `LlamaEngine.kt`?**
  _High betweenness centrality (0.094) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `MainActivity` (e.g. with `ConversationArchiveDiskStore` and `ConversationStore`) actually correct?**
  _`MainActivity` has 3 INFERRED edges - model-reasoned connections that need verification._
- **Are the 15 inferred relationships involving `RetrievedChunk` (e.g. with `.retrieve()` and `.retrieve()`) actually correct?**
  _`RetrievedChunk` has 15 INFERRED edges - model-reasoned connections that need verification._
- **Are the 36 inferred relationships involving `ValueError` (e.g. with `build_dataset()` and `_balanced()`) actually correct?**
  _`ValueError` has 36 INFERRED edges - model-reasoned connections that need verification._
- **What connects `RELEVANT`, `SIMILAR_BUT_WRONG`, `UNRELATED` to the rest of the system?**
  _223 weakly-connected nodes found - possible documentation gaps or missing edges._