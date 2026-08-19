package com.example.minicpm_v_demo.rag.guard

import com.example.minicpm_v_demo.rag.retrieval.RetrievedChunk

object RagGuardInput {
    fun answerability(question: String, sources: List<RetrievedChunk>): String =
        build(question, sources, answer = null)

    fun groundedness(
        question: String,
        sources: List<RetrievedChunk>,
        answer: String,
    ): String = build(question, sources, answer)

    fun truncatePreservingEndToken(ids: LongArray, maxTokens: Int): LongArray {
        require(ids.isNotEmpty() && maxTokens > 1)
        if (ids.size <= maxTokens) return ids.copyOf()
        return ids.copyOf(maxTokens).also { truncated -> truncated[truncated.lastIndex] = ids.last() }
    }

    private fun build(
        question: String,
        sources: List<RetrievedChunk>,
        answer: String?,
    ): String {
        val cleanQuestion = question.trim()
        require(cleanQuestion.isNotEmpty())
        require(sources.size in 1..3)
        val evidence = sources.joinToString("\n\n") { source -> source.text.trim() }
        require(evidence.isNotEmpty() && sources.all { it.text.isNotBlank() })
        val parts = mutableListOf("query: $cleanQuestion", "evidence: $evidence")
        if (answer != null) {
            val cleanAnswer = answer.trim()
            require(cleanAnswer.isNotEmpty())
            parts += "answer: $cleanAnswer"
        }
        return parts.joinToString("\n")
    }
}
