package com.example.minicpm_v_demo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.WorkManager
import com.example.minicpm_v_demo.rag.db.KnowledgeBaseEntity
import com.example.minicpm_v_demo.rag.importer.DocumentImportQueue
import com.example.minicpm_v_demo.rag.naming.KnowledgeBaseNamePolicy
import com.example.minicpm_v_demo.rag.work.WorkManagerRagWorkCoordinator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class KnowledgeBaseActivity : StatusBarVisibleActivity() {
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var adapter: KnowledgeBaseAdapter
    private lateinit var importButton: Button
    private var knowledgeBases = emptyList<KnowledgeBaseEntity>()
    private var selectedKnowledgeBaseId: String? = null

    private val openDocuments = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        val knowledgeBaseId = selectedKnowledgeBaseId ?: return@registerForActivityResult
        if (uris.isEmpty()) return@registerForActivityResult
        lifecycleScope.launch {
            val imported = withContext(Dispatchers.IO) {
                val app = application as MiniCPMApplication
                val queue = DocumentImportQueue(
                    contentResolver = contentResolver,
                    documentDao = app.ragDatabase.documentDao(),
                    workCoordinator = WorkManagerRagWorkCoordinator(WorkManager.getInstance(this@KnowledgeBaseActivity)),
                )
                uris.count { uri -> runCatching { queue.enqueue(uri, knowledgeBaseId) }.isSuccess }
            }
            Toast.makeText(
                this@KnowledgeBaseActivity,
                getString(R.string.rag_documents_queued, imported, uris.size),
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knowledge_base)
        listView = findViewById(R.id.list_knowledge_bases)
        emptyView = findViewById(R.id.tv_empty_knowledge_bases)
        importButton = findViewById(R.id.btn_import_documents)
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener { finish() }
        adapter = KnowledgeBaseAdapter(this, onSelect = { knowledgeBase ->
            selectedKnowledgeBaseId = knowledgeBase.id
            requestRefresh()
        }, onDelete = ::showDeleteConfirmation)
        listView.adapter = adapter
        findViewById<Button>(R.id.btn_create_knowledge_base).setOnClickListener { showCreateDialog() }
        importButton.setOnClickListener {
            if (selectedKnowledgeBaseId == null) {
                Toast.makeText(this, R.string.rag_select_knowledge_base_first, Toast.LENGTH_SHORT).show()
            } else {
                openDocuments.launch(SUPPORTED_MIME_TYPES)
            }
        }
        loadKnowledgeBases()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    refreshList()
                    delay(PROGRESS_REFRESH_MS)
                }
            }
        }
    }

    private fun loadKnowledgeBases() {
        lifecycleScope.launch {
            knowledgeBases = withContext(Dispatchers.IO) {
                (application as MiniCPMApplication).ragDatabase.knowledgeBaseDao().findAll()
            }
            if (selectedKnowledgeBaseId !in knowledgeBases.map { it.id }) {
                selectedKnowledgeBaseId = knowledgeBases.firstOrNull()?.id
            }
            refreshList()
        }
    }

    private fun requestRefresh() {
        lifecycleScope.launch {
            refreshList()
        }
    }

    private suspend fun refreshList() {
        val app = application as MiniCPMApplication
        val items = withContext(Dispatchers.IO) {
            knowledgeBases.map { kb ->
                val documents = app.ragDatabase.documentDao().findByKnowledgeBase(kb.id)
                KnowledgeBaseListItem(kb, documents, kb.id == selectedKnowledgeBaseId)
            }
        }
        adapter.submitItems(items)
        val empty = items.isEmpty()
        emptyView.visibility = if (empty) android.view.View.VISIBLE else android.view.View.GONE
        listView.visibility = if (empty) android.view.View.GONE else android.view.View.VISIBLE
        importButton.isEnabled = selectedKnowledgeBaseId != null
    }

    private fun showDeleteConfirmation(knowledgeBase: KnowledgeBaseEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.rag_delete_knowledge_base)
            .setMessage(getString(R.string.rag_delete_knowledge_base_confirm, knowledgeBase.name))
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ -> deleteKnowledgeBase(knowledgeBase) }
            .show()
    }

    private fun deleteKnowledgeBase(knowledgeBase: KnowledgeBaseEntity) {
        lifecycleScope.launch {
            val app = application as MiniCPMApplication
            withContext(Dispatchers.IO) {
                val documents = app.ragDatabase.documentDao().findByKnowledgeBase(knowledgeBase.id)
                val coordinator = WorkManagerRagWorkCoordinator(WorkManager.getInstance(this@KnowledgeBaseActivity))
                documents.forEach { document ->
                    coordinator.cancel(document.id).result.get()
                    if (isSafePrivateFileName(document.privateFileName)) {
                        com.example.minicpm_v_demo.rag.crypto.RagTempFileCleaner
                            .stagingDirectory(noBackupFilesDir)
                            .resolve(document.privateFileName)
                            .delete()
                    }
                }
                app.ragDatabase.knowledgeBaseDao().deleteById(knowledgeBase.id)
            }
            if (selectedKnowledgeBaseId == knowledgeBase.id) selectedKnowledgeBaseId = null
            Toast.makeText(this@KnowledgeBaseActivity, R.string.rag_knowledge_base_deleted, Toast.LENGTH_SHORT).show()
            loadKnowledgeBases()
        }
    }

    private fun showCreateDialog() {
        val input = EditText(this).apply { maxLines = 1; hint = getString(R.string.rag_knowledge_base_name_hint) }
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.rag_create_knowledge_base)
            .setView(input)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.confirm, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val validated = runCatching { KnowledgeBaseNamePolicy.validateAndNormalize(input.text.toString()) }
                    .getOrElse {
                        input.error = getString(R.string.rag_invalid_knowledge_base_name)
                        return@setOnClickListener
                    }
                lifecycleScope.launch {
                    val id = UUID.randomUUID().toString()
                    val timestamp = System.currentTimeMillis()
                    val inserted = runCatching {
                        withContext(Dispatchers.IO) {
                            (application as MiniCPMApplication).ragDatabase.knowledgeBaseDao().insert(
                                KnowledgeBaseEntity(id, validated.displayName, validated.normalizedName, timestamp, timestamp),
                            )
                        }
                    }.isSuccess
                    if (inserted) {
                        selectedKnowledgeBaseId = id
                        dialog.dismiss()
                        loadKnowledgeBases()
                    } else {
                        input.error = getString(R.string.rag_duplicate_knowledge_base_name)
                    }
                }
            }
        }
        dialog.show()
    }

    companion object {
        private const val PROGRESS_REFRESH_MS = 1_000L
        private val SUPPORTED_MIME_TYPES = arrayOf(
            "text/*", "application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        )

        private fun isSafePrivateFileName(name: String): Boolean =
            File(name).name == name && name.endsWith(".src.enc")
    }
}
