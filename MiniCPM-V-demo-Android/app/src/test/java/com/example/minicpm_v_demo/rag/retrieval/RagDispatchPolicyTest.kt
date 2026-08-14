package com.example.minicpm_v_demo.rag.retrieval

import org.junit.Assert.assertEquals
import org.junit.Test

class RagDispatchPolicyTest {
    @Test
    fun `disabled RAG passes through without retrieval`() {
        assertEquals(RagDispatchAction.PASS_THROUGH, RagDispatchPolicy.decide(false, 0, false, 0))
    }

    @Test
    fun `enabled RAG never silently bypasses missing prerequisites`() {
        assertEquals(RagDispatchAction.REQUEST_SELECTION, RagDispatchPolicy.decide(true, 0, true, 0))
        assertEquals(RagDispatchAction.MODEL_REQUIRED, RagDispatchPolicy.decide(true, 1, false, 0))
        assertEquals(RagDispatchAction.NO_EVIDENCE, RagDispatchPolicy.decide(true, 1, true, 0))
    }

    @Test
    fun `evidence is the only enabled path that augments model prompt`() {
        assertEquals(RagDispatchAction.AUGMENT, RagDispatchPolicy.decide(true, 2, true, 3))
    }
}
