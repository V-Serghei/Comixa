package com.comixa.core.domain.source

import com.comixa.core.domain.model.ComicBook

interface ComicSource {
    val sourceId: String
    val displayName: String

    suspend fun scan(): List<ComicBook>
    suspend fun getPageCount(book: ComicBook): Int
    suspend fun getPageData(book: ComicBook, pageIndex: Int): ByteArray
    suspend fun getCoverData(book: ComicBook): ByteArray?
}
