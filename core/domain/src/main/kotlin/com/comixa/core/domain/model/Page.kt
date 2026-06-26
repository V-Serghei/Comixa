package com.comixa.core.domain.model

data class Page(
    val index: Int,
    val bookId: Long,
    val entryName: String? = null,
)
