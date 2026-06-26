package com.comixa.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.model.ComicFormat

@Entity(tableName = "comic_books")
data class ComicBookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val filePath: String,
    val format: String,
    val pageCount: Int = 0,
    val coverPath: String? = null,
    val addedAt: Long = 0L,
) {
    fun toDomain() = ComicBook(
        id = id,
        title = title,
        filePath = filePath,
        format = ComicFormat.valueOf(format),
        pageCount = pageCount,
        coverPath = coverPath,
        addedAt = addedAt,
    )

    companion object {
        fun fromDomain(book: ComicBook) = ComicBookEntity(
            id = book.id,
            title = book.title,
            filePath = book.filePath,
            format = book.format.name,
            pageCount = book.pageCount,
            coverPath = book.coverPath,
            addedAt = book.addedAt,
        )
    }
}
