package com.example.hanyu.mypicturejournal;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.Arrays;


public class DisplayImagesActivity extends AppCompatActivity {
    private static final String TAG = "counter";
    ListView listView;
    private static ImageListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);
        String query = getIntent().getStringExtra("List");
        String[] parameters = new String[3];
        parameters[1] = query;
        listView = (ListView) findViewById(R.id.list);
        adapter = new ImageListAdapter(this);
        listView.setAdapter(adapter);
        if (isEmotion(query)) {
            parameters[0] = "Emotion";
        } else if (isFavourite(query)) {
            parameters[0] = "Favourite";
        } else {
            parameters[0] = "Date";
            int counter = 0;
            while (parameters[1].charAt(counter) != ' ') {
                Log.d(TAG, Integer.toString(counter));
                counter ++;
            }
            parameters[2] = parameters[1].substring(counter+1);
            parameters[1] = parameters[1].substring(0, counter);
            Log.d(TAG, parameters[1]);
            Log.d(TAG, parameters[2]);
        }
        new DatabaseAsync().execute(parameters);
    }


    private class DatabaseAsync extends AsyncTask<String, Void, ArrayList<Image>> {
        @Override
        protected ArrayList<Image> doInBackground(String... params) {
            ArrayList<Image> imagesToDisplay = new ArrayList<>();
            if (params[0] == "Emotion") {
                imagesToDisplay = (ArrayList) ImageDatabase.getInstance(DisplayImagesActivity.this).daoAccessImage().fetchImagesFromEmotion(params[1]);
            } else if (params[0] == "Date") {
                imagesToDisplay = (ArrayList) ImageDatabase.getInstance(DisplayImagesActivity.this).daoAccessImage().fetchImagesFromDate(params[1], Integer.valueOf(params[2]));
            } else if (params[0] == "Favourite") {
                imagesToDisplay = (ArrayList) ImageDatabase.getInstance(DisplayImagesActivity.this).daoAccessImage().fetchImagesFromFavourites(params[1]);
            }
            if (imagesToDisplay.size() == 0) {
                return null;
            }
            return imagesToDisplay;
        }

        @Override
        protected void onPostExecute(ArrayList<Image> imagesToDisplay) {
            if (imagesToDisplay != null) {
                Log.d(TAG, Integer.toString(imagesToDisplay.size()));
                adapter.instantiate(imagesToDisplay);
            }
        }
    }


    private boolean isEmotion (String query) {
        String[] emotions = {
                "Angry",
                "Contempt",
                "Disgust",
                "Happiness",
                "Neutral",
                "Sadness",
                "Surprise"
        };
        if (Arrays.asList(emotions).contains(query)) {
            return true;
        }
        return false;
    }

    private boolean isFavourite(String query) {
        if (query == "Favourite") {
            return true;
        }
        return false;
    }
}
