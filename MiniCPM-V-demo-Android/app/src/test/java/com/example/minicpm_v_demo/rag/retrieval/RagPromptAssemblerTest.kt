package com.example.minicpm_v_demo.rag.retrieval

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RagPromptAssemblerTest {
    @Test
    fun `keeps user question and labels untrusted sources`() {
        val prompt = RagPromptAssembler.assemble(
            "What is the limit?",
            listOf(RetrievedChunk(7, "policy.txt", "page 2", "The limit is 20.", 0.9f)),
        )

        assertTrue(prompt.contains("What is the limit?"))
        assertTrue(prompt.contains("[S1] policy.txt (page 2)"))
        assertTrue(prompt.contains("The limit is 20."))
        assertTrue(prompt.contains("untrusted reference data"))
        assertFalse(prompt.contains("<system>"))
    }
}
