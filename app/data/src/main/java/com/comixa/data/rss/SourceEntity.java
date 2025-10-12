package com.comixa.data.rss;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "sources",
        indices = { @Index(value = {"url"}, unique = true) }
)
public class SourceEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String url;

    private String title;

    private Long lastUpdatedEpochSec;

    public SourceEntity() {
        this.url = "";
    }

    @Ignore
    public SourceEntity(@NonNull String url) {
        this.url = url;
    }

    @Ignore
    public SourceEntity(long id, @NonNull String url, String title, Long lastUpdatedEpochSec) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.lastUpdatedEpochSec = lastUpdatedEpochSec;
    }

    // --- getters/setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull public String getUrl() { return url; }
    public void setUrl(@NonNull String url) { this.url = url; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getLastUpdatedEpochSec() { return lastUpdatedEpochSec; }
    public void setLastUpdatedEpochSec(Long lastUpdatedEpochSec) { this.lastUpdatedEpochSec = lastUpdatedEpochSec; }
}
