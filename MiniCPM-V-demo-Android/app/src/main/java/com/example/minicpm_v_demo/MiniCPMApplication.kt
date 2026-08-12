package com.example.minicpm_v_demo

import android.app.Application
import com.example.minicpm_v_demo.rag.crypto.RagKeyManager
import com.example.minicpm_v_demo.rag.crypto.RagTempFileCleaner
import com.example.minicpm_v_demo.rag.db.RagDatabaseFactory
import com.example.minicpm_v_demo.rag.work.RagWorkRecovery
import com.example.minicpm_v_demo.rag.work.WorkManagerRagWorkCoordinator
import androidx.work.WorkManager
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class MiniCPMApplication : Application() {
    val ragKeyManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RagKeyManager(this)
    }

    val ragDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RagDatabaseFactory(this, ragKeyManager).open()
    }

    override fun onCreate() {
        super.onCreate()
        LocaleManager.applyOnAppStart(this)
        ragMaintenanceExecutor.execute {
            RagTempFileCleaner.cleanup(RagTempFileCleaner.stagingDirectory(noBackupFilesDir))
            runBlocking {
                RagWorkRecovery(
                    ragDatabase.documentDao(),
                    WorkManagerRagWorkCoordinator(WorkManager.getInstance(this@MiniCPMApplication)),
                ).rescheduleInterruptedImports()
            }
        }
    }

    private val ragMaintenanceExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "rag-maintenance").apply { isDaemon = true }
    }
}
