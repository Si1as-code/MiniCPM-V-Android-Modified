package com.example.minicpm_v_demo.rag.guard

import java.io.File
import java.security.MessageDigest

data class RagGuardModelFile(
    val name: String,
    val bytes: Long,
    val sha256: String,
) {
    init {
        require(SAFE_NAME.matches(name))
        require(bytes in 1..MAX_MODEL_BYTES)
        require(SHA256.matches(sha256))
    }

    private companion object {
        val SAFE_NAME = Regex("[A-Za-z0-9._-]{1,128}")
        val SHA256 = Regex("[0-9a-f]{64}")
        const val MAX_MODEL_BYTES = 256L * 1024L * 1024L
    }
}

data class RagGuardModelManifest(
    val modelId: String,
    val revision: String,
    val architecture: String,
    val maxTokens: Int,
    val externalTokenizerSha256: String,
    val answerabilityTaskId: Int,
    val groundednessTaskId: Int,
    val model: RagGuardModelFile,
) {
    init {
        require(modelId.isNotBlank() && revision.isNotBlank())
        require(architecture == "shared_encoder_dual_three_class_heads")
        require(maxTokens in 1..256)
        require(SHA256.matches(externalTokenizerSha256))
        require(setOf(answerabilityTaskId, groundednessTaskId) == setOf(0, 1))
    }

    private companion object {
        val SHA256 = Regex("[0-9a-f]{64}")
    }
}

object CurrentRagGuardModel {
    val PINNED = RagGuardModelManifest(
        modelId = "local/minicpm-rag-guard-dual-head-v3-experimental",
        revision = "272d66e948a8eee81dbc2656c699c37890a9d6e34bae3f999394ce0c21b19f98",
        architecture = "shared_encoder_dual_three_class_heads",
        maxTokens = 256,
        externalTokenizerSha256 =
            "3396f311d68a8ee4351c0949ab2626543334c5566d7f8ea17b026952ac14d0fe",
        answerabilityTaskId = 0,
        groundednessTaskId = 1,
        model = RagGuardModelFile(
            name = "model.int8.onnx",
            bytes = 118_169_267L,
            sha256 = "6d11400d62b8f15250932e3187aa7b7823809dc0baf0a0ff0a3c157dbe1d35fa",
        ),
    )
}

object RagGuardModelPackageVerifier {
    fun verify(root: File, manifest: RagGuardModelManifest): File {
        val canonicalRoot = root.canonicalFile
        require(canonicalRoot.isDirectory) { "RAG guard model directory is missing" }
        val model = canonicalRoot.resolve(manifest.model.name).canonicalFile
        require(model.parentFile == canonicalRoot && model.isFile) {
            "RAG guard model file is missing"
        }
        require(model.length() == manifest.model.bytes) { "RAG guard model size mismatch" }
        require(sha256(model) == manifest.model.sha256) { "RAG guard model hash mismatch" }
        return canonicalRoot
    }

    fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                if (count > 0) digest.update(buffer, 0, count)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
