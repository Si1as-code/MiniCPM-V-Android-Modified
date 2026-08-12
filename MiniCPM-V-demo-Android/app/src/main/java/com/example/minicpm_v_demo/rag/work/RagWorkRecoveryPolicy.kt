package com.example.minicpm_v_demo.rag.work

import com.example.minicpm_v_demo.rag.db.DocumentStatus

object RagWorkRecoveryPolicy {
    fun shouldReschedule(status: DocumentStatus): Boolean =
        status == DocumentStatus.QUEUED || status == DocumentStatus.COPYING

    fun <T> selectObservable(items: List<T>, isFinished: (T) -> Boolean): T? =
        items.firstOrNull { !isFinished(it) } ?: items.firstOrNull()
}
