package com.comixa.data.rss;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    public Flow<List<SourceEntity>> observeSources() { return dao.observeSources(); }
    public List<SourceEntity> getAllSources() { return dao.getAllSources(); }

    @WorkerThread
    public void addSourceAndSync(@NonNull String url) {
        SourceEntity existing = dao.getSourceByUrl(url);
        long id = (existing != null) ? existing.getId() : dao.insertSource(new SourceEntity(url));
        SourceEntity source = dao.getSourceById(id);
        if (source == null) return;
        refreshSource(id);
    }

    @WorkerThread
    public void addSourceAndSyncWithProgress(@NonNull String url, @Nullable ProgressReporter reporter) {
        if (reporter != null) reporter.onStart();
        try {
            if (reporter != null) reporter.onProgress(3);
            SourceEntity existing = dao.getSourceByUrl(url);
            long id = (existing != null) ? existing.getId() : dao.insertSource(new SourceEntity(url));

            if (reporter != null) reporter.onProgress(10);
            SourceEntity source = dao.getSourceById(id);
            if (source == null) return;

            if (reporter != null) reporter.onProgress(20);
            refreshSourceWithProgress(source.getId(), reporter);

            if (reporter != null) reporter.onProgress(100);
        } finally {
            if (reporter != null) reporter.onStop();
        }
    }

    @WorkerThread
    public void refreshSource(long sourceId) {
        SourceEntity source = dao.getSourceById(sourceId);
        if (source == null) return;

        ParsedFeed parsed = fetchAndParse(source.getUrl());
        if (parsed == null) return;

        List<ArticleEntity> items = mapParsed(parsed, sourceId);
        dao.replaceArticles(sourceId, items);

        long maxPublished = getMaxPublished(items);
        String newTitle = (parsed.getTitle() != null) ? parsed.getTitle() : source.getUrl();
        Long newLastUpdated = (maxPublished > 0) ? maxPublished : null;

        dao.updateSourceMeta(sourceId, newTitle, newLastUpdated);
    }

    @WorkerThread
    public void refreshSourceWithProgress(long sourceId, @Nullable ProgressReporter reporter) {
        if (reporter != null) reporter.onStart();
        try {
            if (reporter != null) reporter.onProgress(5);

            SourceEntity source = dao.getSourceById(sourceId);
            if (source == null) return;

            if (reporter != null) reporter.onProgress(15);
            ParsedFeed parsed = fetchAndParse(source.getUrl());
            if (parsed == null) return;

            if (reporter != null) reporter.onProgress(50);
            List<ArticleEntity> items = mapParsed(parsed, sourceId);

            if (reporter != null) reporter.onProgress(70);
            dao.replaceArticles(sourceId, items);

            long maxPublished = getMaxPublished(items);
            String newTitle = parsed.getTitle() != null ? parsed.getTitle() : source.getUrl();
            Long newLastUpdated = (maxPublished > 0) ? maxPublished : null;

            if (reporter != null) reporter.onProgress(90);
            dao.updateSourceMeta(sourceId, newTitle, newLastUpdated);

            if (reporter != null) reporter.onProgress(100);
        } finally {
            if (reporter != null) reporter.onStop();
        }
    }

    // ---------- Articles ----------
    public Flow<List<ArticleEntity>> observeArticles(long sourceId) { return dao.observeArticles(sourceId); }

    // ---------- HTTP + Parse ----------
    @WorkerThread
    private ParsedFeed fetchAndParse(@NonNull String url) {
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

    // ---------- Helpers ----------
    private static List<ArticleEntity> mapParsed(ParsedFeed parsed, long sourceId) {
        List<ArticleEntity> items = new ArrayList<>();
        List<ParsedItem> parsedItems = parsed.getItems();
        if (parsedItems != null) {
            for (ParsedItem it : parsedItems) {
                String link = it.getLink();
                if (link != null) {
                    items.add(new ArticleEntity(
                            sourceId,
                            it.getTitle() != null ? it.getTitle() : link,
                            link,
                            it.getSummary(),
                            it.getPublishedEpochSec()
                    ));
                }
            }
        }
        return items;
    }

    private static long getMaxPublished(List<ArticleEntity> items) {
        long maxPublished = 0L;
        for (ArticleEntity e : items) {
            Long p = e.getPublishedEpochSec();
            if (p != null && p > maxPublished) maxPublished = p;
        }
        return maxPublished;
    }
}
