package com.example.minicpm_v_demo.rag.embed

import android.content.Context
import java.io.File

class EmbeddingModelManager(private val context: Context) : AutoCloseable {
    @Volatile private var opened: E5Embedder? = null

    fun modelDirectory(): File = File(context.filesDir, "rag/models/multilingual-e5-small")

    @Synchronized
    fun openInstalled(): E5Embedder? {
        opened?.let { return it }
        val directory = modelDirectory()
        if (!directory.isDirectory) return null
        return runCatching { E5Embedder.open(directory, E5ModelSpec.PINNED) }.getOrNull()?.also {
            opened = it
            E5TokenizerRegistry.installVerified(it)
        }
    }

    override fun close() {
        opened?.close()
        opened = null
        E5TokenizerRegistry.clear()
    }
}
