package com.application.anywherenamaaz;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.application.anywherenamaaz.util.UserUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private String SERVER_URL = "https://meet.jit.si";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFCMToken();
        //Initializing HTTP request connections
        AndroidNetworking.initialize(getApplicationContext());
        //Loading the layout
        loadLayout();
        // Initialize default options for Jitsi Meet conferences.
        initializeMeeting();
    }

    private void initializeMeeting() {
        URL serverURL;
        try {
            serverURL = new URL(SERVER_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setWelcomePageEnabled(false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);
    }

    private void loadLayout() {
        UserUtil userUtil = new UserUtil();
        SQLiteDatabase myDb = openOrCreateDatabase("VoiceCalling",MODE_PRIVATE,null);
        myDb.execSQL("CREATE TABLE IF NOT EXISTS User(Name VARCHAR, Token VARCHAR);");
        if(!userUtil.getNameFromDb(myDb).isEmpty()) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_name);
        }
    }

    public void onButtonClick(View v) {
        //EditText editText = findViewById(R.id.conferenceName);
        //String text = editText.getText().toString();
       // if (text.length() > 0) {
            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom("crazy-developer")
                    .setAudioOnly(true)
                    .setAudioMuted(true)
                    .setVideoMuted(true)
                    .build();
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(this, options);
        //}
    }

    public void nameSubmit(View v){
        EditText editText = findViewById(R.id.userName);
        String name = editText.getText().toString();
        UserUtil userUtil = new UserUtil();
        if(!name.isEmpty()) {
            if(isNetworkAvailable()) {
                saveNameToDb(name);
                userUtil.sendNameAndToken(name, FirebaseInstanceId.getInstance().getToken());
                // TODO: 01/05/20 Should be using INTENTS
                setContentView(R.layout.activity_main);
            } else {
                Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            }
        } else {
            editText.setHint("Please enter your name");
            editText.setError( "Name is required!" );
        }
    }

    public void saveNameToDb(String name) {
        SQLiteDatabase myDb = openOrCreateDatabase("VoiceCalling",MODE_PRIVATE,null);
        myDb.execSQL("CREATE TABLE IF NOT EXISTS User(Name VARCHAR);");
        ContentValues cv = new ContentValues();
        cv.put("Name", name);
        myDb.insertWithOnConflict("User",null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        myDb.close();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    private static final String TAG = "";
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("The token is ", token);
                    }
                });
    }


}