package com.example.minicpm_v_demo.rag.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RagDatabaseDaoTest {
    private lateinit var database: RagDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RagDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun retrievalOnlyReturnsChunksFromReadyEnabledDocuments() = runBlocking {
        val now = 1_723_200_000_000L
        database.knowledgeBaseDao().insert(
            KnowledgeBaseEntity(
                id = "kb-1",
                name = "Office",
                normalizedName = "office",
                createdAt = now,
                updatedAt = now,
            ),
        )
        database.documentDao().upsert(document("ready", DocumentStatus.READY, now))
        database.documentDao().upsert(document("parsing", DocumentStatus.PARSING, now))
        database.chunkDao().insertAll(
            listOf(
                chunk(id = 1, documentId = "ready", text = "approved contract amount"),
                chunk(id = 2, documentId = "parsing", text = "draft contract amount"),
            ),
        )

        val results = database.chunkDao().searchReadyChunks("contract", "kb-1", 10)

        assertEquals(listOf(1L), results.map { it.id })
    }

    @Test
    fun deletingKnowledgeBaseCascadesDocumentsChunksAndFtsRows() = runBlocking {
        val now = 1_723_200_000_000L
        database.knowledgeBaseDao().insert(
            KnowledgeBaseEntity(
                id = "kb-delete",
                name = "Delete me",
                normalizedName = "delete me",
                createdAt = now,
                updatedAt = now,
            ),
        )
        database.documentDao().upsert(document("doc-delete", DocumentStatus.READY, now, "kb-delete"))
        database.chunkDao().insertAll(listOf(chunk(3, "doc-delete", "confidential payroll", "kb-delete")))

        database.knowledgeBaseDao().deleteById("kb-delete")

        assertTrue(database.documentDao().findByKnowledgeBase("kb-delete").isEmpty())
        assertTrue(database.chunkDao().findByDocument("doc-delete").isEmpty())
        assertTrue(database.chunkDao().searchReadyChunks("payroll", "kb-delete", 10).isEmpty())
    }

    private fun document(
        id: String,
        status: DocumentStatus,
        now: Long,
        knowledgeBaseId: String = "kb-1",
    ) = DocumentEntity(
        id = id,
        knowledgeBaseId = knowledgeBaseId,
        displayName = "$id.txt",
        sourceUri = null,
        privateFileName = "$id.source",
        mimeType = "text/plain",
        detectedType = "text/plain",
        sha256 = id.padEnd(64, '0'),
        sizeBytes = 10,
        status = status,
        createdAt = now,
        updatedAt = now,
    )

    private fun chunk(
        id: Long,
        documentId: String,
        text: String,
        knowledgeBaseId: String = "kb-1",
    ) = ChunkEntity(
        id = id,
        documentId = documentId,
        knowledgeBaseId = knowledgeBaseId,
        ordinal = id.toInt(),
        text = text,
        searchText = text,
        displayName = "$documentId.txt",
        tokenCount = text.length,
        contentSha256 = id.toString().padEnd(64, '0'),
    )
}
