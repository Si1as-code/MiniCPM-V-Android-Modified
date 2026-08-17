package com.example.minicpm_v_demo.rag

internal fun RagTurnPlan.plainModelPromptOrNull(originalUserText: String): String? = when (this) {
    RagTurnPlan.Disabled,
    RagTurnPlan.NoRetrieval,
    RagTurnPlan.NoEvidence -> originalUserText
    else -> null
}
