package com.example.minicpm_v_demo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.example.minicpm_v_demo.rag.db.KnowledgeBaseEntity
import com.example.minicpm_v_demo.rag.importer.DocumentImportQueue
import com.example.minicpm_v_demo.rag.naming.KnowledgeBaseNamePolicy
import com.example.minicpm_v_demo.rag.work.WorkManagerRagWorkCoordinator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KnowledgeBaseActivity : StatusBarVisibleActivity() {
    private lateinit var listView: ListView
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
        findViewById<Button>(R.id.btn_create_knowledge_base).setOnClickListener { showCreateDialog() }
        findViewById<Button>(R.id.btn_import_documents).setOnClickListener {
            if (selectedKnowledgeBaseId == null) {
                Toast.makeText(this, R.string.rag_select_knowledge_base_first, Toast.LENGTH_SHORT).show()
            } else {
                openDocuments.launch(SUPPORTED_MIME_TYPES)
            }
        }
        listView.setOnItemClickListener { _, _, position, _ ->
            selectedKnowledgeBaseId = knowledgeBases[position].id
            refreshList()
        }
        loadKnowledgeBases()
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

    private fun refreshList() {
        val labels = knowledgeBases.map { kb ->
            if (kb.id == selectedKnowledgeBaseId) getString(R.string.rag_selected_name, kb.name) else kb.name
        }
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, labels)
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
        private val SUPPORTED_MIME_TYPES = arrayOf(
            "text/*", "application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        )
    }
}
