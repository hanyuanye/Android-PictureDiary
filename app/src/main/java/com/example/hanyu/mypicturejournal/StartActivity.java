package com.example.hanyu.mypicturejournal;

import android.Manifest;
import android.app.ActivityManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.hanyu.mypicturejournal.EmotionClient.DBTAG;


public class StartActivity extends AppCompatActivity {
    private static ImageView imageView;
    private ImageDatabase db;
    private static final int REQUEST_CAMERA = 1;
    String TAG = "activityTAG";
    Intent takePhotoIntent;
    String filePath;
    public static String EMOTION_KEY;
    public static String FACE_KEY = "(Insert API Key)"; //Insert Personal API key here
    public static String FACE_API_ENDPOINT = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0";
    public static final String FileName = "MyPictureJournal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

        FACE_KEY = this.getString(R.string.face_subscription_key);
        EMOTION_KEY = this.getString(R.string.emotion_subscription_key);
        FACE_API_ENDPOINT = this.getString(R.string.face_api_endpoint);
        File MyPictureJournalDir = new File(Environment.getExternalStorageDirectory(), FileName);
        if (!MyPictureJournalDir.exists()) {
            MyPictureJournalDir.mkdir();
        }
        filePath = Environment.getExternalStorageDirectory()  + "/MyPictureJournal" + "/image";
        takePhotoIntent = new Intent(StartActivity.this, Camera2Service.class);
        takePhotoIntent.putExtra("FilePath", filePath);
        if (!isServiceRunning(takePhotoIntent.getClass())) {
            Log.d(TAG, "starting service");
            StartActivity.this.startService(takePhotoIntent);
        }

        db  = Room.databaseBuilder(this, ImageDatabase.class, "image-db").build();
        imageView = (ImageView) findViewById(R.id.imageView);
        Button getStuff = (Button) findViewById(R.id.button);
        getStuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] parameters = {"Emotion", "Neutral"};
                new DatabaseAsync().execute(parameters);
            }
        });
    }

    private class DatabaseAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            ArrayList<Image> imageToDisplay = (ArrayList)db.daoAccessImage().fetchImagesFromEmotion(params[1]);
            if (imageToDisplay.size() == 0) {
                return null;
            }
            return imageToDisplay.get(0).getFilePath();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.d(DBTAG, "null");
                return;
            }
            Log.d(TAG, result);
            File image = new File(result);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.outHeight = imageView.getHeight();
            bmOptions.outWidth = Math.round(imageView.getHeight() * 4 / 3);
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            imageView.setImageBitmap(bitmap);
            imageView = RotateImageView(imageView, 270);
        }
    }

    private ImageView RotateImageView(ImageView source, float angle) {
        Matrix matrix = new Matrix();
        source.setScaleType(ImageView.ScaleType.MATRIX);   //required
        matrix.postRotate(angle, source.getPivotX(), source.getPivotY());
        source.setImageMatrix(matrix);
        return source;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CAMERA: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
            }
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(takePhotoIntent);
        super.onDestroy();
    }
}
