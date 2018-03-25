package com.example.hanyu.mypicturejournal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by hanyu on 3/10/2018.
 */
@Entity
public class Image {
    @PrimaryKey(autoGenerate =  true)
    public int SQLiteKey; //Only for SQLite database, not used anywhere else

    private String mFilePath;
    private String mEmotion;
    private String mFavourite;
    private String mMonth;
    private int mDay;

    //All getters and setters are for SQLite database and accessed on a different level of abstraction
    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public String getEmotion() {
        return mEmotion;
    }

    public void setEmotion(String emotion) {
        this.mEmotion = emotion;
    }

    public String getFavourite() {
        return mFavourite;
    }

    public void setFavourite(String favourite) {
        this.mFavourite = favourite;
    }

    public String getMonth() {
        return mMonth;
    }

    public void setMonth(String mMonth) {
        this.mMonth = mMonth;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int mDay) {
        this.mDay = mDay;
    }

    public Image(String filePath, String emotion, String favourite, String month, int day) {
        this.mFilePath = filePath;
        this.mEmotion = emotion;
        this.mFavourite = favourite;
        this.mMonth = month;
        this.mDay = day;
    }
}
