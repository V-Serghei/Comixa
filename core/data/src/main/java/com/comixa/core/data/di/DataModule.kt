package com.comixa.core.data.di

import android.content.Context
import androidx.room.Room
import com.comixa.core.data.db.ComixaDatabase
import com.comixa.core.data.repository.BookmarkRepositoryImpl
import com.comixa.core.data.repository.ComicRepositoryImpl
import com.comixa.core.data.repository.ProgressRepositoryImpl
import com.comixa.core.data.repository.ShelfRepositoryImpl
import com.comixa.core.data.repository.WatchedFolderRepositoryImpl
import com.comixa.core.data.source.LocalFolderSource
import com.comixa.core.domain.repository.BookmarkRepository
import com.comixa.core.domain.repository.ComicRepository
import com.comixa.core.domain.repository.ProgressRepository
import com.comixa.core.domain.repository.ShelfRepository
import com.comixa.core.domain.repository.WatchedFolderRepository
import com.comixa.core.domain.source.ComicSource
import com.comixa.core.domain.usecase.ScanLibraryUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ComixaDatabase =
        Room.databaseBuilder(context, ComixaDatabase::class.java, "comixa.db")
            .addMigrations(ComixaDatabase.MIGRATION_3_4, ComixaDatabase.MIGRATION_4_5)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideComicDao(db: ComixaDatabase) = db.comicDao()
    @Provides fun provideBookmarkDao(db: ComixaDatabase) = db.bookmarkDao()
    @Provides fun provideProgressDao(db: ComixaDatabase) = db.progressDao()
    @Provides fun provideWatchedFolderDao(db: ComixaDatabase) = db.watchedFolderDao()
    @Provides fun provideShelfDao(db: ComixaDatabase) = db.shelfDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindComicRepository(impl: ComicRepositoryImpl): ComicRepository

    @Binds @Singleton
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds @Singleton
    abstract fun bindComicSource(impl: LocalFolderSource): ComicSource

    @Binds @Singleton
    abstract fun bindWatchedFolderRepository(impl: WatchedFolderRepositoryImpl): WatchedFolderRepository

    @Binds @Singleton
    abstract fun bindShelfRepository(impl: ShelfRepositoryImpl): ShelfRepository
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideScanLibraryUseCase(
        source: ComicSource,
        repository: ComicRepository,
    ): ScanLibraryUseCase = ScanLibraryUseCase(source, repository)
}
