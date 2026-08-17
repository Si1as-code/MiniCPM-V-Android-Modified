package com.example.minicpm_v_demo.rag

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RagTurnDeliveryPolicyTest {
    @Test
    fun noEvidenceFallsBackToUnmodifiedPlainModelPrompt() {
        val originalUserText = "你能做什么"

        assertEquals(
            originalUserText,
            RagTurnPlan.NoEvidence.plainModelPromptOrNull(originalUserText),
        )
    }

    @Test
    fun actionableAndTechnicalStatesDoNotSilentlyFallBack() {
        val originalUserText = "问题"

        assertNull(RagTurnPlan.NoSelection.plainModelPromptOrNull(originalUserText))
        assertNull(RagTurnPlan.Indexing.plainModelPromptOrNull(originalUserText))
        assertNull(RagTurnPlan.ModelRequired.plainModelPromptOrNull(originalUserText))
        assertNull(
            RagTurnPlan.Failed(RagTurnFailure.RETRIEVAL_UNAVAILABLE)
                .plainModelPromptOrNull(originalUserText),
        )
    }
}
