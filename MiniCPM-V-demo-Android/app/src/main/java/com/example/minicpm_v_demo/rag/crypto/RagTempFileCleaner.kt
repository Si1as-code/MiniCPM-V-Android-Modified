package com.example.minicpm_v_demo.rag.crypto

import java.io.File
import java.nio.file.Files

object RagTempFileCleaner {
    const val DEFAULT_STALE_AFTER_MS = 24L * 60 * 60 * 1_000

    /** Returns true when at least one stale plaintext staging file was removed. */
    fun cleanup(
        stagingDirectory: File,
        nowMs: Long = System.currentTimeMillis(),
        staleAfterMs: Long = DEFAULT_STALE_AFTER_MS,
    ): Boolean {
        require(staleAfterMs >= 0) { "staleAfterMs must be non-negative" }
        if (!stagingDirectory.isDirectory || Files.isSymbolicLink(stagingDirectory.toPath())) return false
        val oldestAllowedModifiedAt = nowMs - staleAfterMs
        var deletedAny = false
        stagingDirectory.listFiles().orEmpty().forEach { candidate ->
            val isPlaintextPart = candidate.isFile &&
                !Files.isSymbolicLink(candidate.toPath()) &&
                candidate.name.endsWith(PART_SUFFIX) &&
                candidate.lastModified() <= oldestAllowedModifiedAt
            if (isPlaintextPart && candidate.delete()) deletedAny = true
        }
        return deletedAny
    }

    fun stagingDirectory(noBackupFilesDirectory: File): File =
        noBackupFilesDirectory.resolve("rag").resolve("source")

    private const val PART_SUFFIX = ".part"
}
