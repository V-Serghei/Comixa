package com.comixa.core.domain.model

data class ComicPageKey(
    val filePath: String,
    val pageIndex: Int,
    val format: ComicFormat,
)
