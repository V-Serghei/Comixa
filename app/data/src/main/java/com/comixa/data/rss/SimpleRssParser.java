package com.comixa.data.rss;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class SimpleRssParser {





    private SimpleRssParser() { }

    public static ParsedFeed parse(InputStream input) throws IOException, XmlPullParserException {
        try (InputStream ins = input) {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(ins, null);
            parser.nextTag();
            String root = parser.getName();
            return "feed".equalsIgnoreCase(root) ? parseAtom(parser) : parseRss(parser);
        }
    }

    // Atom
    private static ParsedFeed parseAtom(XmlPullParser parser) throws IOException, XmlPullParserException {
        String feedTitle = null;
        java.util.List<ParsedItem> items = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "feed");
        while (!(parser.next() == XmlPullParser.END_TAG && "feed".equals(parser.getName()))) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            switch (parser.getName()) {
                case "title":
                    feedTitle = readText(parser);
                    break;
                case "entry":
                    items.add(readAtomEntry(parser));
                    break;
                default:
                    skip(parser);
            }
        }
        return new ParsedFeed(feedTitle, items);
    }

    private static ParsedItem readAtomEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "entry");
        String title = null, link = null, summary = null;
        Long published = null;
        while (!(parser.next() == XmlPullParser.END_TAG && "entry".equals(parser.getName()))) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            switch (parser.getName()) {
                case "title": title = readText(parser); break;
                case "summary": summary = readText(parser); break;
                case "updated":
                case "published": published = parseDate(readText(parser)); break;
                case "link":
                    String rel = parser.getAttributeValue(null, "rel");
                    String href = parser.getAttributeValue(null, "href");
                    if (href != null && (rel == null || "alternate".equals(rel))) link = href;
                    skip(parser);
                    break;
                default: skip(parser);
            }
        }
        return new ParsedItem(title, link, summary, published);
    }

    // RSS2
    private static ParsedFeed parseRss(XmlPullParser parser) throws IOException, XmlPullParserException {
        String feedTitle = null;
        java.util.List<ParsedItem> items = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if ("channel".equals(parser.getName())) {
                while (!(parser.next() == XmlPullParser.END_TAG && "channel".equals(parser.getName()))) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) continue;
                    switch (parser.getName()) {
                        case "title": feedTitle = readText(parser); break;
                        case "item": items.add(readRssItem(parser)); break;
                        default: skip(parser);
                    }
                }
                break;
            } else {
                skip(parser);
            }
        }
        return new ParsedFeed(feedTitle, items);
    }

    private static ParsedItem readRssItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "item");
        String title = null, link = null, desc = null;
        Long pubDate = null;
        while (!(parser.next() == XmlPullParser.END_TAG && "item".equals(parser.getName()))) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            switch (parser.getName()) {
                case "title": title = readText(parser); break;
                case "link": link = readText(parser); break;
                case "description": desc = readText(parser); break;
                case "pubDate": pubDate = parseDate(readText(parser)); break;
                default: skip(parser);
            }
        }
        return new ParsedItem(title, link, desc, pubDate);
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText() != null ? parser.getText() : "";
            parser.nextTag();
        }
        return result.trim();
    }

    private static void skip(XmlPullParser parser) throws IOException, XmlPullParserException {
        if (parser.getEventType() != XmlPullParser.START_TAG) return;
        int depth = 1;
        while (depth != 0) {
            int type = parser.next();
            if (type == XmlPullParser.END_TAG) depth--;
            else if (type == XmlPullParser.START_TAG) depth++;
        }
    }

    private static final String[] FORMATS = new String[] {
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssXXX"
    };

    private static Long parseDate(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        for (String f : FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(f, Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                return Objects.requireNonNull(sdf.parse(text)).getTime() / 1000L;
            } catch (ParseException | RuntimeException ignored) { }
        }
        return null;
    }
}
