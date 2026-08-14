package com.example.minicpm_v_demo

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.minicpm_v_demo.rag.db.DocumentEntity
import com.example.minicpm_v_demo.rag.db.KnowledgeBaseEntity
import com.example.minicpm_v_demo.rag.ui.KnowledgeBaseDocumentPresentation
import com.google.android.material.card.MaterialCardView

data class KnowledgeBaseListItem(
    val knowledgeBase: KnowledgeBaseEntity,
    val documents: List<DocumentEntity>,
    val selected: Boolean,
)

class KnowledgeBaseAdapter(
    context: Context,
    private val onSelect: (KnowledgeBaseEntity) -> Unit,
    private val onDelete: (KnowledgeBaseEntity) -> Unit,
    private val showDelete: Boolean = true,
) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)
    private var items = emptyList<KnowledgeBaseListItem>()

    fun submitItems(newItems: List<KnowledgeBaseListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): KnowledgeBaseListItem = items[position]
    override fun getItemId(position: Int): Long = items[position].knowledgeBase.id.hashCode().toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_knowledge_base, parent, false)
        val item = getItem(position)
        val card = view.findViewById<MaterialCardView>(R.id.card_knowledge_base)
        val title = view.findViewById<TextView>(R.id.tv_knowledge_base_name)
        val statusContainer = view.findViewById<LinearLayout>(R.id.container_document_status)
        title.text = item.knowledgeBase.name
        card.setCardBackgroundColor(
            ContextCompat.getColor(view.context, if (item.selected) R.color.rag_selected_surface else R.color.surface),
        )
        card.strokeColor = ContextCompat.getColor(
            view.context,
            if (item.selected) R.color.rag_selected_outline else R.color.rag_card_outline,
        )
        card.strokeWidth = view.context.resources.getDimensionPixelSize(
            if (item.selected) R.dimen.rag_selected_stroke else R.dimen.rag_card_stroke,
        )
        card.setOnClickListener { onSelect(item.knowledgeBase) }
        val deleteButton = view.findViewById<ImageButton>(R.id.btn_delete_knowledge_base)
        deleteButton.visibility = if (showDelete) View.VISIBLE else View.GONE
        deleteButton.setOnClickListener {
            onDelete(item.knowledgeBase)
        }

        statusContainer.removeAllViews()
        item.documents.forEach { document ->
            val presentation = KnowledgeBaseDocumentPresentation.from(document.status, document.lastErrorCode)
                ?: return@forEach
            val status = inflater.inflate(R.layout.item_knowledge_base_document_status, statusContainer, false) as TextView
            status.text = when (presentation) {
                KnowledgeBaseDocumentPresentation.Processing ->
                    view.context.getString(R.string.rag_document_processing, document.displayName)
                KnowledgeBaseDocumentPresentation.Uploaded ->
                    view.context.getString(R.string.rag_document_uploaded, document.displayName)
                is KnowledgeBaseDocumentPresentation.Failure ->
                    view.context.getString(R.string.rag_document_failed, document.displayName, presentation.reason)
            }
            val color = when (presentation) {
                KnowledgeBaseDocumentPresentation.Processing -> R.color.rag_status_neutral
                KnowledgeBaseDocumentPresentation.Uploaded -> R.color.rag_status_success
                is KnowledgeBaseDocumentPresentation.Failure -> R.color.rag_status_error
            }
            status.setTextColor(ContextCompat.getColor(view.context, color))
            status.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    view.context,
                    when (presentation) {
                        KnowledgeBaseDocumentPresentation.Processing -> R.color.rag_status_neutral_surface
                        KnowledgeBaseDocumentPresentation.Uploaded -> R.color.rag_status_success_surface
                        is KnowledgeBaseDocumentPresentation.Failure -> R.color.rag_status_error_surface
                    },
                ),
            )
            statusContainer.addView(status)
        }
        statusContainer.visibility = if (statusContainer.childCount == 0) View.GONE else View.VISIBLE
        return view
    }
}
