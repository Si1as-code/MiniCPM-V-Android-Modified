package com.example.minicpm_v_demo.rag.retrieval

import com.example.minicpm_v_demo.rag.db.RagDatabase
import com.example.minicpm_v_demo.rag.embed.E5InputKind
import com.example.minicpm_v_demo.rag.embed.E5ModelSpec
import com.example.minicpm_v_demo.rag.embed.EmbeddingModelManager
import com.example.minicpm_v_demo.rag.embed.FloatVectorCodec
import com.example.minicpm_v_demo.rag.RagEvidenceRetriever
import com.example.minicpm_v_demo.rag.RagRetrievalOutcome
import com.example.minicpm_v_demo.rag.RagRetrievalRequest

class LocalRagRetriever(
    private val database: RagDatabase,
    private val modelManager: EmbeddingModelManager,
) : RagEvidenceRetriever {
    override suspend fun retrieve(request: RagRetrievalRequest): RagRetrievalOutcome {
        require(request.knowledgeBaseIds.isNotEmpty())
        require(request.question.isNotBlank() && request.limit in 1..20)
        val embedder = modelManager.openInstalled() ?: return RagRetrievalOutcome.ModelRequired
        return RagRetrievalOutcome.Evidence(
            retrieveWithModel(
                request.knowledgeBaseIds,
                request.question,
                request.limit,
                embedder,
            ),
        )
    }

    private suspend fun retrieveWithModel(
        knowledgeBaseIds: List<String>,
        question: String,
        limit: Int,
        embedder: com.example.minicpm_v_demo.rag.embed.E5Embedder,
    ): List<RetrievedChunk> {
        val queryVector = embedder.embed(listOf(question), E5InputKind.QUERY).single()
        val modelSha = E5ModelSpec.PINNED.files.getValue("model.int8.onnx")
        val embeddings = database.chunkDao().findReadyEmbeddings(knowledgeBaseIds, modelSha)
        val ranked = ExactVectorRanker.rank(
            queryVector,
            embeddings.map { VectorCandidate(it.chunkId, FloatVectorCodec.decode(it.vector, it.dimension)) },
            limit,
        )
        val chunks = database.chunkDao().findByIds(ranked.map(RankedChunkId::chunkId)).associateBy { it.id }
        return ranked.mapNotNull { result -> chunks[result.chunkId]?.let { chunk ->
            RetrievedChunk(
                chunkId = chunk.id,
                displayName = chunk.displayName,
                locator = listOf(chunk.locatorType, chunk.locatorValue).filter(String::isNotBlank).joinToString(" "),
                text = chunk.text,
                score = result.score,
                documentId = chunk.documentId,
                tokenCount = chunk.tokenCount,
            )
        } }
    }
}
