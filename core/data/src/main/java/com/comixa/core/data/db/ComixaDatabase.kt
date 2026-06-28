package com.comixa.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.comixa.core.data.db.dao.BookmarkDao
import com.comixa.core.data.db.dao.ComicDao
import com.comixa.core.data.db.dao.ProgressDao
import com.comixa.core.data.db.dao.ShelfDao
import com.comixa.core.data.db.dao.WatchedFolderDao
import com.comixa.core.data.db.entity.BookmarkEntity
import com.comixa.core.data.db.entity.ComicBookEntity
import com.comixa.core.data.db.entity.ReadingProgressEntity
import com.comixa.core.data.db.entity.ShelfEntryEntity
import com.comixa.core.data.db.entity.ShelfEntity
import com.comixa.core.data.db.entity.WatchedFolderEntity

@Database(
    entities = [
        ComicBookEntity::class,
        BookmarkEntity::class,
        ReadingProgressEntity::class,
        WatchedFolderEntity::class,
        ShelfEntity::class,
        ShelfEntryEntity::class,
    ],
    version = 6,
    exportSchema = true,
)
abstract class ComixaDatabase : RoomDatabase() {
    abstract fun comicDao(): ComicDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun progressDao(): ProgressDao
    abstract fun watchedFolderDao(): WatchedFolderDao
    abstract fun shelfDao(): ShelfDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE comic_books ADD COLUMN seriesName TEXT")
                db.execSQL("ALTER TABLE comic_books ADD COLUMN issueNumber INTEGER")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS shelves (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, createdAt INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS shelf_entries (shelfId INTEGER NOT NULL, bookId INTEGER NOT NULL, PRIMARY KEY(shelfId, bookId), FOREIGN KEY(shelfId) REFERENCES shelves(id) ON DELETE CASCADE, FOREIGN KEY(bookId) REFERENCES comic_books(id) ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_shelf_entries_bookId ON shelf_entries(bookId)")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reading_progress ADD COLUMN status TEXT NOT NULL DEFAULT 'UNREAD'")
            }
        }
    }
}
