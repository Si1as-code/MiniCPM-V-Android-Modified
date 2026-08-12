package com.example.minicpm_v_demo.rag.ui

import com.example.minicpm_v_demo.rag.db.DocumentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class KnowledgeBaseDocumentPresentationTest {
    @Test
    fun `failed documents remain visible with a safe reason`() {
        assertEquals(
            KnowledgeBaseDocumentPresentation.Failure("加密失败"),
            KnowledgeBaseDocumentPresentation.from(DocumentStatus.FAILED, "ENCRYPTION_FAILED"),
        )
        assertEquals(
            KnowledgeBaseDocumentPresentation.Failure("导入失败"),
            KnowledgeBaseDocumentPresentation.from(DocumentStatus.FAILED, "unexpected-private-detail"),
        )
    }

    @Test
    fun `active documents show processing and completed copy remains visible as success`() {
        assertEquals(
            KnowledgeBaseDocumentPresentation.Processing,
            KnowledgeBaseDocumentPresentation.from(DocumentStatus.COPYING, null),
        )
        assertEquals(
            KnowledgeBaseDocumentPresentation.Uploaded,
            KnowledgeBaseDocumentPresentation.from(DocumentStatus.PARSING, null),
        )
    }

    @Test
    fun `terminal non-failure documents do not remain in the status list`() {
        assertNull(KnowledgeBaseDocumentPresentation.from(DocumentStatus.READY, null))
        assertNull(KnowledgeBaseDocumentPresentation.from(DocumentStatus.CANCELLED, "CANCELLED"))
    }
}
