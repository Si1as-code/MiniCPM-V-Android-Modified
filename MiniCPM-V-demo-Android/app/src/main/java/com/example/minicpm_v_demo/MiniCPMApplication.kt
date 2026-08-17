package com.example.minicpm_v_demo

import android.app.Application
import com.example.minicpm_v_demo.rag.crypto.RagKeyManager
import com.example.minicpm_v_demo.rag.crypto.RagTempFileCleaner
import com.example.minicpm_v_demo.rag.retrieval.CascadedEvidenceAcceptancePolicy
import com.example.minicpm_v_demo.rag.retrieval.CurrentRetrievalCalibration
import com.example.minicpm_v_demo.rag.DatabaseRagTurnStateSource
import com.example.minicpm_v_demo.rag.IdentityRagEvidenceReducer
import com.example.minicpm_v_demo.rag.RagCoordinator
import com.example.minicpm_v_demo.rag.RagPromptBuilder
import com.example.minicpm_v_demo.rag.RagRunIdFactory
import com.example.minicpm_v_demo.rag.RoomRagStateQueries
import com.example.minicpm_v_demo.rag.SourceCountRagEvidenceBudgeter
import com.example.minicpm_v_demo.rag.db.RagDatabaseFactory
import com.example.minicpm_v_demo.rag.embed.EmbeddingModelManager
import com.example.minicpm_v_demo.rag.retrieval.RoomDenseEvidenceRetriever
import com.example.minicpm_v_demo.rag.retrieval.HybridRetriever
import com.example.minicpm_v_demo.rag.retrieval.RagPromptAssembler
import com.example.minicpm_v_demo.rag.retrieval.RoomLexicalEvidenceRetriever
import com.example.minicpm_v_demo.rag.route.DefaultRagQueryRouter
import com.example.minicpm_v_demo.rag.work.RagWorkRecovery
import com.example.minicpm_v_demo.rag.work.WorkManagerRagWorkCoordinator
import androidx.work.WorkManager
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.UUID
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class MiniCPMApplication : Application() {
    val embeddingModelManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { EmbeddingModelManager(this) }
    val ragKeyManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RagKeyManager(this)
    }

    val ragDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RagDatabaseFactory(this, ragKeyManager).open()
    }
    private val denseRagRetriever by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RoomDenseEvidenceRetriever(ragDatabase, embeddingModelManager)
    }
    private val hybridRagRetriever by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        HybridRetriever(
            denseRetriever = denseRagRetriever,
            lexicalRetriever = RoomLexicalEvidenceRetriever(
                ragDatabase,
                CurrentRetrievalCalibration.key,
            ),
            calibrationKey = CurrentRetrievalCalibration.key,
        )
    }
    val ragCoordinator by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RagCoordinator(
            stateSource = DatabaseRagTurnStateSource(
                RoomRagStateQueries(ragDatabase.conversationRagDao()),
            ),
            router = DefaultRagQueryRouter(),
            retriever = hybridRagRetriever,
            acceptancePolicy = CascadedEvidenceAcceptancePolicy(
                retrievalKey = CurrentRetrievalCalibration.key,
                classifier = null,
                profile = null,
            ),
            reducer = IdentityRagEvidenceReducer,
            budgeter = SourceCountRagEvidenceBudgeter(),
            promptBuilder = RagPromptBuilder(RagPromptAssembler::assemble),
            runIdFactory = RagRunIdFactory { UUID.randomUUID().toString() },
        )
    }

    override fun onCreate() {
        super.onCreate()
        PDFBoxResourceLoader.init(this)
        LocaleManager.applyOnAppStart(this)
        ragMaintenanceExecutor.execute {
            RagTempFileCleaner.cleanup(RagTempFileCleaner.stagingDirectory(noBackupFilesDir))
            runBlocking {
                val installedModel = embeddingModelManager.openInstalled()
                installedModel?.let { model ->
                    ragDatabase.knowledgeBaseDao().updateInstalledModelHash(
                        model.modelId, model.modelSha256, System.currentTimeMillis(),
                    )
                }
                RagWorkRecovery(
                    ragDatabase.documentDao(),
                    WorkManagerRagWorkCoordinator(WorkManager.getInstance(this@MiniCPMApplication)),
                ).rescheduleInterruptedImports(retryModelBindingFailures = installedModel != null)
            }
        }
    }

    private val ragMaintenanceExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "rag-maintenance").apply { isDaemon = true }
    }
}
