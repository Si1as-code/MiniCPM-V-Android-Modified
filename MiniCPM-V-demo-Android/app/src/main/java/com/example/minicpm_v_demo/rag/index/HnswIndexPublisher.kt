package com.example.minicpm_v_demo.rag.index

import android.util.AtomicFile
import com.example.minicpm_v_demo.rag.crypto.EncryptedFileStore
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

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
    private val publicationLock = publicationLocks.computeIfAbsent(directory.path) { Any() }

    @Throws(IOException::class)
    fun publish(
        metadata: HnswIndexMetadata,
        plaintextIndex: File,
        shouldContinue: () -> Boolean = { true },
    ): HnswIndexPaths = synchronized(publicationLock) {
        val candidate = plaintextIndex.canonicalFile
        require(candidate.parentFile == directory && candidate.isFile) {
            "HNSW plaintext candidate must be inside the managed index directory"
        }
        require(HnswIndexIntegrity.verify(candidate, metadata)) {
            "HNSW plaintext candidate does not match metadata"
        }
        val paths = manager.pathsFor(metadata.corpusKey)
        val previous = previousPaths(paths)
        val hasPrevious = backupCurrentIfValid(paths, previous, metadata.corpusKey)
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
            check(isValidPair(paths, metadata.corpusKey)) {
                "Published HNSW generation failed verification"
            }
            deletePrevious(previous)
            return paths
        } catch (error: Exception) {
            if (hasPrevious) restorePrevious(paths, previous, metadata.corpusKey)
            throw error
        } finally {
            if (candidate.exists() && !candidate.delete()) {
                runCatching { candidate.writeBytes(ByteArray(0)) }
                candidate.delete()
            }
        }
    }

    @Throws(IOException::class)
    fun readMetadata(corpusKey: EmbeddingCorpusKey): HnswIndexMetadata = synchronized(publicationLock) {
        val paths = manager.pathsFor(corpusKey)
        return try {
            readMetadataFile(paths.metadata).also { metadata ->
                if (!metadata.matches(corpusKey)) throw IOException("HNSW metadata corpus mismatch")
            }
        } catch (error: Exception) {
            if (!restorePrevious(paths, previousPaths(paths), corpusKey)) throw error
            readMetadataFile(paths.metadata).also { metadata ->
                if (!metadata.matches(corpusKey)) throw IOException("HNSW metadata corpus mismatch")
            }
        }
    }

    private fun readMetadataFile(file: File): HnswIndexMetadata {
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
        val plaintext = synchronized(publicationLock) {
            var metadata = readMetadata(corpusKey)
            try {
                decryptVerified(paths, metadata)
            } catch (error: Exception) {
                if (!restorePrevious(paths, previousPaths(paths), corpusKey)) throw error
                metadata = readMetadataFile(paths.metadata)
                decryptVerified(paths, metadata)
            }
        }
        try {
            return block(plaintext)
        } finally {
            deletePlaintext(plaintext)
        }
    }

    private fun decryptVerified(paths: HnswIndexPaths, metadata: HnswIndexMetadata): File {
        if (!paths.encryptedIndex.isFile) throw IOException("HNSW publication is incomplete")
        val plaintext = File.createTempFile("hnsw-", ".plain", directory).canonicalFile
        try {
            FileOutputStream(plaintext).use { output ->
                encryptedFileStore.decrypt(paths.encryptedIndex, output)
                output.fd.sync()
            }
            if (!HnswIndexIntegrity.verify(plaintext, metadata)) {
                throw IOException("HNSW publication failed integrity verification")
            }
            return plaintext
        } catch (error: Exception) {
            deletePlaintext(plaintext)
            throw error
        }
    }

    private fun backupCurrentIfValid(
        paths: HnswIndexPaths,
        previous: HnswIndexPaths,
        corpusKey: EmbeddingCorpusKey,
    ): Boolean {
        deletePrevious(previous)
        if (!isValidPair(paths, corpusKey)) return false
        return try {
            copyAtomically(paths.encryptedIndex, previous.encryptedIndex)
            copyAtomically(paths.metadata, previous.metadata)
            true
        } catch (_: Exception) {
            deletePrevious(previous)
            false
        }
    }

    private fun restorePrevious(
        paths: HnswIndexPaths,
        previous: HnswIndexPaths,
        corpusKey: EmbeddingCorpusKey,
    ): Boolean {
        if (!previous.encryptedIndex.isFile || !previous.metadata.isFile) return false
        return try {
            copyAtomically(previous.encryptedIndex, paths.encryptedIndex)
            copyAtomically(previous.metadata, paths.metadata)
            if (!isValidPair(paths, corpusKey)) return false
            deletePrevious(previous)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun isValidPair(paths: HnswIndexPaths, corpusKey: EmbeddingCorpusKey): Boolean =
        runCatching {
            val metadata = readMetadataFile(paths.metadata)
            if (!metadata.matches(corpusKey)) return@runCatching false
            val plaintext = decryptVerified(paths, metadata)
            deletePlaintext(plaintext)
            true
        }.getOrDefault(false)

    private fun previousPaths(paths: HnswIndexPaths) = HnswIndexPaths(
        encryptedIndex = File(directory, "${paths.encryptedIndex.name}.previous"),
        metadata = File(directory, "${paths.metadata.name}.previous"),
    )

    private fun copyAtomically(source: File, target: File) {
        if (!source.isFile || source.canonicalFile.parentFile != directory ||
            target.canonicalFile.parentFile != directory
        ) throw IOException("Invalid HNSW generation file")
        val atomicFile = AtomicFile(target)
        val output = atomicFile.startWrite()
        try {
            source.inputStream().buffered().use { input ->
                BufferedOutputStream(output).useWithoutClosingUnderlying { buffered ->
                    input.copyTo(buffered)
                    buffered.flush()
                }
            }
            output.fd.sync()
            atomicFile.finishWrite(output)
        } catch (error: Exception) {
            atomicFile.failWrite(output)
            throw error
        }
    }

    private fun deletePrevious(previous: HnswIndexPaths) {
        previous.encryptedIndex.delete()
        previous.metadata.delete()
    }

    private fun deletePlaintext(plaintext: File) {
        if (plaintext.exists() && !plaintext.delete()) {
            runCatching { plaintext.writeBytes(ByteArray(0)) }
            plaintext.delete()
        }
    }

    private inline fun BufferedOutputStream.useWithoutClosingUnderlying(
        block: (BufferedOutputStream) -> Unit,
    ) {
        block(this)
    }

    private companion object {
        val publicationLocks = ConcurrentHashMap<String, Any>()
    }
}
