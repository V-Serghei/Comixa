package com.comixa.core.domain.model

data class Bookmark(
    val id: Long = 0,
    val bookId: Long,
    val pageIndex: Int,
    val label: String = "",
    val createdAt: Long = 0L,
)
