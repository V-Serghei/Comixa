package com.comixa.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.comixa.core.data.db.dao.BookmarkDao
import com.comixa.core.data.db.dao.ComicDao
import com.comixa.core.data.db.dao.ProgressDao
import com.comixa.core.data.db.dao.WatchedFolderDao
import com.comixa.core.data.db.entity.BookmarkEntity
import com.comixa.core.data.db.entity.ComicBookEntity
import com.comixa.core.data.db.entity.ReadingProgressEntity
import com.comixa.core.data.db.entity.WatchedFolderEntity

@Database(
    entities = [
        ComicBookEntity::class,
        BookmarkEntity::class,
        ReadingProgressEntity::class,
        WatchedFolderEntity::class,
    ],
    version = 4,
    exportSchema = true,
)
abstract class ComixaDatabase : RoomDatabase() {
    abstract fun comicDao(): ComicDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun progressDao(): ProgressDao
    abstract fun watchedFolderDao(): WatchedFolderDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE comic_books ADD COLUMN seriesName TEXT")
                db.execSQL("ALTER TABLE comic_books ADD COLUMN issueNumber INTEGER")
            }
        }
    }
}
