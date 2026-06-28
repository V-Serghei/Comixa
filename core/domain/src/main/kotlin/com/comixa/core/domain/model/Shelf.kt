package com.comixa.core.domain.model

data class Shelf(
    val id: Long = 0,
    val name: String,
    val createdAt: Long = 0L,
    val bookCount: Int = 0,
)
