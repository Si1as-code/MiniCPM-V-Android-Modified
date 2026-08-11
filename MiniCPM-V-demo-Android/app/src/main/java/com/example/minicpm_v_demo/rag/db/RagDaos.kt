package com.example.minicpm_v_demo.rag.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface KnowledgeBaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: KnowledgeBaseEntity)

    @Query("DELETE FROM knowledge_bases WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT * FROM knowledge_bases ORDER BY updatedAt DESC")
    suspend fun findAll(): List<KnowledgeBaseEntity>
}

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DocumentEntity)

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun findById(id: String): DocumentEntity?

    @Query("SELECT * FROM documents WHERE knowledgeBaseId = :knowledgeBaseId ORDER BY createdAt")
    suspend fun findByKnowledgeBase(knowledgeBaseId: String): List<DocumentEntity>

    @Query(
        """
        UPDATE documents
        SET status = :status,
            progressDone = :progressDone,
            progressTotal = :progressTotal,
            updatedAt = :updatedAt,
            lastErrorCode = :lastErrorCode,
            lastErrorDetail = :lastErrorDetail
        WHERE id = :id
        """,
    )
    suspend fun updateStatusAndProgress(
        id: String,
        status: DocumentStatus,
        progressDone: Int,
        progressTotal: Int,
        updatedAt: Long,
        lastErrorCode: String?,
        lastErrorDetail: String?,
    ): Int

    @Transaction
    suspend fun transition(
        id: String,
        to: DocumentStatus,
        progressDone: Int,
        progressTotal: Int,
        updatedAt: Long,
        lastErrorCode: String? = null,
        lastErrorDetail: String? = null,
    ) {
        require(progressDone >= 0 && progressTotal >= 0 && progressDone <= progressTotal) {
            "Invalid progress $progressDone/$progressTotal"
        }
        val current = requireNotNull(findById(id)) { "Unknown document $id" }
        require(DocumentStatusTransitionPolicy.canTransition(current.status, to)) {
            "Invalid document transition ${current.status} -> $to"
        }
        check(
            updateStatusAndProgress(
                id = id,
                status = to,
                progressDone = progressDone,
                progressTotal = progressTotal,
                updatedAt = updatedAt,
                lastErrorCode = lastErrorCode,
                lastErrorDetail = lastErrorDetail,
            ) == 1,
        )
    }
}

@Dao
interface ChunkDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(chunks: List<ChunkEntity>)

    @Query("SELECT * FROM chunks WHERE documentId = :documentId ORDER BY ordinal")
    suspend fun findByDocument(documentId: String): List<ChunkEntity>

    @Query(
        """
        SELECT chunks.*
        FROM chunks
        JOIN chunk_fts ON chunk_fts.rowid = chunks.id
        JOIN documents ON documents.id = chunks.documentId
        JOIN knowledge_bases ON knowledge_bases.id = chunks.knowledgeBaseId
        WHERE chunk_fts MATCH :matchQuery
          AND chunks.knowledgeBaseId = :knowledgeBaseId
          AND documents.status = 'READY'
          AND knowledge_bases.enabled = 1
        ORDER BY chunks.id
        LIMIT :limit
        """,
    )
    suspend fun searchReadyChunks(
        matchQuery: String,
        knowledgeBaseId: String,
        limit: Int,
    ): List<ChunkEntity>
}
