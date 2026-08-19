package com.example.minicpm_v_demo.rag.guard

import kotlin.io.path.createTempDirectory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class RagGuardModelManifestTest {
    @Test
    fun `pinned manifest matches the exported dual-head package`() {
        val manifest = CurrentRagGuardModel.PINNED

        assertEquals(256, manifest.maxTokens)
        assertEquals(0, manifest.answerabilityTaskId)
        assertEquals(1, manifest.groundednessTaskId)
        assertEquals(
            "6d11400d62b8f15250932e3187aa7b7823809dc0baf0a0ff0a3c157dbe1d35fa",
            manifest.model.sha256,
        )
        assertEquals(118_169_267L, manifest.model.bytes)
        assertEquals(
            "3396f311d68a8ee4351c0949ab2626543334c5566d7f8ea17b026952ac14d0fe",
            manifest.externalTokenizerSha256,
        )
    }

    @Test
    fun `verifier enforces exact size hash and canonical child path`() {
        val root = createTempDirectory("rag-guard-package-").toFile()
        try {
            val model = root.resolve("model.int8.onnx").apply { writeText("guard") }
            val manifest = CurrentRagGuardModel.PINNED.copy(
                model = RagGuardModelFile(
                    name = model.name,
                    bytes = model.length(),
                    sha256 = RagGuardModelPackageVerifier.sha256(model),
                ),
            )

            assertEquals(root.canonicalFile, RagGuardModelPackageVerifier.verify(root, manifest))
            assertThrows(IllegalArgumentException::class.java) {
                RagGuardModelPackageVerifier.verify(
                    root,
                    manifest.copy(model = manifest.model.copy(name = "../model.int8.onnx")),
                )
            }
            model.appendText("tampered")
            assertThrows(IllegalArgumentException::class.java) {
                RagGuardModelPackageVerifier.verify(root, manifest)
            }
        } finally {
            root.deleteRecursively()
        }
    }
}
