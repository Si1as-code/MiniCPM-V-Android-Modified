package com.example.minicpm_v_demo.rag.guard

enum class RagOutputReviewAction {
    ACCEPT,
    REGENERATE,
    REJECT_WITH_LOCAL_REPLY,
}

object RagOutputReviewPolicy {
    private const val MAX_REGENERATIONS = 1

    fun decide(
        label: GroundednessLabel,
        regenerationCount: Int,
    ): RagOutputReviewAction {
        require(regenerationCount >= 0)
        if (label == GroundednessLabel.GROUNDED) return RagOutputReviewAction.ACCEPT
        return if (regenerationCount < MAX_REGENERATIONS) {
            RagOutputReviewAction.REGENERATE
        } else {
            RagOutputReviewAction.REJECT_WITH_LOCAL_REPLY
        }
    }
}
