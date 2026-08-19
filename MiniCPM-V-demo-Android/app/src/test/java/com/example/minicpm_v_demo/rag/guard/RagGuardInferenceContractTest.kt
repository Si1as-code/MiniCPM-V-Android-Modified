package com.example.minicpm_v_demo.rag.guard

import com.example.minicpm_v_demo.rag.retrieval.AnswerabilityLabel
import com.example.minicpm_v_demo.rag.retrieval.RetrievedChunk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RagGuardInferenceContractTest {
    @Test
    fun `input text exactly matches the training contract`() {
        val sources = listOf(source("first"), source("second", id = 2))

        assertEquals(
            "query: question\nevidence: first\n\nsecond",
            RagGuardInput.answerability(" question ", sources),
        )
        assertEquals(
            "query: question\nevidence: first\n\nsecond\nanswer: response",
            RagGuardInput.groundedness(" question ", sources, " response "),
        )
    }

    @Test
    fun `token truncation preserves the tokenizer end token`() {
        assertArrayEquals(
            longArrayOf(0, 10, 11, 2),
            RagGuardInput.truncatePreservingEndToken(longArrayOf(0, 10, 11, 12, 13, 2), 4),
        )
        assertArrayEquals(
            longArrayOf(0, 2),
            RagGuardInput.truncatePreservingEndToken(longArrayOf(0, 2), 4),
        )
    }

    @Test
    fun `shared runner selects the requested head and decodes softmax probabilities`() = runBlocking {
        val calls = mutableListOf<Int>()
        val classifier = OnnxRagGuardClassifier.forTest(
            manifest = CurrentRagGuardModel.PINNED,
            encode = { longArrayOf(0, 7, 2) },
            infer = { ids, attention, taskId ->
                assertArrayEquals(longArrayOf(0, 7, 2), ids)
                assertArrayEquals(longArrayOf(1, 1, 1), attention)
                calls += taskId
                if (taskId == 0) floatArrayOf(4f, 1f, -1f) else floatArrayOf(-2f, 0f, 3f)
            },
        )

        val answerability = classifier.classifyAnswerability("question", listOf(source("evidence")))
        val groundedness = classifier.classifyGroundedness(
            "question",
            listOf(source("evidence")),
            "answer",
        )

        assertEquals(listOf(0, 1), calls)
        assertEquals(AnswerabilityLabel.SUPPORTED, answerability.label)
        assertEquals(GroundednessLabel.UNGROUNDED, groundedness.label)
        assertTrue(answerability.supportedProbability > 0.94f)
        assertTrue(groundedness.groundedProbability < 0.01f)
        assertEquals(CurrentRagGuardModel.PINNED.model.sha256, answerability.modelSha256)
    }

    private fun source(text: String, id: Long = 1) = RetrievedChunk(
        chunkId = id,
        displayName = "policy.txt",
        locator = "line $id",
        text = text,
        score = 1f,
        documentId = "doc-$id",
        tokenCount = 1,
    )
}
