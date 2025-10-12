package com.comixa.data.rss;

public  class ParsedFeed {
    private final String title;
    private final java.util.List<ParsedItem> items;

    public ParsedFeed(String title, java.util.List<ParsedItem> items) {
        this.title = title;
        this.items = items;
    }
    public String getTitle() { return title; }
    public java.util.List<ParsedItem> getItems() { return items; }
}