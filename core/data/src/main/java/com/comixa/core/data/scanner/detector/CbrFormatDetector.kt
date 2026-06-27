package com.comixa.core.data.scanner.detector

import com.comixa.core.data.scanner.FormatDetector
import com.comixa.core.domain.model.ComicFormat
import javax.inject.Inject

class CbrFormatDetector @Inject constructor() : FormatDetector {

    override val priority: Int = 10
    override val displayName: String = "CBR / RAR"

    private val extensions = setOf("cbr", "rar")
    private val mimeTypes = setOf(
        "application/vnd.comicbook-rar",
        "application/x-cbr",
        "application/x-rar-compressed",
        "application/vnd.rar",
    )

    override fun canHandle(fileName: String, mimeType: String): Boolean {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return ext in extensions || mimeType in mimeTypes
    }

    override fun format(): ComicFormat = ComicFormat.CBR
}
