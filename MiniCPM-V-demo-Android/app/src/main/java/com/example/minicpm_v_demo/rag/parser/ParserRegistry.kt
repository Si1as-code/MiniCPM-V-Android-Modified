package com.example.minicpm_v_demo.rag.parser

import java.util.Locale

object ParserRegistry {
    fun forDocument(displayName: String, mimeType: String): DocumentParser {
        val extension = displayName.substringAfterLast('.', "").lowercase(Locale.ROOT)
        val mime = mimeType.substringBefore(';').trim().lowercase(Locale.ROOT)
        return when {
            extension in setOf("md", "markdown") || mime == "text/markdown" -> MarkdownParser()
            extension == "csv" || mime in setOf("text/csv", "application/csv") -> CsvParser()
            extension in setOf("html", "htm") || mime == "text/html" -> HtmlParser()
            extension == "txt" || mime == "text/plain" -> TextParser()
            else -> fail(ParserError.UNSUPPORTED_FORMAT)
        }
    }
}
