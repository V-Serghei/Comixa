package com.comixa.data.rss;

import androidx.room.Entity;
import androidx.room.ForeignKey;
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
        indices = { @Index("sourceId"), @Index(value = "link", unique = false) }
)
public class ArticleEntity {
    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    public long sourceId;
    public String title;
    public String link;
    public String summary;
    public Long publishedEpochSec;

    public ArticleEntity() {}

    public ArticleEntity(long sourceId, String title, String link, String summary, Long publishedEpochSec) {
        this.sourceId = sourceId;
        this.title = title;
        this.link = link;
        this.summary = summary;
        this.publishedEpochSec = publishedEpochSec;
    }

    public Long getPublishedEpochSec() { return publishedEpochSec; }
}
