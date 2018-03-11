package com.example.hanyu.mypicturejournal;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Bitmap;


import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.example.hanyu.mypicturejournal.StartActivity.FACE_API_ENDPOINT;
import static com.example.hanyu.mypicturejournal.StartActivity.FACE_KEY;


/**
 * Created by hanyu on 3/9/2018.
 */

public class EmotionClient {
    private static final int ANGER = 0;
    private static final int CONTEMPT = 1;
    private static final int DISGUST = 2;
    private static final int FEAR = 3;
    private static final int HAPPINESS = 4;
    private static final int NEUTRAL = 5;
    private static final int SADNESS = 6;
    private static final int SURPRISE = 7;

    private static String TAG = "tag";
    static String DBTAG = "dbtag";
    private static int imageCounter = 0;
    private Bitmap mBitmap;
    private String imagePath;
    private FaceServiceClient client;
    private ImageDatabase db;

    public EmotionClient(Context context) {
        if (client == null) {
            client = new FaceServiceRestClient(FACE_API_ENDPOINT, FACE_KEY);
        }
        db = Room.databaseBuilder(context, ImageDatabase.class, "image-db").build();
    }

    public void recognizeImage(String path) {
        mBitmap = BitmapFactory.decodeFile(path);
        imagePath = path;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mBitmap = RotateBitmap(mBitmap, 270);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            new detectAndFrame().execute(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class detectAndFrame extends AsyncTask<InputStream, String, Face[]> {
        public detectAndFrame() {
        }

        @Override
        protected Face[] doInBackground(InputStream... params) {
            try {
                imageCounter ++;
                publishProgress("Detecting");
                Face[]result = client.detect(
                        params[0],
                        true,
                        false,
                        new FaceServiceClient.FaceAttributeType[] {
                                FaceServiceClient.FaceAttributeType.Emotion
                        }
                );
                if (result.length == 0) {
                    publishProgress("Image" + Integer.toString(imageCounter) + " no face found");
                    File file = new File(imagePath);
                    file.delete();
                } else {
                    publishProgress("Image" + Integer.toString(imageCounter) + " faces found: " + result.length);
                }
                return result;
            } catch (IOException | ClientException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... update) {
            Log.d(TAG, update[0]);
        }

        @Override
        protected void onPostExecute(Face[] result) {
            for (Face face : result) {
                parseJson(face);
            }
        }
    }

    private static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void parseJson(Face face) {
        Emotion emotion = face.faceAttributes.emotion;
        double emotionArray[] =  {
            emotion.anger,
            emotion.contempt,
            emotion.disgust,
            emotion.fear,
            emotion.happiness,
            emotion.neutral,
            emotion.sadness,
            emotion.surprise
        };
        double max = 0;
        int emotionIndex = 0;
        for (int i = 0; i < emotionArray.length; i++) {
            if (emotionArray[i] > max) {
                max = emotionArray[i];
                emotionIndex = i;
            }
        }
        String emotionString;
        switch (emotionIndex) {
            case ANGER:
                emotionString = "Angry";
                break;
            case CONTEMPT:
                emotionString = "Contempt";
                break;
            case DISGUST:
                emotionString = "Disgust";
                break;
            case FEAR:
                emotionString = "Fear";
                break;
            case HAPPINESS:
                emotionString = "Happiness";
                break;
            case NEUTRAL:
                emotionString = "Neutral";
                break;
            case SADNESS:
                emotionString = "Sadness";
                break;
            case SURPRISE:
                emotionString = "Surprise";
                break;
            default:
                emotionString = "";
                break;
        }

        Image image = new Image(imagePath, emotionString, "false", 10, 10);
        new DatabaseAsync().execute(image);

        Log.d(TAG, Integer.toString(emotionIndex));
    }

    private class DatabaseAsync extends AsyncTask<Image, Void, Void> {
        @Override
        protected Void doInBackground(Image... image) {
            Log.d(DBTAG, image[0].getFilePath() + "  " + image[0].getEmotion() + "  " + image[0].getFavourite());
            db.daoAccessImage().insertOneImage(image[0]);
            return null;
        }
    }
}
