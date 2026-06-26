package com.comixa.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.comixa.core.data.db.dao.BookmarkDao
import com.comixa.core.data.db.dao.ComicDao
import com.comixa.core.data.db.dao.ProgressDao
import com.comixa.core.data.db.entity.BookmarkEntity
import com.comixa.core.data.db.entity.ComicBookEntity
import com.comixa.core.data.db.entity.ReadingProgressEntity

@Database(
    entities = [ComicBookEntity::class, BookmarkEntity::class, ReadingProgressEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class ComixaDatabase : RoomDatabase() {
    abstract fun comicDao(): ComicDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun progressDao(): ProgressDao
}
