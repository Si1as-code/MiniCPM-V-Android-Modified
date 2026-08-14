package com.example.minicpm_v_demo.rag.retrieval

enum class RagDispatchAction { PASS_THROUGH, REQUEST_SELECTION, MODEL_REQUIRED, NO_EVIDENCE, AUGMENT }

object RagDispatchPolicy {
    fun decide(
        enabled: Boolean,
        selectedKnowledgeBaseCount: Int,
        modelAvailable: Boolean,
        evidenceCount: Int,
    ): RagDispatchAction {
        require(selectedKnowledgeBaseCount >= 0 && evidenceCount >= 0)
        if (!enabled) return RagDispatchAction.PASS_THROUGH
        if (selectedKnowledgeBaseCount == 0) return RagDispatchAction.REQUEST_SELECTION
        if (!modelAvailable) return RagDispatchAction.MODEL_REQUIRED
        if (evidenceCount == 0) return RagDispatchAction.NO_EVIDENCE
        return RagDispatchAction.AUGMENT
    }
}

enum class RagLocalReplyKind { SELECTION_REQUIRED, MODEL_REQUIRED, NO_EVIDENCE, RETRIEVAL_UNAVAILABLE }

sealed interface RagPromptPreparation {
    data object PassThrough : RagPromptPreparation
    data class Augmented(
        val ragRunId: String,
        val prompt: String,
        val sources: List<RetrievedChunk>,
    ) : RagPromptPreparation
    data class LocalReply(val kind: RagLocalReplyKind) : RagPromptPreparation
}
