package com.comixa.core.domain.model

data class ReadingProgress(
    val bookId: Long,
    val currentPage: Int,
    val totalPages: Int,
    val lastReadAt: Long = 0L,
    val status: ReadingStatus = ReadingStatus.UNREAD,
) {
    val percentComplete: Float
        get() = if (totalPages > 0) currentPage.toFloat() / totalPages else 0f

    val isCompleted: Boolean
        get() = status == ReadingStatus.COMPLETED
}
