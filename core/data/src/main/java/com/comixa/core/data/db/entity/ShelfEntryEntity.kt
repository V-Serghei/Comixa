package com.comixa.core.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "shelf_entries",
    primaryKeys = ["shelfId", "bookId"],
    foreignKeys = [
        ForeignKey(
            entity = ShelfEntity::class,
            parentColumns = ["id"],
            childColumns = ["shelfId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ComicBookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("bookId")],
)
data class ShelfEntryEntity(
    val shelfId: Long,
    val bookId: Long,
)
