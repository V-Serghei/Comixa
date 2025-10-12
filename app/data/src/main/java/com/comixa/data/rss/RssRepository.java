package com.comixa.data.rss;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kotlinx.coroutines.flow.Flow;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RssRepository {

    private final RssDao dao;
    private final OkHttpClient client;

    public RssRepository(@NonNull Context ctx) {
        RssDatabase db = RssDatabase.get(ctx);
        this.dao = db.rssDao();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    // Sources
    public Flow<List<SourceEntity>> observeSources() {
        return dao.observeSources();
    }

    @WorkerThread
    public void addSourceAndSync(@NonNull String url) {
        SourceEntity existing = dao.getSourceByUrl(url);
        long id;
        if (existing != null) {
            id = existing.getId();
        } else {
            SourceEntity toInsert = new SourceEntity(url);
            id = dao.insertSource(toInsert);
        }
        SourceEntity source = dao.getSourceById(id);
        if (source == null) return;
        refreshSource(source.getId());
    }

    @WorkerThread
    public void deleteSource(@NonNull SourceEntity source) {
        dao.deleteSource(source);
    }

    @WorkerThread
    public void refreshSource(long sourceId) {
        SourceEntity source = dao.getSourceById(sourceId);
        if (source == null) return;

        SimpleRssParser.ParsedFeed parsed = fetchAndParse(source.getUrl());
        if (parsed == null) return;

        List<ArticleEntity> items = new ArrayList<>();
        List<SimpleRssParser.ParsedItem> parsedItems = parsed.getItems();
        if (parsedItems != null) {
            for (SimpleRssParser.ParsedItem it : parsedItems) {
                String link = it.getLink();
                if (link != null) {
                    ArticleEntity e = new ArticleEntity(
                            sourceId,
                            it.getTitle() != null ? it.getTitle() : link,
                            link,
                            it.getSummary(),
                            it.getPublishedEpochSec()
                    );
                    items.add(e);
                }
            }
        }

        dao.replaceArticles(sourceId, items);

        long maxPublished = 0L;
        for (ArticleEntity e : items) {
            Long p = e.getPublishedEpochSec();
            if (p != null && p > maxPublished) maxPublished = p;
        }

        String newTitle = (parsed.getTitle() != null) ? parsed.getTitle() : source.getUrl();
        Long newLastUpdated = (maxPublished > 0) ? maxPublished : null;

        dao.updateSourceMeta(sourceId, newTitle, newLastUpdated);
    }

    // Articles
    public Flow<List<ArticleEntity>> observeArticles(long sourceId) {
        return dao.observeArticles(sourceId);
    }

    // HTTP + parse
    @WorkerThread
    private SimpleRssParser.ParsedFeed fetchAndParse(@NonNull String url) {
        Request request = new Request.Builder().url(url).get().build();
        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful()) return null;
            if (resp.body() == null) return null;
            try (InputStream is = resp.body().byteStream()) {
                return SimpleRssParser.parse(is);
            }
        } catch (Throwable t) {
            return null;
        }
    }
}
