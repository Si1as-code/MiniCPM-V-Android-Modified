package com.example.minicpm_v_demo.rag.guard

import org.junit.Assert.assertEquals
import org.junit.Test

class RagOutputReviewPolicyTest {
    @Test
    fun `grounded output is accepted immediately`() {
        assertEquals(
            RagOutputReviewAction.ACCEPT,
            RagOutputReviewPolicy.decide(GroundednessLabel.GROUNDED, regenerationCount = 0),
        )
    }

    @Test
    fun `partial or ungrounded output regenerates only once`() {
        listOf(GroundednessLabel.PARTIAL, GroundednessLabel.UNGROUNDED).forEach { label ->
            assertEquals(
                RagOutputReviewAction.REGENERATE,
                RagOutputReviewPolicy.decide(label, regenerationCount = 0),
            )
            assertEquals(
                RagOutputReviewAction.REJECT_WITH_LOCAL_REPLY,
                RagOutputReviewPolicy.decide(label, regenerationCount = 1),
            )
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative regeneration count is rejected`() {
        RagOutputReviewPolicy.decide(GroundednessLabel.GROUNDED, regenerationCount = -1)
    }
}
