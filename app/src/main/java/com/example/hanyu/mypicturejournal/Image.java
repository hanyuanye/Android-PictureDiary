package com.example.hanyu.mypicturejournal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by hanyu on 3/10/2018.
 */
@Entity
public class Image {
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Image(String filePath, String emotion, String favourite, int month, int day) {
        this.filePath = filePath;
        this.emotion = emotion;
        this.favourite = favourite;
        this.month = month;
        this.day = day;
    }

    @PrimaryKey(autoGenerate =  true)
    private int key;
    private String filePath;
    private String emotion;
    private String favourite;
    private int month;
    private int day;

}
