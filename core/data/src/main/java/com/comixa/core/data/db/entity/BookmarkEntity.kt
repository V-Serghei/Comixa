package com.comixa.core.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.comixa.core.domain.model.Bookmark

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = ComicBookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("bookId")],
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val pageIndex: Int,
    val label: String = "",
    val createdAt: Long = 0L,
) {
    fun toDomain() = Bookmark(id, bookId, pageIndex, label, createdAt)

    companion object {
        fun fromDomain(b: Bookmark) = BookmarkEntity(b.id, b.bookId, b.pageIndex, b.label, b.createdAt)
    }
}
