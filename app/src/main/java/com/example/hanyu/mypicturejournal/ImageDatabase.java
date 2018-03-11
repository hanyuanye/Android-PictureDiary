package com.example.hanyu.mypicturejournal;

/**
 * Created by hanyu on 3/10/2018.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Image.class}, version = 1)
public abstract class ImageDatabase extends RoomDatabase {
    public abstract DaoAccessImage daoAccessImage();
}