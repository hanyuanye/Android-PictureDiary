package com.example.hanyu.mypicturejournal;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by hanyu on 3/9/2018.
 */

class Camera{
    private static final String TAG = "Tag";
    private static final int CAMERA_LENS_CHOICE = CameraCharacteristics.LENS_FACING_BACK;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;
    private ImageReader mImageReader;
    private String mFilePath;

    void startCamera(CameraManager cameraManager, String filePath) {
        this.mFilePath = filePath;
        try {
            String pickedCamera = getCamera(cameraManager);
            cameraManager.openCamera(pickedCamera, cameraStateCallBack, null); //This would be checked in the initial activity which starts the service.
            mImageReader = ImageReader.newInstance(1920, 1088, ImageFormat.JPEG, 2);
            mImageReader.setOnImageAvailableListener(onImageAvailableListener, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private String getCamera(CameraManager manager) {
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) != CAMERA_LENS_CHOICE) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    void closeCamera() {
        try {
            if (mCameraCaptureSession != null) {
                mCameraCaptureSession.abortCaptures();
                mCameraCaptureSession.close();
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, "closed camera");
        mCameraDevice.close();
    }

    private void processImage(Image image) {
        Log.d(TAG, "processing image");
        ByteBuffer buffer;
        byte[] bytes;
        File file = new File(mFilePath);
        FileOutputStream output = null;

        if(image.getFormat() == ImageFormat.JPEG) {
            buffer = image.getPlanes()[0].getBuffer();
            bytes = new byte[buffer.remaining()]; // makes byte array large enough to hold image
            buffer.get(bytes); // copies image from buffer to byte array
            try {
                output = new FileOutputStream(file);
                output.write(bytes);    // write the byte array to file
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            } finally {
                image.close(); // close this to free up buffer for other images
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
        }
    }

    private CameraDevice.StateCallback cameraStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "Camera Opened");
            mCameraDevice = camera;
            startCaptureSession();
        }
        @Override
        public void onClosed(@NonNull CameraDevice camera) {
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            closeCamera();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            Log.d(TAG, Integer.toString(i) + "  Error");
            closeCamera();
        }
    };

    private CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onReady(@NonNull CameraCaptureSession session) {
        }

        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            if (mCameraDevice == null) {
                return;
            }
            mCameraCaptureSession = session;
            try {
                mCameraCaptureSession.setRepeatingRequest(createCaptureRequest(), null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                mCameraCaptureSession.capture(createCaptureRequest(), captureCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
        }
    };

    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            Log.d(TAG, "Capture Completed");
            closeCamera();
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            Log.d(TAG, "Capture Failed");
            closeCamera();
        }

        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            Log.d(TAG, "Capture Starting");
        }
    };

    private ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Image image = imageReader.acquireLatestImage();
            if (image != null) {
                processImage(image);
                image.close();
            }
        }
    };

    private void startCaptureSession() {
        try {
            mCameraDevice.createCaptureSession(Arrays.asList(mImageReader.getSurface()), sessionStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CaptureRequest createCaptureRequest() {
        try {
            CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            builder.addTarget(mImageReader.getSurface());
            return builder.build();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
