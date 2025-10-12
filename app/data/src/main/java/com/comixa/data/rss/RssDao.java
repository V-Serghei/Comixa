package com.comixa.data.rss;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;
import androidx.room.OnConflictStrategy;

import java.util.List;
import kotlinx.coroutines.flow.Flow;

@Dao
public interface RssDao {
    // Sources
    @Query("SELECT * FROM sources ORDER BY id DESC")
    List<SourceEntity> getAllSources();

    @Query("SELECT * FROM sources ORDER BY id DESC")
    Flow<List<SourceEntity>> observeSources();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertSource(SourceEntity source);

    @Query("SELECT * FROM sources WHERE id = :id LIMIT 1")
    SourceEntity getSourceById(long id);

    @Query("SELECT * FROM sources WHERE url = :url LIMIT 1")
    SourceEntity getSourceByUrl(String url);

    @Update
    void updateSource(SourceEntity source);

    @Query("UPDATE sources SET title = :title, lastUpdatedEpochSec = :lastUpdated WHERE id = :id")
    void updateSourceMeta(long id, String title, Long lastUpdated);

    @Delete
    void deleteSource(SourceEntity source);

    @Query("SELECT * FROM articles WHERE sourceId = :sourceId " +
            "ORDER BY COALESCE(publishedEpochSec, 0) DESC, id DESC")
    Flow<List<ArticleEntity>> observeArticles(long sourceId);

    @Query("DELETE FROM articles WHERE sourceId = :sourceId")
    void clearArticlesForSource(long sourceId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticles(List<ArticleEntity> items);

    @Transaction
    default void replaceArticles(long sourceId, List<ArticleEntity> items) {
        clearArticlesForSource(sourceId);
        insertArticles(items);
    }
}

