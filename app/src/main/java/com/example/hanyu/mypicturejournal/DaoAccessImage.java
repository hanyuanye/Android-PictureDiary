package com.example.hanyu.mypicturejournal;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Delete;

import java.util.List;

/**
 * Created by hanyu on 3/10/2018.
 */
@Dao
public interface DaoAccessImage {
    @Insert
    void insertOneImage(Image image);

    @Query("Select * FROM Image WHERE month =:queried_month AND day =:queried_day")
    List<Image> fetchImagesFromDate(int queried_month, int queried_day);

    @Query("Select * FROM Image WHERE emotion =:queried_emotion")
    List<Image> fetchImagesFromEmotion(String queried_emotion);

    @Query("Select * FROM Image WHERE favourite =:TRUE")
    List<Image> fetchFavouriteImages(String TRUE);

    @Update()
    void updateImage(Image image);

    @Delete()
    void deleteImage(Image image);
}
