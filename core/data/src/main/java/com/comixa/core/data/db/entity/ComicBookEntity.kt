package com.comixa.core.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.model.ComicFormat

@Entity(
    tableName = "comic_books",
    indices = [Index(value = ["filePath"], unique = true)],
)
data class ComicBookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val seriesName: String? = null,
    val issueNumber: Int? = null,
    val filePath: String,
    val format: String,
    val pageCount: Int = 0,
    val coverPath: String? = null,
    val addedAt: Long = 0L,
) {
    fun toDomain() = ComicBook(
        id = id,
        title = title,
        seriesName = seriesName,
        issueNumber = issueNumber,
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
            seriesName = book.seriesName,
            issueNumber = book.issueNumber,
            filePath = book.filePath,
            format = book.format.name,
            pageCount = book.pageCount,
            coverPath = book.coverPath,
            addedAt = book.addedAt,
        )
    }
}
