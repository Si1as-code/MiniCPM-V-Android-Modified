package com.example.minicpm_v_demo.rag.guard

import android.content.Context
import com.example.minicpm_v_demo.rag.embed.EmbeddingModelManager
import java.io.File

class RagGuardModelManager private constructor(
    private val directoryProvider: () -> File,
    private val opener: (File) -> OnnxRagGuardClassifier,
) : AutoCloseable {
    @Volatile private var opened: OnnxRagGuardClassifier? = null

    constructor(
        context: Context,
        embeddingModelManager: EmbeddingModelManager,
    ) : this(
        directoryProvider = { File(context.filesDir, MODEL_DIRECTORY) },
        opener = { directory ->
            val tokenizer = requireNotNull(embeddingModelManager.openInstalled()) {
                "Verified E5 tokenizer is unavailable"
            }
            OnnxRagGuardClassifier.open(directory, tokenizer)
        },
    )

    fun modelDirectory(): File = directoryProvider()

    @Synchronized
    fun openInstalled(): OnnxRagGuardClassifier? {
        opened?.let { return it }
        val directory = modelDirectory()
        if (!directory.isDirectory) return null
        return runCatching { opener(directory) }.getOrNull()?.also { opened = it }
    }

    @Synchronized
    override fun close() {
        opened?.close()
        opened = null
    }

    companion object {
        private const val MODEL_DIRECTORY = "rag/models/rag-guard-dual-head-v3"

        internal fun forTest(
            directory: File,
            opener: (File) -> OnnxRagGuardClassifier,
        ) = RagGuardModelManager({ directory }, opener)
    }
}
