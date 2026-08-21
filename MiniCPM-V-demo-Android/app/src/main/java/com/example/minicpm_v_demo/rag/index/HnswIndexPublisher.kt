package com.example.minicpm_v_demo.rag.index

import com.example.minicpm_v_demo.rag.crypto.EncryptedFileStore
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HnswIndexPublisher(
    indexDirectory: File,
    private val encryptedFileStore: EncryptedFileStore,
) {
    private val directory = indexDirectory.canonicalFile.also { root ->
        require((root.isDirectory || root.mkdirs()) && root.isDirectory) {
            "HNSW index directory is unavailable"
        }
    }
    private val manager = HnswIndexManager(directory) { Long.MAX_VALUE }

    @Throws(IOException::class)
    fun publish(
        metadata: HnswIndexMetadata,
        plaintextIndex: File,
        shouldContinue: () -> Boolean = { true },
    ): HnswIndexPaths {
        val candidate = plaintextIndex.canonicalFile
        require(candidate.parentFile == directory && candidate.isFile) {
            "HNSW plaintext candidate must be inside the managed index directory"
        }
        require(HnswIndexIntegrity.verify(candidate, metadata)) {
            "HNSW plaintext candidate does not match metadata"
        }
        val paths = manager.pathsFor(metadata.corpusKey)
        try {
            candidate.inputStream().buffered().use { input ->
                encryptedFileStore.encrypt(input, paths.encryptedIndex, shouldContinue)
            }
            if (!shouldContinue()) throw IOException("HNSW publication cancelled")
            encryptedFileStore.encrypt(
                ByteArrayInputStream(HnswIndexMetadataCodec.encode(metadata)),
                paths.metadata,
                shouldContinue,
            )
            return paths
        } finally {
            if (candidate.exists() && !candidate.delete()) {
                runCatching { candidate.writeBytes(ByteArray(0)) }
                candidate.delete()
            }
        }
    }

    @Throws(IOException::class)
    fun readMetadata(corpusKey: EmbeddingCorpusKey): HnswIndexMetadata {
        val file = manager.pathsFor(corpusKey).metadata
        if (!file.isFile) throw IOException("HNSW metadata is unavailable")
        val plaintext = ByteArrayOutputStream()
        encryptedFileStore.decrypt(file, plaintext)
        return HnswIndexMetadataCodec.decode(ByteArrayInputStream(plaintext.toByteArray()))
    }

    @Throws(IOException::class)
    fun <T> withVerifiedPlaintext(
        corpusKey: EmbeddingCorpusKey,
        block: (File) -> T,
    ): T {
        val paths = manager.pathsFor(corpusKey)
        val metadata = readMetadata(corpusKey)
        if (!metadata.matches(corpusKey) || !paths.encryptedIndex.isFile) {
            throw IOException("HNSW publication is stale or incomplete")
        }
        val plaintext = File.createTempFile("hnsw-", ".plain", directory).canonicalFile
        try {
            FileOutputStream(plaintext).use { output ->
                encryptedFileStore.decrypt(paths.encryptedIndex, output)
                output.fd.sync()
            }
            if (!HnswIndexIntegrity.verify(plaintext, metadata)) {
                throw IOException("HNSW publication failed integrity verification")
            }
            return block(plaintext)
        } finally {
            if (plaintext.exists() && !plaintext.delete()) {
                runCatching { plaintext.writeBytes(ByteArray(0)) }
                plaintext.delete()
            }
        }
    }

}
