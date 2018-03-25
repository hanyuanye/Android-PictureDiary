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
    private static final String TAG = "Tag";
    private boolean takePictureWhenScreenOn = true;
    private CameraManager mCameraManager;
    private int mImageCounter = 0;
    private  String mFilePath;
    private PowerManager mPowerManager;
    private BroadcastReceiver mReceiver;
    private FaceClientHelper mFaceClient;

    private Camera mCamera;

    private Handler handler;
    private int timer = 5000;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mPowerManager.isInteractive()) {
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
        mFaceClient = new FaceClientHelper(this);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.os.action.DEVICE_IDLE_MODE_CHANGED")){
                    if (mPowerManager.isInteractive() && takePictureWhenScreenOn) {
                        callStartCamera();
                        takePictureWhenScreenOn = false;
                    }
                }
            }
        };
        final IntentFilter ScreenIdleFilter = new IntentFilter();
        this.registerReceiver(mReceiver, ScreenIdleFilter);
        mFilePath = intent.getStringExtra("FilePath");
        startHandler();

        return START_STICKY;
    }

    private void callStartCamera() {
        mCamera = new Camera();
        mImageCounter++;
        mCamera.startCamera(mCameraManager, mFilePath + Integer.toString(mImageCounter) + ".jpeg");
        if (mImageCounter > 0) {
            mFaceClient.recognizeImage(mFilePath + Integer.toString(mImageCounter - 1) + ".jpeg");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mCamera.closeCamera();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
        Log.d(TAG, "Closing Session");
    }
}
