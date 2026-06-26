package com.comixa.core.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.comixa.core.domain.model.ReadingProgress

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
) {
    fun toDomain() = ReadingProgress(bookId, currentPage, totalPages, lastReadAt)

    companion object {
        fun fromDomain(p: ReadingProgress) =
            ReadingProgressEntity(p.bookId, p.currentPage, p.totalPages, p.lastReadAt)
    }
}
