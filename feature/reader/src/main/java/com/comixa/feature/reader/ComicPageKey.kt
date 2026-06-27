package com.comixa.feature.reader

import com.comixa.core.domain.model.ComicFormat

data class ComicPageKey(
    val filePath: String,
    val pageIndex: Int,
    val format: ComicFormat,
)
