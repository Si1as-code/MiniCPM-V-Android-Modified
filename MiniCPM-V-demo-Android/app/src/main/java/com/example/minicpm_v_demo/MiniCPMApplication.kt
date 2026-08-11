package com.example.minicpm_v_demo

import android.app.Application
import com.example.minicpm_v_demo.rag.crypto.RagKeyManager
import com.example.minicpm_v_demo.rag.db.RagDatabaseFactory

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
    }
}
