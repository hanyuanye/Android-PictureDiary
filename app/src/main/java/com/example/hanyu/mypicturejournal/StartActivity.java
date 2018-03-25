package com.example.hanyu.mypicturejournal;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;


public class StartActivity extends AppCompatActivity {
    public static final String[] MONTHS_ARRAY = {
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };
    public static final String[] EMOTION_ARRAY = {
            "Angry",
            "Contempt",
            "Disgust",
            "Happiness",
            "Neutral",
            "Sadness",
            "Surprise"
    };
    private static final String[] SPINNER_ARRAY = {
            "Date",
            "Emotion"
    };
    public static final String FACE_KEY = "7ff4f1db4d334ca6bc75fbaacae7a82f";
    public static final String FACE_API_ENDPOINT = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0";
    public static final String FILE_NAME = "MyPictureJournal";
    private static final int REQUEST_CAMERA = 1;
    private static final String TAG = "activityTAG";
    private static final int NUMBER_OF_DAYS_LISTED = 7;
    private ListView listView;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private String[] mDateListItems;
    private Intent mTakePhotoIntent;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
        File MyPictureJournalDir = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
        if (!MyPictureJournalDir.exists()) {
            MyPictureJournalDir.mkdir();
        }
        initiateDateList();
        mFilePath = Environment.getExternalStorageDirectory()  + "/" + FILE_NAME + "/image";
        mTakePhotoIntent = new Intent(StartActivity.this, Camera2Service.class);
        mTakePhotoIntent.putExtra("FilePath", mFilePath);
        if (!isServiceRunning(mTakePhotoIntent.getClass())) {
            Log.d(TAG, "starting service");
            StartActivity.this.startService(mTakePhotoIntent);
        }

        new DatabaseClear().execute();
        initiateViews();
    }

    private void initiateViews() {
        initiateDateList();
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter(StartActivity.this, android.R.layout.simple_list_item_1, mDateListItems);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        adapter = new ArrayAdapter(StartActivity.this, android.R.layout.simple_list_item_1, mDateListItems);
                        listView.setAdapter(adapter);
                        break;
                    case 1:
                        adapter = new ArrayAdapter(StartActivity.this, android.R.layout.simple_list_item_1, EMOTION_ARRAY);
                        listView.setAdapter(adapter);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent showImages = new Intent(StartActivity.this, DisplayImagesActivity.class);
                showImages.putExtra("List", adapter.getItem(i));
                startActivity(showImages);
            }
        });
    }
    //initiates the Date List to include all days in the past NUMBER_OF_DAYS_LISTED days.
    private void initiateDateList() {
        ArrayList<String> tempList = new ArrayList<>();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(calendar.DATE, 1 - NUMBER_OF_DAYS_LISTED);
        for (int i = 0; i < NUMBER_OF_DAYS_LISTED; ++i) {
            String month = MONTHS_ARRAY[calendar.get(calendar.MONTH)];
            String day = Integer.toString(calendar.get(calendar.DATE));
            tempList.add(month + " " + day);
            calendar.add(calendar.DATE,1);
        }
        mDateListItems = tempList.toArray(new String[tempList.size()]);
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
        stopService(mTakePhotoIntent);
        super.onDestroy();
    }

    private class DatabaseClear extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            ImageDatabase.getInstance(StartActivity.this).daoAccessImage().deleteAll();
            return null;
        }
    }
}