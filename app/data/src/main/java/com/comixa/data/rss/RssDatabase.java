package com.comixa.data.rss;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {SourceEntity.class, ArticleEntity.class},
        version = 2,
        exportSchema = false
)
public abstract class RssDatabase extends RoomDatabase {
    public abstract RssDao rssDao();

    private static volatile RssDatabase INSTANCE;
    public static RssDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (RssDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            ctx.getApplicationContext(),
                            RssDatabase.class,
                            "lab3_rss.db"
                    )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
