package com.example.minicpm_v_demo.rag.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.minicpm_v_demo.MiniCPMApplication
import com.example.minicpm_v_demo.rag.crypto.EncryptedFileStore
import com.example.minicpm_v_demo.rag.db.DocumentStatus
import com.example.minicpm_v_demo.rag.embed.E5ModelSpec
import com.example.minicpm_v_demo.rag.index.EmbeddingCorpusKey
import com.example.minicpm_v_demo.rag.index.HnswCorpusSource
import com.example.minicpm_v_demo.rag.index.HnswIndexBuilder
import com.example.minicpm_v_demo.rag.index.HnswIndexPublisher
import com.example.minicpm_v_demo.rag.retrieval.CurrentRetrievalCalibration
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RagWorkStagePlan {
    val workerClasses: List<Class<out ListenableWorker>> = listOf(
        ImportCopyWorker::class.java,
        ParseWorker::class.java,
        OcrWorker::class.java,
        ChunkWorker::class.java,
        EmbedWorker::class.java,
        FinalizeIndexWorker::class.java,
        VectorIndexWorker::class.java,
    )
}

/** Builds an optional per-knowledge-base HNSW acceleration sidecar after the document is READY. */
class VectorIndexWorker(appContext: Context, parameters: WorkerParameters) :
    CoroutineWorker(appContext, parameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val documentId = inputData.getString(RagWorkContract.KEY_DOCUMENT_ID)
            ?: return@withContext Result.failure()
        runCatching { RagWorkContract.requireValidDocumentId(documentId) }
            .getOrElse { return@withContext Result.failure() }
        val app = applicationContext as? MiniCPMApplication ?: return@withContext Result.failure()
        val document = app.ragDatabase.documentDao().findById(documentId)
            ?: return@withContext Result.failure()
        if (document.status != DocumentStatus.READY) return@withContext Result.failure()

        val knowledgeBaseIds = listOf(document.knowledgeBaseId)
        val modelSha = E5ModelSpec.PINNED.files.getValue("model.int8.onnx")
        val corpusVersion = CurrentRetrievalCalibration.key.corpusVersion
        val chunkDao = app.ragDatabase.chunkDao()
        val source = object : HnswCorpusSource {
            override suspend fun currentKey(): EmbeddingCorpusKey {
                val stamp = chunkDao.findReadyEmbeddingStamp(
                    knowledgeBaseIds,
                    modelSha,
                    corpusVersion,
                )
                return EmbeddingCorpusKey(
                    knowledgeBaseIds = knowledgeBaseIds,
                    modelSha256 = modelSha,
                    corpusVersion = corpusVersion,
                    embeddingCount = stamp.embeddingCount,
                    maximumUpdatedAt = stamp.maximumUpdatedAt,
                    chunkIdSum = stamp.chunkIdSum,
                )
            }

            override suspend fun loadPage(offset: Int, pageSize: Int) =
                chunkDao.findReadyEmbeddingsPage(
                    knowledgeBaseIds = knowledgeBaseIds,
                    modelSha256 = modelSha,
                    corpusVersion = corpusVersion,
                    pageSize = pageSize,
                    offset = offset,
                )
        }

        try {
            val expectedCorpus = source.currentKey()
            val indexDirectory = File(applicationContext.noBackupFilesDir, "rag/index").apply {
                check((isDirectory || mkdirs()) && isDirectory)
            }
            val publisher = HnswIndexPublisher(
                indexDirectory,
                EncryptedFileStore(app.ragKeyManager::getOrCreateMasterKey),
            )
            HnswIndexBuilder(indexDirectory, publisher).build(
                expectedCorpus = expectedCorpus,
                source = source,
                shouldContinue = { !isStopped },
            )
            Result.success()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            if (isStopped) throw CancellationException("HNSW index build cancelled")
            // HNSW is an optional acceleration layer. Room vectors remain the source of truth.
            Result.success()
        }
    }
}
