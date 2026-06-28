package com.comixa.core.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.comixa.core.domain.model.ReadingProgress
import com.comixa.core.domain.model.ReadingStatus

@Entity(
    tableName = "reading_progress",
    foreignKeys = [
        ForeignKey(
            entity = ComicBookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class ReadingProgressEntity(
    @PrimaryKey val bookId: Long,
    val currentPage: Int,
    val totalPages: Int,
    val lastReadAt: Long = 0L,
    val status: String = ReadingStatus.UNREAD.name,
) {
    fun toDomain() = ReadingProgress(
        bookId = bookId,
        currentPage = currentPage,
        totalPages = totalPages,
        lastReadAt = lastReadAt,
        status = ReadingStatus.valueOf(status),
    )

    companion object {
        fun fromDomain(p: ReadingProgress) = ReadingProgressEntity(
            bookId = p.bookId,
            currentPage = p.currentPage,
            totalPages = p.totalPages,
            lastReadAt = p.lastReadAt,
            status = p.status.name,
        )
    }
}
