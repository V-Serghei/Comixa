package com.comixa.core.domain.model

data class ComicBook(
    val id: Long = 0,
    val title: String,
    val filePath: String,
    val format: ComicFormat,
    val pageCount: Int = 0,
    val coverPath: String? = null,
    val addedAt: Long = 0L,
)
