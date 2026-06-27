package com.comixa.core.domain.source

import com.comixa.core.domain.model.ComicBook
import kotlinx.coroutines.flow.Flow

interface ComicSource {
    val sourceId: String
    val displayName: String

    fun scan(): Flow<ComicBook>
    suspend fun getPageCount(book: ComicBook): Int
    suspend fun getPageData(book: ComicBook, pageIndex: Int): ByteArray
    suspend fun getCoverData(book: ComicBook): ByteArray?
}
