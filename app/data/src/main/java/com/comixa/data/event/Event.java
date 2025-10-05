package com.comixa.data.event;

import java.util.Objects;

public class Event {
    private long id;
    private String title;
    private long timeMillis;
    private String description;

    public Event() { }

    public Event(long id, String title, long timeMillis, String description) {
        this.id = id;
        this.title = title;
        this.timeMillis = timeMillis;
        this.description = description;
    }

    public Event(String title, long timeMillis, String description) {
        this(0L, title, timeMillis, description);
    }

    // getters/setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title != null ? title : ""; }
    public void setTitle(String title) { this.title = title; }

    public long getTimeMillis() { return timeMillis; }
    public void setTimeMillis(long timeMillis) { this.timeMillis = timeMillis; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // equals/hashCode
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return id == event.id;
    }

    @Override public int hashCode() {
        return Objects.hash(id);
    }

    @Override public String toString() {
        return "Event{id=" + id + ", title='" + title + "', time=" + timeMillis + "}";
    }
}
