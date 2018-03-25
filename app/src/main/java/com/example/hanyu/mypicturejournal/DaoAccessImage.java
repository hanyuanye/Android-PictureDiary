package com.example.hanyu.mypicturejournal;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by hanyu on 3/10/2018.
 */
@Dao
public interface DaoAccessImage {
    @Insert
    void insertOneImage(Image image);

    @Query("Select * FROM Image WHERE mMonth =:queried_month AND mDay =:queried_day")
    List<Image> fetchImagesFromDate(String queried_month, int queried_day);

    @Query("Select * FROM Image WHERE mEmotion =:queried_emotion")
    List<Image> fetchImagesFromEmotion(String queried_emotion);

    @Query("Select * FROM Image WHERE mFavourite =:TRUE")
    List<Image> fetchImagesFromFavourites(String TRUE);

    @Update()
    void updateImage(Image image);

    @Delete()
    void deleteImage(Image image);

    @Query("DELETE FROM Image")
    void deleteAll();
}
