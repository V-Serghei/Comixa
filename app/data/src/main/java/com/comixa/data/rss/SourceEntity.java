package com.comixa.data.rss;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "sources",
        indices = {@Index(value = {"url"}, unique = true)}
)
public class SourceEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String url;
    public String title;
    public Long lastUpdatedEpochSec;

    public SourceEntity() {}
    public SourceEntity(String url) {
        this.url = url;
    }
    public SourceEntity(String url, String title, Long lastUpdatedEpochSec) {
        this.url = url;
        this.title = title;
        this.lastUpdatedEpochSec = lastUpdatedEpochSec;
    }

    public long getId() { return id; }
    public String getUrl() { return url; }
    public String getTitle() { return title; }
    public Long getLastUpdatedEpochSec() { return lastUpdatedEpochSec; }
}
