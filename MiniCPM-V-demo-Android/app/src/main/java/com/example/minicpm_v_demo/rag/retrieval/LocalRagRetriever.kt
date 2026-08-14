package com.example.minicpm_v_demo.rag.retrieval

import com.example.minicpm_v_demo.rag.db.RagDatabase
import com.example.minicpm_v_demo.rag.embed.E5InputKind
import com.example.minicpm_v_demo.rag.embed.E5ModelSpec
import com.example.minicpm_v_demo.rag.embed.EmbeddingModelManager
import com.example.minicpm_v_demo.rag.embed.FloatVectorCodec
import com.example.minicpm_v_demo.rag.route.DefaultRagQueryRouter
import com.example.minicpm_v_demo.rag.route.RagQueryRoute
import com.example.minicpm_v_demo.rag.route.RagQueryRouter
import com.example.minicpm_v_demo.rag.route.RagRouteInput
import java.util.UUID

class LocalRagRetriever(
    private val database: RagDatabase,
    private val modelManager: EmbeddingModelManager,
    private val queryRouter: RagQueryRouter = DefaultRagQueryRouter(),
) {
    suspend fun preparePrompt(
        conversationId: Long,
        question: String,
        limit: Int = 6,
    ): RagPromptPreparation {
        require(conversationId > 0 && question.isNotBlank() && limit in 1..20)
        return runCatching {
            val dao = database.conversationRagDao()
            val enabled = dao.findState(conversationId)?.ragEnabled == true
            val route = queryRouter.route(
                RagRouteInput(
                    ragEnabled = enabled,
                    query = question,
                    knownDocumentNames = emptyList(),
                )
            )
            if (route == RagQueryRoute.NO_RETRIEVAL) {
                return@runCatching RagPromptPreparation.PassThrough
            }
            val knowledgeBaseIds = dao.findBoundKnowledgeBaseIds(conversationId)
            val model = if (enabled && knowledgeBaseIds.isNotEmpty()) modelManager.openInstalled() else null
            val preliminary = RagDispatchPolicy.decide(enabled, knowledgeBaseIds.size, model != null, 0)
            when (preliminary) {
                RagDispatchAction.PASS_THROUGH -> RagPromptPreparation.PassThrough
                RagDispatchAction.REQUEST_SELECTION ->
                    RagPromptPreparation.LocalReply(RagLocalReplyKind.SELECTION_REQUIRED)
                RagDispatchAction.MODEL_REQUIRED ->
                    RagPromptPreparation.LocalReply(RagLocalReplyKind.MODEL_REQUIRED)
                RagDispatchAction.NO_EVIDENCE -> {
                    val sources = retrieveWithModel(knowledgeBaseIds, question, limit, requireNotNull(model))
                    if (sources.isEmpty()) RagPromptPreparation.LocalReply(RagLocalReplyKind.NO_EVIDENCE)
                    else RagPromptPreparation.Augmented(
                        ragRunId = UUID.randomUUID().toString(),
                        prompt = RagPromptAssembler.assemble(question, sources),
                        sources = sources.toList(),
                    )
                }
                RagDispatchAction.AUGMENT -> error("Evidence is loaded after preflight")
            }
        }.getOrElse {
            RagPromptPreparation.LocalReply(RagLocalReplyKind.RETRIEVAL_UNAVAILABLE)
        }
    }

    suspend fun retrieve(conversationId: Long, question: String, limit: Int = 6): List<RetrievedChunk> {
        require(conversationId > 0 && question.isNotBlank() && limit in 1..20)
        val knowledgeBaseIds = database.conversationRagDao()
            .findSelectedEnabledKnowledgeBaseIds(conversationId)
        if (knowledgeBaseIds.isEmpty()) return emptyList()
        val embedder = modelManager.openInstalled() ?: return emptyList()
        return retrieveWithModel(knowledgeBaseIds, question, limit, embedder)
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
            )
        } }
    }
}
