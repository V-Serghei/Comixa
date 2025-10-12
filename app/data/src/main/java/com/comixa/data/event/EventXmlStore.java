package com.comixa.data.event;

import android.content.Context;
import android.util.Xml;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EventXmlStore {



    private final Context context;

    public EventXmlStore(Context context) {
        this.context = context.getApplicationContext();
    }

    public synchronized List<Event> getAll() {
        List<Event> result = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();

        try (InputStreamReader reader =
                     new InputStreamReader(context.openFileInput(Variables.FILE_NAME), StandardCharsets.UTF_8)) {

            parser.setInput(reader);
            int type = parser.getEventType();

            while (type != XmlPullParser.END_DOCUMENT) {
                if (type == XmlPullParser.START_TAG && Variables.TAG_EVENT.equals(parser.getName())) {
                    long id   = parseLongOrDefault(parser.getAttributeValue(null, Variables.A_ID), 0L);
                    String title = nonNull(parser.getAttributeValue(null, Variables.A_TITLE));
                    long time = parseLongOrDefault(parser.getAttributeValue(null, Variables.A_TIME), 0L);
                    String desc = parser.getAttributeValue(null, Variables.A_DESC);

                    Event e = new Event(id, title, time, desc);
                    result.add(e);
                }
                type = parser.next();
            }
        } catch (FileNotFoundException notExists) {
        } catch (Exception e) {
        }
        return result;
    }

    public synchronized Event add(Event event) {
        List<Event> all = new ArrayList<>(getAll());
        long newId = nextId(all);
        Event withId = new Event(newId, event.getTitle(), event.getTimeMillis(), event.getDescription());
        all.add(withId);
        writeAll(all);
        return withId;
    }

    public synchronized boolean update(Event event) {
        if (event.getId() == 0L) return false;
        List<Event> all = new ArrayList<>(getAll());
        int idx = indexOfId(all, event.getId());
        if (idx < 0) return false;
        all.set(idx, event);
        writeAll(all);
        return true;
    }

    public synchronized boolean delete(long id) {
        List<Event> all = new ArrayList<>(getAll());
        int idx = indexOfId(all, id);
        if (idx < 0) return false;
        all.remove(idx);
        writeAll(all);
        return true;
    }


    private void writeAll(List<Event> list) {
        XmlSerializer serializer = Xml.newSerializer();
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(context.openFileOutput(Variables.FILE_NAME, Context.MODE_PRIVATE),
                             StandardCharsets.UTF_8)) {

            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, Variables.TAG_ROOT);

            for (Event e : list) {
                serializer.startTag(null, Variables.TAG_EVENT);
                serializer.attribute(null, Variables.A_ID, String.valueOf(e.getId()));
                serializer.attribute(null, Variables.A_TITLE, nonNull(e.getTitle()));
                serializer.attribute(null, Variables.A_TIME, String.valueOf(e.getTimeMillis()));
                if (e.getDescription() != null && !e.getDescription().isEmpty()) {
                    serializer.attribute(null, Variables.A_DESC, e.getDescription());
                }
                serializer.endTag(null, Variables.TAG_EVENT);
            }

            serializer.endTag(null, Variables.TAG_ROOT);
            serializer.endDocument();
            writer.flush();
        } catch (Exception e) {
        }
    }

    private static String nonNull(String s) {
        return (s == null) ? "" : s;
    }

    private static long parseLongOrDefault(String s, long def) {
        if (s == null) return def;
        try { return Long.parseLong(s); } catch (NumberFormatException e) { return def; }
    }

    private static long nextId(List<Event> list) {
        long max = 0L;
        for (Event e : list) if (e.getId() > max) max = e.getId();
        return max + 1L;
    }

    private static int indexOfId(List<Event> list, long id) {
        for (int i = 0; i < list.size(); i++) if (list.get(i).getId() == id) return i;
        return -1;
    }
}
