package com.comixa.data.rss;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "articles",
        foreignKeys = @ForeignKey(
                entity = SourceEntity.class,
                parentColumns = {"id"},
                childColumns = {"sourceId"},
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index(value = {"sourceId"}),
                @Index(value = {"link"}, unique = false)
        }
)
public class ArticleEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long sourceId;
    private String title;
    private String link;
    private String summary;
    private Long publishedEpochSec;

    public ArticleEntity() { }

    @Ignore
    public ArticleEntity(long sourceId, String title, String link, String summary, Long publishedEpochSec) {
        this.sourceId = sourceId;
        this.title = title;
        this.link = link;
        this.summary = summary;
        this.publishedEpochSec = publishedEpochSec;
    }

    // --- getters/setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getSourceId() { return sourceId; }
    public void setSourceId(long sourceId) { this.sourceId = sourceId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Long getPublishedEpochSec() { return publishedEpochSec; }
    public void setPublishedEpochSec(Long publishedEpochSec) { this.publishedEpochSec = publishedEpochSec; }
}
