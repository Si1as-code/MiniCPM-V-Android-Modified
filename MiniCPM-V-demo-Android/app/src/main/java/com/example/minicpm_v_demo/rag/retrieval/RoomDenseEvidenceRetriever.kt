package com.example.minicpm_v_demo.rag.retrieval

import com.example.minicpm_v_demo.rag.RagEvidenceRetriever
import com.example.minicpm_v_demo.rag.RagRetrievalOutcome
import com.example.minicpm_v_demo.rag.RagRetrievalRequest
import com.example.minicpm_v_demo.rag.db.RagDatabase
import com.example.minicpm_v_demo.rag.embed.E5InputKind
import com.example.minicpm_v_demo.rag.embed.E5ModelSpec
import com.example.minicpm_v_demo.rag.embed.EmbeddingModelManager
import com.example.minicpm_v_demo.rag.index.EmbeddingCorpusKey
import com.example.minicpm_v_demo.rag.index.ExactVectorBuffer
import com.example.minicpm_v_demo.rag.index.ExactVectorBufferCache
import com.example.minicpm_v_demo.rag.index.PartitionedExactVectorRanker

class RoomDenseEvidenceRetriever(
    private val database: RagDatabase,
    private val modelManager: EmbeddingModelManager,
    private val corpusVersion: Int = CurrentRetrievalCalibration.key.corpusVersion,
    private val bufferCache: ExactVectorBufferCache = ExactVectorBufferCache(),
) : RagEvidenceRetriever {
    override suspend fun retrieve(request: RagRetrievalRequest): RagRetrievalOutcome {
        require(request.knowledgeBaseIds.isNotEmpty())
        require(request.question.isNotBlank() && request.limit in 1..40)
        val embedder = modelManager.openInstalled() ?: return RagRetrievalOutcome.ModelRequired
        val queryVector = embedder.embed(listOf(request.question), E5InputKind.QUERY).single()
        val modelSha = E5ModelSpec.PINNED.files.getValue("model.int8.onnx")
        val sortedKnowledgeBaseIds = request.knowledgeBaseIds.distinct().sorted()
        val stamp = database.chunkDao().findReadyEmbeddingStamp(
            sortedKnowledgeBaseIds,
            modelSha,
            corpusVersion,
        )
        if (stamp.embeddingCount == 0) return RagRetrievalOutcome.Evidence(emptyList())
        val cacheKey = EmbeddingCorpusKey(
            knowledgeBaseIds = sortedKnowledgeBaseIds,
            modelSha256 = modelSha,
            corpusVersion = corpusVersion,
            embeddingCount = stamp.embeddingCount,
            maximumUpdatedAt = stamp.maximumUpdatedAt,
            chunkIdSum = stamp.chunkIdSum,
        )
        val ranked = if (stamp.embeddingCount <= MAX_CACHED_CHUNKS) {
            val vectorBuffer = bufferCache.get(cacheKey) ?: ExactVectorBuffer.from(
                database.chunkDao().findReadyEmbeddings(
                    sortedKnowledgeBaseIds,
                    modelSha,
                    corpusVersion,
                ),
            ).also { bufferCache.put(cacheKey, it) }
            vectorBuffer.rank(queryVector, request.limit)
        } else {
            rankPartitioned(
                queryVector = queryVector,
                knowledgeBaseIds = sortedKnowledgeBaseIds,
                modelSha = modelSha,
                limit = request.limit,
            )
        }
        val finalStamp = database.chunkDao().findReadyEmbeddingStamp(
            sortedKnowledgeBaseIds,
            modelSha,
            corpusVersion,
        )
        if (finalStamp != stamp) return RagRetrievalOutcome.Evidence(emptyList())
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

    private suspend fun rankPartitioned(
        queryVector: FloatArray,
        knowledgeBaseIds: List<String>,
        modelSha: String,
        limit: Int,
    ): List<RankedChunkId> {
        var offset = 0
        var ranked = emptyList<RankedChunkId>()
        while (true) {
            val page = database.chunkDao().findReadyEmbeddingsPage(
                knowledgeBaseIds = knowledgeBaseIds,
                modelSha256 = modelSha,
                corpusVersion = corpusVersion,
                pageSize = PARTITION_CHUNKS,
                offset = offset,
            )
            if (page.isEmpty()) break
            ranked = PartitionedExactVectorRanker.merge(
                ranked,
                ExactVectorBuffer.from(page).rank(queryVector, limit),
                limit,
            )
            offset += page.size
            if (page.size < PARTITION_CHUNKS) break
        }
        return ranked
    }

    private companion object {
        const val MAX_CACHED_CHUNKS = 5_000
        const val PARTITION_CHUNKS = 1_000
    }
}
