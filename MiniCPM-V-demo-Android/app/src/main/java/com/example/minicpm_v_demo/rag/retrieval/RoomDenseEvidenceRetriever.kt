package com.example.minicpm_v_demo.rag.retrieval

import com.example.minicpm_v_demo.rag.RagEvidenceRetriever
import com.example.minicpm_v_demo.rag.RagRetrievalOutcome
import com.example.minicpm_v_demo.rag.RagRetrievalRequest
import com.example.minicpm_v_demo.rag.db.RagDatabase
import com.example.minicpm_v_demo.rag.embed.E5InputKind
import com.example.minicpm_v_demo.rag.embed.E5ModelSpec
import com.example.minicpm_v_demo.rag.embed.EmbeddingModelManager
import com.example.minicpm_v_demo.rag.embed.FloatVectorCodec

class RoomDenseEvidenceRetriever(
    private val database: RagDatabase,
    private val modelManager: EmbeddingModelManager,
    private val corpusVersion: Int = CurrentRetrievalCalibration.key.corpusVersion,
) : RagEvidenceRetriever {
    override suspend fun retrieve(request: RagRetrievalRequest): RagRetrievalOutcome {
        require(request.knowledgeBaseIds.isNotEmpty())
        require(request.question.isNotBlank() && request.limit in 1..40)
        val embedder = modelManager.openInstalled() ?: return RagRetrievalOutcome.ModelRequired
        val queryVector = embedder.embed(listOf(request.question), E5InputKind.QUERY).single()
        val modelSha = E5ModelSpec.PINNED.files.getValue("model.int8.onnx")
        val embeddings = database.chunkDao().findReadyEmbeddings(
            request.knowledgeBaseIds,
            modelSha,
            corpusVersion,
        )
        val ranked = ExactVectorRanker.rank(
            queryVector,
            embeddings.map { embedding ->
                VectorCandidate(
                    embedding.chunkId,
                    FloatVectorCodec.decode(embedding.vector, embedding.dimension),
                )
            },
            request.limit,
        )
        val chunks = database.chunkDao().findByIds(ranked.map(RankedChunkId::chunkId)).associateBy { it.id }
        return RagRetrievalOutcome.Evidence(
            ranked.mapNotNull { result ->
                chunks[result.chunkId]?.let { chunk ->
                    RetrievedChunk(
                        chunkId = chunk.id,
                        displayName = chunk.displayName,
                        locator = listOf(chunk.locatorType, chunk.locatorValue)
                            .filter(String::isNotBlank)
                            .joinToString(" "),
                        text = chunk.text,
                        score = result.score,
                        documentId = chunk.documentId,
                        tokenCount = chunk.tokenCount,
                        denseScore = result.score,
                        calibrationKey = CurrentRetrievalCalibration.key,
                    )
                }
            },
        )
    }
}
