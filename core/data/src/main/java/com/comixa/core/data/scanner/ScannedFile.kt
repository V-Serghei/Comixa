package com.comixa.core.data.scanner

import android.net.Uri
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.model.ComicFormat

data class ScannedFile(
    val uri: Uri,
    val fileName: String,
    val format: ComicFormat,
    val sizeBytes: Long = 0L,
) {
    fun toComicBook(addedAt: Long): ComicBook {
        val parsed = ComicTitleParser.parse(fileName)
        return ComicBook(
            title = parsed.displayTitle,
            seriesName = parsed.seriesName,
            issueNumber = parsed.issueNumber,
            filePath = uri.toString(),
            format = format,
            addedAt = addedAt,
        )
    }
}
