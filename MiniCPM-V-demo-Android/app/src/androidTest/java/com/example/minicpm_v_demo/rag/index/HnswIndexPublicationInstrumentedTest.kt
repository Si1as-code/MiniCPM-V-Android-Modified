package com.example.minicpm_v_demo.rag.index

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.minicpm_v_demo.rag.crypto.EncryptedFileStore
import com.example.minicpm_v_demo.rag.embed.E5ModelSpec
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.util.UUID
import javax.crypto.KeyGenerator
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HnswIndexPublicationInstrumentedTest {
    @Test
    fun publishEncryptsPayloadAuthenticatesMetadataAndLeavesNoPlaintext() {
        val root = testRoot()
        try {
            val key = generatedKey()
            val store = EncryptedFileStore { key }
            val publisher = HnswIndexPublisher(root, store)
            val plaintext = File(root, "candidate.hnsw").apply { writeBytes("native-index-v1".toByteArray()) }
            val metadata = metadata(plaintext, generation = 1)

            val published = publisher.publish(metadata, plaintext)

            assertFalse(plaintext.exists())
            assertTrue(published.encryptedIndex.isFile)
            assertTrue(published.metadata.isFile)
            assertEquals(metadata, publisher.readMetadata(metadata.corpusKey))
            assertArrayEquals(
                "native-index-v1".toByteArray(),
                publisher.withVerifiedPlaintext(metadata.corpusKey) { file -> file.readBytes() },
            )
            assertFalse(root.walkTopDown().any { it.name.endsWith(".part") || it.name.endsWith(".plain") })
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun cancelledReplacementPreservesThePreviousAuthenticatedGeneration() {
        val root = testRoot()
        try {
            val key = generatedKey()
            val publisher = HnswIndexPublisher(root, EncryptedFileStore { key })
            val firstPlaintext = File(root, "first.hnsw").apply { writeBytes("generation-one".toByteArray()) }
            val first = metadata(firstPlaintext, generation = 1)
            publisher.publish(first, firstPlaintext)

            val replacement = File(root, "replacement.hnsw").apply {
                writeBytes(ByteArray(256 * 1024) { 7 })
            }
            val second = metadata(replacement, generation = 2)
            var checks = 0
            assertThrows(IOException::class.java) {
                publisher.publish(second, replacement) { ++checks < 2 }
            }

            assertFalse(replacement.exists())
            assertEquals(first, publisher.readMetadata(first.corpusKey))
            assertArrayEquals(
                "generation-one".toByteArray(),
                publisher.withVerifiedPlaintext(first.corpusKey) { file -> file.readBytes() },
            )
            assertFalse(root.walkTopDown().any { it.name.endsWith(".part") || it.name.endsWith(".plain") })
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun tamperedMetadataIsRejectedByAuthenticatedRead() {
        val root = testRoot()
        try {
            val key = generatedKey()
            val publisher = HnswIndexPublisher(root, EncryptedFileStore { key })
            val plaintext = File(root, "metadata-tamper.hnsw").apply {
                writeBytes("metadata-authentication".toByteArray())
            }
            val metadata = metadata(plaintext, generation = 3)
            val paths = publisher.publish(metadata, plaintext)
            RandomAccessFile(paths.metadata, "rw").use { file ->
                file.seek(file.length() - 1)
                val value = file.read()
                file.seek(file.length() - 1)
                file.write(value xor 1)
            }

            assertThrows(IOException::class.java) {
                publisher.readMetadata(metadata.corpusKey)
            }
        } finally {
            root.deleteRecursively()
        }
    }

    private fun testRoot(): File {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        return File(context.noBackupFilesDir, "rag/index-test-${UUID.randomUUID()}").apply {
            check(mkdirs())
        }
    }

    private fun metadata(file: File, generation: Long) = HnswIndexMetadata(
        corpusKey = EmbeddingCorpusKey(
            knowledgeBaseIds = listOf("kb-publication"),
            modelSha256 = "0".repeat(64),
            corpusVersion = 1,
            embeddingCount = 6_001,
            maximumUpdatedAt = 10,
            chunkIdSum = 18_009_001,
        ),
        dimension = E5ModelSpec.PINNED.dimension,
        indexGeneration = generation,
        maximumChunkId = 6_001,
        plaintextLength = file.length(),
        plaintextSha256 = HnswIndexIntegrity.sha256(file),
        builtAt = generation,
    )

    private fun generatedKey() = KeyGenerator.getInstance("AES").run {
        init(256)
        generateKey()
    }
}
