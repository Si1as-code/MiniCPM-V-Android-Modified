package com.example.minicpm_v_demo.rag.importer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FileTypeDetectorTest {
    @Test
    fun `magic bytes override a misleading PDF extension and MIME`() {
        val png = byteArrayOf(
            0x89.toByte(), 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a,
        )

        val result = FileTypeDetector.detect(png, "application/pdf", "invoice.pdf")

        assertEquals(DetectedFileType.PNG, result.type)
        assertTrue(result.declarationMismatch)
    }

    @Test
    fun `detects supported binary containers from signatures`() {
        assertEquals(
            DetectedFileType.PDF,
            FileTypeDetector.detect("%PDF-1.7".toByteArray(), null, "report").type,
        )
        assertEquals(
            DetectedFileType.JPEG,
            FileTypeDetector.detect(byteArrayOf(0xff.toByte(), 0xd8.toByte(), 0xff.toByte()), null, "photo").type,
        )
        assertEquals(
            DetectedFileType.OOXML_ZIP,
            FileTypeDetector.detect(byteArrayOf(0x50, 0x4b, 0x03, 0x04), null, "document.docx").type,
        )
        assertEquals(
            DetectedFileType.WEBP,
            FileTypeDetector.detect("RIFF1234WEBP".toByteArray(), null, "image").type,
        )
    }

    @Test
    fun `accepts UTF text but rejects unknown binary data`() {
        assertEquals(
            DetectedFileType.TEXT,
            FileTypeDetector.detect("会议记录\nAction items".toByteArray(), "text/plain", "notes.txt").type,
        )
        assertEquals(
            DetectedFileType.UNSUPPORTED_BINARY,
            FileTypeDetector.detect(byteArrayOf(0x00, 0x01, 0x02, 0x00, 0x7f), null, "payload.bin").type,
        )
    }

    @Test
    fun `empty files are rejected explicitly`() {
        assertEquals(
            DetectedFileType.EMPTY,
            FileTypeDetector.detect(byteArrayOf(), "text/plain", "empty.txt").type,
        )
    }
}
