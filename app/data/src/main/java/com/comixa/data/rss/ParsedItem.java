package com.comixa.data.rss;

public  class ParsedItem {
    private final String title;
    private final String link;
    private final String summary;
    private final Long publishedEpochSec;

    public ParsedItem(String title, String link, String summary, Long publishedEpochSec) {
        this.title = title;
        this.link = link;
        this.summary = summary;
        this.publishedEpochSec = publishedEpochSec;
    }
    public String getTitle() { return title; }
    public String getLink() { return link; }
    public String getSummary() { return summary; }
    public Long getPublishedEpochSec() { return publishedEpochSec; }
}