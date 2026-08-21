# Graph Report - MiniCPM-V-demo-Android  (2026-08-21)

## Corpus Check
- 286 files · ~124,724 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 3143 nodes · 6280 edges · 195 communities (135 shown, 60 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 352 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `43286fdb`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MainActivity
- build_multisource_dataset.py
- llama_jni.cpp
- KnowledgeBaseActivity
- RetrievalCalibrationKey
- RagPhase
- ConversationArchive
- LlamaEngine
- RagDatabase
- .plan
- export_onnx.py
- ModelDownloadService
- HnswIndex
- PendingImageStateMachine
- quality_gate.py
- PendingImageViewModel
- DocumentStatus
- ModelManagerActivity
- RetrievedChunk
- PdfOcrInstrumentedTest
- LlamaEngine.kt
- OoxmlSecurityTest
- RagWorkRecoveryTest
- ParsedBlock
- TtsActivity
- WorkManagerRagWorkCoordinator
- KnowledgeBaseEntity
- EncryptedFileStore
- RagRetrievalRequest
- LlamaState
- EmbeddingCorpusKey
- GroundednessVerdict
- Fixture
- RagGuardInstrumentedTest
- .refreshInputControls
- RagQueryRouterTest
- TokenSpan
- E5Embedder
- fail
- ConversationStore
- VisualResponseDecision
- RagImportNotifications.kt
- AnswerabilityVerdict
- Result
- RagContextBudgeter
- HnswIndexMetadata.kt
- VisualContextPolicy
- ChatAdapter
- ImageSourceCache
- HierarchicalNSW
- CascadedEvidenceAcceptancePolicy
- VectorEmbeddingSource
- IOException
- Context
- ValueError
- Java_com_example_minicpm_1v_1demo_TtsEngine_nativeTtsGenerate
- .attempt
- ParserInput
- AppLanguage
- DenseRankedHit
- ParserError
- XlsxParser
- AnswerabilityClassifier
- ChunkEmbeddingEntity
- CitationRef
- PptxParser
- MiniCPMApplication
- RuntimeException
- FakeStateQueries
- ChatMessage
- LocalGuardReplyPolicy.kt
- KnowledgeBaseDocumentPresentationTest
- rag_hnsw_jni.cpp
- RagGuardClassifier.kt
- AnswerabilityModelManifestTest
- RagDocumentRemovalService
- ContentSafetyPolicyTest
- Bounded Mobile RAG Context
- RecordingSource
- RagPromptTokenCounter
- DocumentEntity
- RagPromptAssembler
- VisualContextPolicy.kt
- Always-On Graphify Guidance
- DetectedFileType
- OnnxRagGuardClassifier
- RankedChunkId
- BoundedXmlHandler
- .handleSelectedVideo
- LazyAnswerabilityClassifier
- LlamaVisualCheckpointInstrumentedTest
- RagGuardModelManifest
- public_office_dataset.py
- MultiVectorSearchStopCondition
- ContentSafetyDecision
- StoredImageThumbnailLoader.kt
- RAG Stage UI And Review Watchdog Implementation Plan
- DocumentImportQueue
- HnswIndexMetadataTest
- build_dataset
- WelcomeAction
- E5EmbedderInstrumentedTest.kt
- ContentSafetyPolicy.kt
- ExifOrientationTransform
- EmbeddingModelManager
- DocumentStatusTransitionPolicyTest
- FileTypeDetectorTest
- KnowledgeBaseNamePolicyTest
- FloatVectorCodec
- RAG Guard 中英文多来源训练集 v3
- ContentDisplayAction
- E5ModelSpec
- BruteforceSearch
- ImageDecodePolicyTest
- RagTurnLifecycleInstrumentedTest
- ConfirmationDecision
- IllegalContentCategory
- ImageDecodePolicy
- MessageTimelineAction
- .maskedMeanAndNormalize
- RAG Large Vector Backend Implementation Plan
- KnowledgeBaseAdapter.kt
- WelcomeSuggestionMode
- ChunkPrerequisiteDecision
- SpaceInterface
- EvidenceReducerTest
- ExactAnchorMatcherTest
- RagVisualGroundingPolicyTest
- RagWorkRecoveryPolicyTest
- RagTurnTransaction
- CheckpointTestHostActivity.kt
- LocalContentSafetyClassifier
- CpuFeatures
- VisualPromptDecision
- MultiVectorInnerProductSpace
- hnswlib.h
- RAG 文档删除与失败提示 Implementation Plan
- RagQueryFeatureExtractor
- LocalGuardReplyPolicyTest
- ModelDownloadPromptPolicyTest
- RagOutputReviewPolicyTest
- BuildDatasetTest
- MainActivityUiTest
- RAG Source Lifecycle Implementation Plan
- PrivacyDataType
- Utf8TokenOffsets
- space_ip.h
- CitationValidator
- AlgorithmInterface
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
- space_l2.h
- ChunkWorker.kt
- RagTurnFailure
- RAG Lifecycle Pressure Matrix Implementation Plan
- BaseSearchStopCondition
- InnerProductSpace
- RagImportCancelReceiver.kt
- HnswIndexInstrumentedTest
- UPSTREAM.md

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 99 edges
2. `HierarchicalNSW` - 89 edges
3. `LlamaEngine` - 89 edges
4. `RetrievedChunk` - 69 edges
5. `DocumentStatus` - 63 edges
6. `DocumentEntity` - 36 edges
7. `TtsActivity` - 35 edges
8. `MiniCPMApplication` - 34 edges
9. `KnowledgeBaseEntity` - 32 edges
10. `KnowledgeBaseActivity` - 29 edges

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

## Communities (195 total, 60 thin omitted)

### Community 0 - "MainActivity"
Cohesion: 0.08
Nodes (13): Bundle, ImageView, Job, RecyclerView, TextInputEditText, TextView, View, MainActivity (+5 more)

### Community 1 - "build_multisource_dataset.py"
Cohesion: 0.13
Nodes (33): _balanced(), build_balanced_rows(), _capped_documents(), _capped_prompts(), _clean(), _conversation_rows(), CorpusExample, _deduplicate_examples() (+25 more)

### Community 2 - "llama_jni.cpp"
Cohesion: 0.08
Nodes (72): assistant_turn_prefix(), chat_add_and_format(), jint, jlong, JNIEnv, JNIEXPORT, jobject, jstring (+64 more)

### Community 3 - "KnowledgeBaseActivity"
Cohesion: 0.11
Nodes (12): Failed, ImportEnqueueOutcome, KnowledgeBaseActivity, Bundle, TextView, Queued, FailedImportNotice, RagImportFailureClassifier (+4 more)

### Community 4 - "RetrievalCalibrationKey"
Cohesion: 0.05
Nodes (26): CalibrationCategory, AMOUNT, CROSS_DOCUMENT, DATE, GREETING, IDENTIFIER, RELEVANT, SIMILAR_BUT_WRONG (+18 more)

### Community 5 - "RagPhase"
Cohesion: 0.07
Nodes (23): MonotonicClock, RagLatencyLogFormatter, RagLatencySnapshot, RagLatencyTrace, RagPhase, CHECKPOINT_RESTORE, CHECKPOINT_SAVE, DENSE (+15 more)

### Community 6 - "ConversationArchive"
Cohesion: 0.17
Nodes (5): ConversationArchive, ConversationArchiveDiskStore, Conversation, ConversationArchiveCodecTest, ByteArray

### Community 8 - "RagDatabase"
Cohesion: 0.11
Nodes (9): T, RetrievalCalibrationInstrumentedTest, RagDatabase, E5InputKind, PASSAGE, QUERY, LexicalScore, RoomLexicalEvidenceRetriever (+1 more)

### Community 9 - ".plan"
Cohesion: 0.09
Nodes (16): BasicRagEvidenceAcceptancePolicy, Disabled, Failed, Indexing, ModelRequired, NoEvidence, NoRetrieval, NoSelection (+8 more)

### Community 10 - "export_onnx.py"
Cohesion: 0.06
Nodes (53): device, no_grad, Optimizer, build_artifact_manifest(), _export_fp32(), _load_evaluation_rows(), _load_regression_rows(), _load_trained_model() (+45 more)

### Community 11 - "ModelDownloadService"
Cohesion: 0.11
Nodes (16): Cancelled, Completed, Failed, Idle, Context, Intent, Job, StateFlow (+8 more)

### Community 12 - "HnswIndex"
Cohesion: 0.06
Nodes (17): RagDatabaseMigrationTest, MigratedName, RagMigrations, HnswIndex, HnswNative, AutoCloseable, FloatArray, NativeHnswSearchResult (+9 more)

### Community 13 - "PendingImageStateMachine"
Cohesion: 0.08
Nodes (14): ChatInputControls, Empty, PendingImageCancellationDisplay, CLEARING, HIDDEN, PendingImageCancellationMode, CONTEXT_RESET, USER_REMOVE (+6 more)

### Community 14 - "quality_gate.py"
Cohesion: 0.17
Nodes (21): _answerability_metrics(), assert_document_isolation(), _binary_metrics(), evaluate_quality_gate(), _groundedness_metrics(), _load_document_ids(), load_scored_jsonl(), main() (+13 more)

### Community 15 - "PendingImageViewModel"
Cohesion: 0.11
Nodes (17): AndroidViewModel, Clearing, Empty, Error, ImageMetadata, Bitmap, Flow, Job (+9 more)

### Community 16 - "DocumentStatus"
Cohesion: 0.05
Nodes (24): DocumentStatus, CANCELLED, CHUNKING, COPYING, DELETING, EMBEDDING, FAILED, INDEXING (+16 more)

### Community 17 - "ModelManagerActivity"
Cohesion: 0.08
Nodes (19): RecyclerView, TextView, ViewGroup, ModelAdapter, ViewHolder, Bundle, LinearProgressIndicator, MaterialButton (+11 more)

### Community 18 - "RetrievedChunk"
Cohesion: 0.10
Nodes (7): RagGuardClassifier, LongArray, RagGuardInput, RetrievedChunk, RagGuardInferenceContractTest, CitationValidatorTest, RagPromptAssemblerTest

### Community 20 - "LlamaEngine.kt"
Cohesion: 0.12
Nodes (9): StateFlow, ModelHistoryRole, ASSISTANT, USER, NativeCheckpoint, NativeContextDebugSnapshot, EphemeralContextEngine, SharedPreferences (+1 more)

### Community 21 - "OoxmlSecurityTest"
Cohesion: 0.10
Nodes (11): DocxParser, Handler, Attributes, CharArray, BlockStructure, CODE, HEADING, PARAGRAPH (+3 more)

### Community 23 - "ParsedBlock"
Cohesion: 0.26
Nodes (5): ChunkConfig, ChunkDraft, DocumentChunker, ParsedBlock, DocumentChunkerTest

### Community 24 - "TtsActivity"
Cohesion: 0.06
Nodes (24): AudioRecorder, ByteArray, Bundle, IntArray, Job, LinearProgressIndicator, MaterialButton, TextInputEditText (+16 more)

### Community 25 - "WorkManagerRagWorkCoordinator"
Cohesion: 0.32
Nodes (5): Flow, RagWorkCoordinator, RagWorkUiState, WorkManagerRagWorkCoordinator, Operation

### Community 26 - "KnowledgeBaseEntity"
Cohesion: 0.06
Nodes (11): RagDatabaseDaoTest, RagSchemaV2DaoTest, ConversationRagDao, KnowledgeBaseDao, ChunkFtsEntity, CitationEntity, ConversationKnowledgeBaseCrossRef, ConversationRagStateEntity (+3 more)

### Community 27 - "EncryptedFileStore"
Cohesion: 0.05
Nodes (32): ConsumerProbeException, FailingInputStream, ByteArray, Context, RagEncryptionTest, EncryptedFileStore, ByteArray, T (+24 more)

### Community 28 - "RagRetrievalRequest"
Cohesion: 0.20
Nodes (13): Evidence, RagEvidenceRetriever, RagRetrievalOutcome, RagRetrievalRequest, HybridRetrievalUnavailableException, HybridRetriever, IllegalStateException, LexicalEvidenceRetriever (+5 more)

### Community 29 - "LlamaState"
Cohesion: 0.17
Nodes (12): Error, Generating, Initialized, Initializing, LlamaState, LoadingModel, ModelReady, PrefillingImage (+4 more)

### Community 30 - "EmbeddingCorpusKey"
Cohesion: 0.24
Nodes (6): EmbeddingCorpusKey, ExactVectorBuffer, ExactVectorBufferCache, FloatArray, ExactVectorBufferTest, FloatArray

### Community 31 - "GroundednessVerdict"
Cohesion: 0.12
Nodes (17): GroundednessVerdict, Accepted, ClassifierIdentityMismatchException, CurrentGroundednessCalibration, EmptyVisibleAnswerException, ExperimentalGroundednessCalibration, FallbackToNormalGeneration, GroundednessCalibrationProfile (+9 more)

### Community 32 - "Fixture"
Cohesion: 0.15
Nodes (5): RagEvidenceBudget, Fixture, RagPromptTokenCounter, RagCoordinatorTest, RagPromptTokenCounter

### Community 34 - ".refreshInputControls"
Cohesion: 0.21
Nodes (4): PendingPrivacyAction, RevealResponse, SubmitPrompt, GroundednessClassifier

### Community 35 - "RagQueryRouterTest"
Cohesion: 0.17
Nodes (8): RagQueryRoute, COMPLEX_RETRIEVAL, NO_RETRIEVAL, SINGLE_RETRIEVAL, RagQueryRouter, RagRouteInput, RagQueryRouterTest, RouteCase

### Community 36 - "TokenSpan"
Cohesion: 0.18
Nodes (8): E5Tokenizer, TokenSpan, validatedTokenSpans(), CodePointTokenizer, E5Tokenizer, KnowledgeBaseEntityFactoryTest, E5Tokenizer, E5Tokenizer

### Community 37 - "E5Embedder"
Cohesion: 0.14
Nodes (9): E5Embedder, Encoded, AutoCloseable, E5Tokenizer, FloatArray, LongArray, EmbeddingModelManifest, EmbeddingModelPackageVerifier (+1 more)

### Community 38 - "fail"
Cohesion: 0.19
Nodes (6): fail(), ParsedBlockCodec, ByteArray, SafeOoxmlReader, LocatedLine, StrictTextSource

### Community 39 - "ConversationStore"
Cohesion: 0.09
Nodes (4): ConversationStore, ModelHistoryText, TimelineMutation, ConversationStoreTest

### Community 40 - "VisualResponseDecision"
Cohesion: 0.14
Nodes (10): RagVisualGroundingPolicy, VisualResponseAssertion, NON_VISUAL_RESPONSE, UNCERTAIN_VISUAL_ASSERTION, VISUAL_ASSERTION, VisualResponseDecision, ALLOW, BLOCK_UNCERTAIN_ASSERTION (+2 more)

### Community 41 - "RagImportNotifications.kt"
Cohesion: 0.60
Nodes (3): Context, RagImportNotifications, ForegroundInfo

### Community 42 - "AnswerabilityVerdict"
Cohesion: 0.13
Nodes (9): AnswerabilityLabel, PARTIAL, SUPPORTED, UNSUPPORTED, AnswerabilityVerdict, RagGuardClassifier, RagGuardContractTest, RagGuardClassifier (+1 more)

### Community 43 - "Result"
Cohesion: 0.20
Nodes (8): CancelImportWorker, CoroutineWorker, EmbedWorker, CoroutineWorker, Context, Uri, Result, VideoFrameExtractor

### Community 44 - "RagContextBudgeter"
Cohesion: 0.20
Nodes (7): RagPromptTokenCounter, RagContextBudgeter, RagPromptTokenCounter, RagEvidenceBudgeter, RagPromptTokenCounter, RagContextBudgeterTest, WordCounter

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
Cohesion: 0.16
Nodes (7): CachedImageSource, ImageSourceCache, ImageSourceTooLargeException, ImageSourceUnreadableException, ImageSourceCacheTest, FileOutputStream, InputStream

### Community 49 - "HierarchicalNSW"
Cohesion: 0.05
Nodes (58): getDataByLabel(), dist_t, DISTFUNC, labeltype, pair, priority_queue, string, unique_ptr (+50 more)

### Community 50 - "CascadedEvidenceAcceptancePolicy"
Cohesion: 0.43
Nodes (4): AnswerabilityCalibrationProfile, CascadedEvidenceAcceptancePolicy, CurrentAnswerabilityCalibration, ExperimentalAnswerabilityCalibration

### Community 52 - "IOException"
Cohesion: 0.33
Nodes (3): ConversationArchiveCodec, RagImportFailureClassifierTest, IOException

### Community 53 - "Context"
Cohesion: 0.19
Nodes (3): Context, Context, ModelInfo

### Community 54 - "ValueError"
Cohesion: 0.16
Nodes (19): _load_excluded_document_ids(), _read_json(), validate_redacted_text(), _load_jsonl(), _load_manifest(), main(), _parse_args(), Namespace (+11 more)

### Community 55 - "Java_com_example_minicpm_1v_1demo_TtsEngine_nativeTtsGenerate"
Cohesion: 0.29
Nodes (14): jint, JNIEnv, JNIEXPORT, jstring, string, vector, Java_com_example_minicpm_1v_1demo_TtsEngine_nativeInitOmni(), Java_com_example_minicpm_1v_1demo_TtsEngine_nativeOmniFree() (+6 more)

### Community 56 - ".attempt"
Cohesion: 0.60
Nodes (4): Attempt, Failure, T, Success

### Community 57 - "ParserInput"
Cohesion: 0.08
Nodes (13): CsvParser, DocumentParser, ParserInput, HtmlParser, MarkdownParser, ParserRegistry, OcrAwareDocumentParser, PdfDocumentParser (+5 more)

### Community 58 - "AppLanguage"
Cohesion: 0.31
Nodes (6): AppLanguage, EN, ZH, Activity, Context, LocaleManager

### Community 59 - "DenseRankedHit"
Cohesion: 0.25
Nodes (6): Accumulator, DenseRankedHit, FusedRankedHit, LexicalRankedHit, ReciprocalRankFusion, ReciprocalRankFusionTest

### Community 60 - "ParserError"
Cohesion: 0.08
Nodes (22): RagLimits, Exception, ParserError, CANCELLED, INVALID_ENCODING, MALFORMED_DOCUMENT, OCR_FAILED, PDF_CORRUPT (+14 more)

### Community 61 - "XlsxParser"
Cohesion: 0.21
Nodes (5): Attributes, CharArray, SharedStringsHandler, SheetHandler, XlsxParser

### Community 63 - "ChunkEmbeddingEntity"
Cohesion: 0.16
Nodes (5): ChunkDao, ChunkFtsMatchInfoRow, EmbeddingCorpusStamp, ChunkEmbeddingEntity, ChunkEntity

### Community 64 - "CitationRef"
Cohesion: 0.19
Nodes (7): CitationRef, Available, CitationSourceResolution, CitationSourceResolver, Deleted, Unavailable, CitationSourceResolverTest

### Community 65 - "PptxParser"
Cohesion: 0.27
Nodes (4): Attributes, CharArray, PptxParser, SlideHandler

### Community 66 - "MiniCPMApplication"
Cohesion: 0.11
Nodes (18): HybridRetrieverInstrumentedTest, MiniCPMApplication, DatabaseRagTurnStateSource, IdentityRagEvidenceReducer, RagCoordinator, RagEvidenceReducer, RagPromptBuilder, RagRetrievalMode (+10 more)

### Community 67 - "RuntimeException"
Cohesion: 0.32
Nodes (4): FileSource, ByteArray, RaceWinner, RuntimeException

### Community 69 - "ChatMessage"
Cohesion: 0.13
Nodes (12): DiffCallback, Bitmap, AiMessage, ChatMessage, confirmedForSubmission(), RagGenerationStage, GENERATING, ORGANIZING (+4 more)

### Community 70 - "LocalGuardReplyPolicy.kt"
Cohesion: 0.18
Nodes (9): LocalGuardReplyKind, NO_VISUAL_CONTEXT, UNCERTAIN_VISUAL_REQUEST, LocalGuardReplyPolicy, LocalResponseStreamer, PromptDestination, LOCAL_ONLY, MODEL (+1 more)

### Community 72 - "rag_hnsw_jni.cpp"
Cohesion: 0.07
Nodes (53): canonical_existing_directory(), jint, jlong, JNIEnv, JNIEXPORT, jobject, jstring, string (+45 more)

### Community 73 - "RagGuardClassifier.kt"
Cohesion: 0.18
Nodes (9): GroundednessLabel, GROUNDED, PARTIAL, UNGROUNDED, RagOutputReviewAction, ACCEPT, REGENERATE, REJECT_WITH_LOCAL_REPLY (+1 more)

### Community 74 - "AnswerabilityModelManifestTest"
Cohesion: 0.24
Nodes (4): AnswerabilityModelManifest, AnswerabilityModelPackageVerifier, CurrentAnswerabilityModel, AnswerabilityModelManifestTest

### Community 75 - "RagDocumentRemovalService"
Cohesion: 0.23
Nodes (5): RagDocumentRemovalService, Context, RagImportFailureHandler, RagDocumentRemovalServiceTest, ListenableWorker

### Community 77 - "Bounded Mobile RAG Context"
Cohesion: 0.18
Nodes (12): Bounded Vector Backend, Guard v3 Training Result, Reviewed Generation Transaction, Sentence and Token Budget, Bounded Mobile RAG Context, Grounded RAG Fallback Policy, Guard v3 Release Boundary, Local RAG Experimental Pipeline (+4 more)

### Community 78 - "RecordingSource"
Cohesion: 0.36
Nodes (4): FloatArray, VectorEmbeddingSource, RecordingSource, VectorSearchBackendTest

### Community 80 - "DocumentEntity"
Cohesion: 0.10
Nodes (7): DocumentDao, DocumentEntity, RagDocumentArtifactCleaner, RagImportFailureData, RagDocumentArtifactCleanerTest, RagImportFailureDataTest, Data

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
Cohesion: 0.06
Nodes (21): ChunkIdentity, DetectedFileType, EMPTY, JPEG, OOXML_ZIP, PDF, PNG, TEXT (+13 more)

### Community 85 - "OnnxRagGuardClassifier"
Cohesion: 0.30
Nodes (4): AutoCloseable, FloatArray, RagGuardClassifier, OnnxRagGuardClassifier

### Community 86 - "RankedChunkId"
Cohesion: 0.15
Nodes (10): PartitionedExactVectorRanker, ExactVectorSearchBackend, VectorEmbeddingSource, VectorSearchBackend, VectorSearchRequest, ExactVectorRanker, FloatArray, RankedChunkId (+2 more)

### Community 87 - "BoundedXmlHandler"
Cohesion: 0.38
Nodes (3): BoundedXmlHandler, Attributes, DefaultHandler

### Community 90 - "LlamaVisualCheckpointInstrumentedTest"
Cohesion: 0.42
Nodes (3): ByteArray, Context, LlamaVisualCheckpointInstrumentedTest

### Community 91 - "RagGuardModelManifest"
Cohesion: 0.23
Nodes (5): CurrentRagGuardModel, RagGuardModelFile, RagGuardModelManifest, RagGuardModelPackageVerifier, RagGuardModelManifestTest

### Community 92 - "public_office_dataset.py"
Cohesion: 0.14
Nodes (30): ArchiveValidationError, build_public_holdout(), _build_rows(), _clean_text(), _evidence_window(), GoldExample, HoldoutBundle, _is_safe_member() (+22 more)

### Community 93 - "MultiVectorSearchStopCondition"
Cohesion: 0.11
Nodes (17): EpsilonSearchStopCondition, curr_num_items_, epsilon_, max_num_candidates_, min_num_candidates_, dist_t, labeltype, pair (+9 more)

### Community 94 - "ContentSafetyDecision"
Cohesion: 0.22
Nodes (7): ContentSafetyAssessment, ContentSafetyDecision, ALLOW, BLOCK, REVIEW, WARNING, ContentSafetyPolicyEngine

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

### Community 104 - "EmbeddingModelManager"
Cohesion: 0.19
Nodes (4): EmbeddingModelManager, AutoCloseable, AutoCloseable, RagGuardModelManager

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

### Community 112 - "BruteforceSearch"
Cohesion: 0.14
Nodes (16): BruteforceSearch, cur_element_count, data_, data_size_, dict_external_to_internal, dist_func_param_, fstdistfunc_, index_lock (+8 more)

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

### Community 120 - ".maskedMeanAndNormalize"
Cohesion: 0.47
Nodes (3): E5Pooling, FloatArray, LongArray

### Community 122 - "RAG Large Vector Backend Implementation Plan"
Cohesion: 0.29
Nodes (6): RAG Large Vector Backend Implementation Plan, Task 1: Extract a unified exact backend, Task 2: Define and validate the HNSW sidecar envelope, Task 3: Add the pinned native HNSW implementation, Task 4: Build, switch, and recover indexes atomically, Task 5: Benchmark and close the phase

### Community 124 - "KnowledgeBaseAdapter.kt"
Cohesion: 0.13
Nodes (11): KnowledgeBaseAdapter, KnowledgeBaseListItem, TextView, View, ViewGroup, HorizontalSwipeDismissPolicy, Failure, KnowledgeBaseDocumentPresentation (+3 more)

### Community 125 - "WelcomeSuggestionMode"
Cohesion: 0.33
Nodes (5): WelcomeSuggestionMode, TEXT_PROMPTS, VISUAL_INPUT_ACTIONS, VISUAL_PROMPTS, WelcomeSuggestionPolicy

### Community 126 - "ChunkPrerequisiteDecision"
Cohesion: 0.20
Nodes (7): ChunkPrerequisiteDecision, MODEL_REQUIRED, READY, TOKENIZER_MISMATCH, ChunkWorkPolicy, TokenizerIdentity, ChunkWorkPolicyTest

### Community 127 - "SpaceInterface"
Cohesion: 0.11
Nodes (13): SpaceInterface, get_data_size, get_dist_func, get_dist_func_param, DISTFUNC, L2Space, data_size_, dim_ (+5 more)

### Community 132 - "RagTurnTransaction"
Cohesion: 0.32
Nodes (5): Context, RagConversationContextInstrumentedTest, RagTurnTransaction, FakeEphemeralContextEngine, RagTurnTransactionTest

### Community 133 - "CheckpointTestHostActivity.kt"
Cohesion: 0.60
Nodes (3): CheckpointTestHostActivity, Activity, Bundle

### Community 136 - "VisualPromptDecision"
Cohesion: 0.40
Nodes (4): VisualPromptDecision, ALLOW, BLOCK_NEEDS_VISUAL, BLOCK_UNCERTAIN

### Community 137 - "MultiVectorInnerProductSpace"
Cohesion: 0.10
Nodes (16): unordered_map, BaseMultiVectorSpace, get_doc_id, set_doc_id, DISTFUNC, MultiVectorInnerProductSpace, data_size_, dim_ (+8 more)

### Community 138 - "hnswlib.h"
Cohesion: 0.17
Nodes (11): AVX512Capable(), AVXCapable(), cpuid(), T, pairGreater, readBinaryPOD(), writeBinaryPOD(), xgetbv() (+3 more)

### Community 139 - "RAG 文档删除与失败提示 Implementation Plan"
Cohesion: 0.33
Nodes (5): RAG 文档删除与失败提示 Implementation Plan, Task 1: 固定安全清理和同名重传的数据行为, Task 2: Make failed imports self-cleaning and observable without a RAG document row, Task 3: Add long-press deletion and swipe-dismiss failure notices, Task 4: Verify build, security boundaries and persisted project graph

### Community 148 - "RAG Source Lifecycle Implementation Plan"
Cohesion: 0.40
Nodes (4): RAG Source Lifecycle Implementation Plan, Task 1: Resolve current and deleted sources, Task 2: Connect source chips to Room lifecycle state, Task 3: Synchronize active progress and Graphify

### Community 149 - "PrivacyDataType"
Cohesion: 0.50
Nodes (4): PrivacyDataType, CHINESE_ID_CARD, MOBILE_PHONE, POSTAL_ADDRESS

### Community 152 - "space_ip.h"
Cohesion: 0.21
Nodes (14): InnerProduct(), InnerProductDistance(), InnerProductDistanceSIMD16ExtAVX(), InnerProductDistanceSIMD16ExtAVX512(), InnerProductDistanceSIMD16ExtResiduals(), InnerProductDistanceSIMD16ExtSSE(), InnerProductDistanceSIMD4ExtAVX(), InnerProductDistanceSIMD4ExtResiduals() (+6 more)

### Community 154 - "AlgorithmInterface"
Cohesion: 0.15
Nodes (11): AlgorithmInterface, addPoint, AlgorithmInterface<dist_t>::searchKnnCloserFirst(), saveIndex, searchKnn, searchKnnCloserFirst, BaseFilterFunctor, dist_t (+3 more)

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

### Community 192 - "space_l2.h"
Cohesion: 0.27
Nodes (4): L2Sqr(), L2SqrSIMD16ExtResiduals(), L2SqrSIMD4Ext(), L2SqrSIMD4ExtResiduals()

### Community 194 - "ChunkWorker.kt"
Cohesion: 0.15
Nodes (5): RagTempFileCleaner, E5TokenizerRegistry, E5Tokenizer, ChunkWorker, CoroutineWorker

### Community 195 - "RagTurnFailure"
Cohesion: 0.33
Nodes (6): RagTurnFailure, EVIDENCE_PROCESSING_FAILED, PROMPT_BUILD_FAILED, RETRIEVAL_UNAVAILABLE, ROUTING_UNAVAILABLE, STATE_UNAVAILABLE

### Community 197 - "RAG Lifecycle Pressure Matrix Implementation Plan"
Cohesion: 0.33
Nodes (5): RAG Lifecycle Pressure Matrix Implementation Plan, Task 1: Expose checkpoint ownership safely, Task 2: Add deterministic success/cancellation pressure, Task 3: Run real Activity lifecycle conflicts, Task 4: Close the phase

### Community 198 - "BaseSearchStopCondition"
Cohesion: 0.25
Nodes (7): BaseSearchStopCondition, add_point_to_result, filter_results, remove_point_from_result, should_consider_candidate, should_remove_extra, should_stop_search

### Community 199 - "InnerProductSpace"
Cohesion: 0.29
Nodes (5): DISTFUNC, InnerProductSpace, data_size_, dim_, fstdistfunc_

### Community 204 - "RagImportCancelReceiver.kt"
Cohesion: 0.53
Nodes (4): Context, Intent, RagImportCancelReceiver, BroadcastReceiver

## Knowledge Gaps
- **345 isolated node(s):** `RELEVANT`, `SIMILAR_BUT_WRONG`, `UNRELATED`, `GREETING`, `IDENTIFIER` (+340 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **60 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `MainActivity` to `CitationRef`, `.refreshInputControls`, `ChatMessage`, `ConversationArchive`, `ConversationStore`, `LlamaEngine`, `.showChatSettingsDialog`, `ChatAdapter`, `ImageSourceCache`, `PendingImageViewModel`, `RagTurnLifecycleInstrumentedTest`, `ModelManagerActivity`, `.handleSelectedVideo`, `.submitMessages`, `LlamaState`?**
  _High betweenness centrality (0.122) - this node is a cross-community bridge._
- **Why does `LlamaEngine` connect `LlamaEngine` to `MainActivity`, `RuntimeException`, `RagTurnTransaction`, `VisualPromptDecision`, `VisualResponseDecision`, `VisualContextPolicy`, `ModelManagerActivity`, `RagTurnLifecycleInstrumentedTest`, `LlamaEngine.kt`, `Context`, `LlamaVisualCheckpointInstrumentedTest`, `EncryptedFileStore`, `LlamaState`?**
  _High betweenness centrality (0.083) - this node is a cross-community bridge._
- **Why does `RetrievedChunk` connect `RetrievedChunk` to `MainActivity`, `EvidenceReducerTest`, `ExactAnchorMatcherTest`, `RagVisualGroundingPolicyTest`, `RetrievalCalibrationKey`, `RagDatabase`, `.plan`, `CitationValidator`, `RagRetrievalRequest`, `GroundednessVerdict`, `Fixture`, `RagGuardInstrumentedTest`, `RagQueryRouterTest`, `VisualResponseDecision`, `AnswerabilityVerdict`, `RagContextBudgeter`, `CascadedEvidenceAcceptancePolicy`, `AnswerabilityClassifier`, `MiniCPMApplication`, `RagGuardClassifier.kt`, `RagPromptAssembler`, `OnnxRagGuardClassifier`, `LazyAnswerabilityClassifier`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `MainActivity` (e.g. with `ConversationArchiveDiskStore` and `ConversationStore`) actually correct?**
  _`MainActivity` has 3 INFERRED edges - model-reasoned connections that need verification._
- **Are the 15 inferred relationships involving `RetrievedChunk` (e.g. with `.retrieve()` and `.retrieve()`) actually correct?**
  _`RetrievedChunk` has 15 INFERRED edges - model-reasoned connections that need verification._
- **What connects `RELEVANT`, `SIMILAR_BUT_WRONG`, `UNRELATED` to the rest of the system?**
  _345 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `MainActivity` be split into smaller, more focused modules?**
  _Cohesion score 0.08383838383838384 - nodes in this community are weakly interconnected._