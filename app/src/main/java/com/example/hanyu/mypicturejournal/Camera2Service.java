package com.example.hanyu.mypicturejournal;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.hardware.camera2.CameraManager;

import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;

import android.util.Log;


public class Camera2Service extends Service {
    protected static final String TAG = "Tag";
    protected boolean takePictureWhenScreenOn = true;
    protected CameraManager manager;
    protected int imageCounter = 0;
    protected String filePath;
    protected PowerManager pm;
    private BroadcastReceiver receiver;
    protected EmotionClient client;

    protected Camera camera;

    private Handler handler;
    private int timer = 5000;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (pm.isInteractive()) {
                callStartCamera();
            } else {
                takePictureWhenScreenOn = true;
            }
            handler.postDelayed(this, timer);
        }
    };

    private void startHandler() {
        handler = new Handler();
        handler.postDelayed(runnable, timer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        client = new EmotionClient(this);

        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.os.action.DEVICE_IDLE_MODE_CHANGED")){
                    if (pm.isInteractive() && takePictureWhenScreenOn) {
                        callStartCamera();
                        takePictureWhenScreenOn = false;
                    }
                }
            }
        };
        final IntentFilter ScreenIdleFilter = new IntentFilter();
        this.registerReceiver(receiver, ScreenIdleFilter);
        filePath = intent.getStringExtra("FilePath");
        startHandler();

        return START_STICKY;
    }

    private void callStartCamera() {
        camera = new Camera();
        imageCounter++;
        camera.startCamera(manager, filePath + Integer.toString(imageCounter) + ".jpeg");
        if (imageCounter > 0) {
            client.recognizeImage(filePath + Integer.toString(imageCounter - 1) + ".jpeg");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        camera.closeCamera();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
        Log.d(TAG, "Closing Session");
    }
}
