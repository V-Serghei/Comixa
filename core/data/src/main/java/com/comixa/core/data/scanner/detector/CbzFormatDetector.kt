package com.comixa.core.data.scanner.detector

import com.comixa.core.data.scanner.FormatDetector
import com.comixa.core.domain.model.ComicFormat
import javax.inject.Inject

class CbzFormatDetector @Inject constructor() : FormatDetector {

    override val priority: Int = 10
    override val displayName: String = "CBZ / ZIP"

    private val extensions = setOf("cbz", "zip")
    private val mimeTypes = setOf(
        "application/vnd.comicbook+zip",
        "application/x-cbz",
        "application/zip",
        "application/x-zip-compressed",
    )

    override fun canHandle(fileName: String, mimeType: String): Boolean {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return ext in extensions || mimeType in mimeTypes
    }

    override fun format(): ComicFormat = ComicFormat.CBZ
}
