package com.example.minicpm_v_demo.rag.work

import com.example.minicpm_v_demo.rag.db.DocumentDao

class RagWorkRecovery(
    private val documentDao: DocumentDao,
    private val coordinator: RagWorkCoordinator,
) {
    suspend fun rescheduleInterruptedImports(): Int {
        val documents = documentDao.findRecoverableImports()
            .filter { RagWorkRecoveryPolicy.shouldReschedule(it.status) }
        documents.forEach { coordinator.enqueue(it.id) }
        return documents.size
    }
}
