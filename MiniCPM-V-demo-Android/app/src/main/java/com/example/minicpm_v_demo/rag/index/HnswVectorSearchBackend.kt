package com.example.minicpm_v_demo.rag.index

import java.io.File

class HnswVectorSearchBackend(
    indexDirectory: File,
    private val publisher: HnswIndexPublisher,
    appMemoryBudgetBytes: () -> Long,
    private val exactFallback: VectorSearchBackend = ExactVectorSearchBackend(),
    private val minimumEmbeddingCount: Int = 5_001,
    private val efSearch: Int = 48,
) : VectorSearchBackend {
    private val directory = indexDirectory.canonicalFile.also { root ->
        require(root.isDirectory) { "HNSW index directory is unavailable" }
    }
    private val manager = HnswIndexManager(directory, appMemoryBudgetBytes)

    init {
        require(minimumEmbeddingCount > 0)
        require(efSearch > 0)
    }

    override suspend fun search(
        request: VectorSearchRequest,
        source: VectorEmbeddingSource,
    ) = if (request.corpusKey.embeddingCount < minimumEmbeddingCount) {
        exactFallback.search(request, source)
    } else {
        runCatching {
            val metadata = publisher.readMetadata(request.corpusKey)
            val admission = manager.assess(request.corpusKey, metadata)
            check(admission.allowed) { "HNSW sidecar was rejected: ${admission.rejection}" }
            publisher.withVerifiedPlaintext(request.corpusKey) { plaintext ->
                HnswIndex.load(
                    indexDirectory = directory,
                    indexFile = plaintext,
                    dimension = metadata.dimension,
                    maximumElements = metadata.corpusKey.embeddingCount,
                ).use { index ->
                    index.search(
                        query = request.query,
                        topK = request.limit,
                        efSearch = maxOf(efSearch, request.limit),
                    )
                }
            }
        }.getOrElse {
            exactFallback.search(request, source)
        }
    }
}
