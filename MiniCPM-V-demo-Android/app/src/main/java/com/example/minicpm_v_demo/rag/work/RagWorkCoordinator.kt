package com.example.minicpm_v_demo.rag.work

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class RagWorkUiState(
    val state: WorkInfo.State,
    val progressDone: Int,
    val progressTotal: Int,
    val failureDocumentId: String? = null,
    val failureKnowledgeBaseId: String? = null,
    val failureDisplayName: String? = null,
    val failureErrorCode: String? = null,
)

interface RagWorkCoordinator {
    fun enqueue(documentId: String): Operation
    fun cancel(documentId: String): Operation
    fun observe(documentId: String): Flow<RagWorkUiState?>
}

class WorkManagerRagWorkCoordinator(
    private val workManager: WorkManager,
) : RagWorkCoordinator {
    override fun enqueue(documentId: String): Operation {
        val input = RagWorkContract.inputValues(documentId)
        val copyRequest = OneTimeWorkRequestBuilder<ImportCopyWorker>()
            .setInputData(Data.Builder().apply { input.forEach(::putString) }.build())
            .addTag(RagWorkContract.uniqueWorkName(documentId))
            .build()
        val parseRequest = OneTimeWorkRequestBuilder<ParseWorker>()
            .setInputData(Data.Builder().apply { input.forEach(::putString) }.build())
            .addTag(RagWorkContract.uniqueWorkName(documentId))
            .build()
        val ocrRequest = OneTimeWorkRequestBuilder<OcrWorker>()
            .setInputData(Data.Builder().apply { input.forEach(::putString) }.build())
            .addTag(RagWorkContract.uniqueWorkName(documentId))
            .build()
        val chunkRequest = OneTimeWorkRequestBuilder<ChunkWorker>()
            .setInputData(Data.Builder().apply { input.forEach(::putString) }.build())
            .addTag(RagWorkContract.uniqueWorkName(documentId))
            .build()
        val embedRequest = OneTimeWorkRequestBuilder<EmbedWorker>()
            .setInputData(Data.Builder().apply { input.forEach(::putString) }.build())
            .addTag(RagWorkContract.uniqueWorkName(documentId))
            .build()
        val finalizeRequest = OneTimeWorkRequestBuilder<FinalizeIndexWorker>()
            .setInputData(Data.Builder().apply { input.forEach(::putString) }.build())
            .addTag(RagWorkContract.uniqueWorkName(documentId))
            .build()
        return workManager.beginUniqueWork(
            RagWorkContract.uniqueWorkName(documentId),
            ExistingWorkPolicy.KEEP,
            copyRequest,
        ).then(parseRequest).then(ocrRequest).then(chunkRequest).then(embedRequest).then(finalizeRequest).enqueue()
    }

    override fun cancel(documentId: String): Operation {
        RagWorkContract.requireValidDocumentId(documentId)
        val request = OneTimeWorkRequestBuilder<CancelImportWorker>()
            .setInputData(Data.Builder().putString(RagWorkContract.KEY_DOCUMENT_ID, documentId).build())
            .build()
        return workManager.beginUniqueWork(
            RagWorkContract.uniqueWorkName(documentId),
            ExistingWorkPolicy.REPLACE,
            request,
        ).enqueue()
    }

    override fun observe(documentId: String): Flow<RagWorkUiState?> =
        workManager.getWorkInfosForUniqueWorkFlow(RagWorkContract.uniqueWorkName(documentId))
            .map { workInfos ->
                RagWorkRecoveryPolicy.selectObservable(
                    workInfos,
                    isActive = { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED },
                    isFailed = { it.state == WorkInfo.State.FAILED },
                )
                    ?.let { info ->
                    RagWorkUiState(
                        state = info.state,
                        progressDone = info.progress.getInt(KEY_PROGRESS_DONE, 0),
                        progressTotal = info.progress.getInt(KEY_PROGRESS_TOTAL, 0),
                        failureDocumentId = info.outputData.getString(RagImportFailureData.KEY_DOCUMENT_ID),
                        failureKnowledgeBaseId = info.outputData.getString(RagImportFailureData.KEY_KNOWLEDGE_BASE_ID),
                        failureDisplayName = info.outputData.getString(RagImportFailureData.KEY_DISPLAY_NAME),
                        failureErrorCode = info.outputData.getString(RagImportFailureData.KEY_ERROR_CODE),
                    )
                }
            }

    companion object {
        const val KEY_PROGRESS_DONE = "progressDone"
        const val KEY_PROGRESS_TOTAL = "progressTotal"
    }

}
