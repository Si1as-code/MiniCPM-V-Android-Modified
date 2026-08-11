package com.example.minicpm_v_demo.rag.crypto

import android.util.AtomicFile
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class EncryptedFileStore(
    private val keyProvider: () -> SecretKey,
    private val secureRandom: SecureRandom = SecureRandom(),
) {
    fun encrypt(source: InputStream, target: File) {
        val parent = target.parentFile
        require(parent == null || parent.isDirectory || parent.mkdirs()) {
            "Unable to create encrypted file directory"
        }
        val atomicFile = AtomicFile(target)
        val fileOutput = atomicFile.startWrite()
        try {
            val nonce = ByteArray(GCM_NONCE_BYTES).also(secureRandom::nextBytes)
            val cipher = newCipher(Cipher.ENCRYPT_MODE, nonce)
            DataOutputStream(BufferedOutputStream(fileOutput)).useWithoutClosingUnderlying { output ->
                output.write(MAGIC)
                output.writeByte(FORMAT_VERSION)
                output.writeByte(nonce.size)
                output.write(nonce)
                transform(source, output, cipher)
                output.flush()
            }
            atomicFile.finishWrite(fileOutput)
        } catch (error: Exception) {
            atomicFile.failWrite(fileOutput)
            throw error
        }
    }

    @Throws(IOException::class)
    fun decrypt(source: File, destination: OutputStream) {
        try {
            DataInputStream(BufferedInputStream(source.inputStream())).use { input ->
                val magic = ByteArray(MAGIC.size).also(input::readFully)
                require(magic.contentEquals(MAGIC)) { "Invalid encrypted RAG file header" }
                require(input.readUnsignedByte() == FORMAT_VERSION) { "Unsupported encrypted RAG file version" }
                val nonceLength = input.readUnsignedByte()
                require(nonceLength == GCM_NONCE_BYTES) { "Invalid encrypted RAG file nonce" }
                val nonce = ByteArray(nonceLength).also(input::readFully)
                transform(input, destination, newCipher(Cipher.DECRYPT_MODE, nonce))
            }
        } catch (error: GeneralSecurityException) {
            throw IOException("Encrypted RAG file authentication failed", error)
        }
    }

    private fun newCipher(mode: Int, nonce: ByteArray): Cipher = Cipher
        .getInstance(AES_GCM_TRANSFORMATION)
        .apply {
            init(mode, keyProvider(), GCMParameterSpec(GCM_TAG_BITS, nonce))
            updateAAD(FILE_AAD)
        }

    private fun transform(source: InputStream, destination: OutputStream, cipher: Cipher) {
        val inputBuffer = ByteArray(BUFFER_BYTES)
        while (true) {
            val count = source.read(inputBuffer)
            if (count < 0) break
            if (count == 0) continue
            cipher.update(inputBuffer, 0, count)?.takeIf { it.isNotEmpty() }?.let(destination::write)
        }
        cipher.doFinal()?.takeIf { it.isNotEmpty() }?.let(destination::write)
    }

    private inline fun DataOutputStream.useWithoutClosingUnderlying(block: (DataOutputStream) -> Unit) {
        block(this)
    }

    companion object {
        private val MAGIC = byteArrayOf('R'.code.toByte(), 'A'.code.toByte(), 'G'.code.toByte(), 'F'.code.toByte())
        private const val FORMAT_VERSION = 1
        private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_NONCE_BYTES = 12
        private const val GCM_TAG_BITS = 128
        private const val BUFFER_BYTES = 64 * 1024
        private val FILE_AAD = "MiniCPM-RAG-FILE-v1".toByteArray(Charsets.UTF_8)
    }
}
