package com.example.hanyu.mypicturejournal;

/**
 * Created by hanyu on 3/10/2018.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Image.class}, version = 3)
public abstract class ImageDatabase extends RoomDatabase {
    public abstract DaoAccessImage daoAccessImage();
    private static final String DB_NAME = "businessDatabase.db";
    private static volatile ImageDatabase instance;

    static synchronized ImageDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static ImageDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                ImageDatabase.class,
                DB_NAME).build();
    }
}