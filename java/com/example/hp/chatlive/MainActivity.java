package com.example.hp.chatlive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.onesignal.OneSignal;
import com.sinch.android.rtc.SinchError;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity implements SinchService.StartFailedListener{
    SharedPreferences pref;
    private static int SPLASH_TIME_OUT = 3000;
    Firebase frostref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        Fabric.with(this, new Crashlytics());
        Firebase.setAndroidContext(this);
        OneSignal.startInit(this).init();
        final String pref_user = pref.getString("Username","");
        final String pref_pass = pref.getString("Password","");
        final String pref_phone = pref.getString("Phone_no","");
        final String pref_uid = pref.getString("UID_no","");
        Fabric.with(this, new Crashlytics());
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.splasss);
        if(pref_user.equals(""))
        {
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(MainActivity.this, welcome.class);
                    startActivity(i);

                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
        else
        {
            UserDetails.username = pref_user;
            UserDetails.password = pref_pass;
            UserDetails.phone = pref_phone;
            UserDetails.idd = pref_uid;
            new Handler().postDelayed(new Runnable() {
             @Override
                public void run() {
                    loginClicked(UserDetails.username);
                }
            }, 4000);
        }

    }
    ////////////////////////////
    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    private void loginClicked(String aa) {
        String userName = aa;
        OneSignal.sendTag("User_id",userName);
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }
        if (!getSinchServiceInterface().isStarted() || getSinchServiceInterface()==null) {
            getSinchServiceInterface().startClient(userName);
        } else {
            openPlaceCallActivity();
        }
    }

    private void openPlaceCallActivity() {
        FrostStatus();

        //Toast.makeText(this,"Sinchservice Started",Toast.LENGTH_LONG).show();
        Intent newIntent = new Intent(this, ResideV.class);
        //Intent newIntent2 = new Intent(this, ConnectionService.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startService(newIntent2);
        startActivity(newIntent);
        finish();
    }
    public void FrostStatus()
    {
        frostref= new Firebase("https://fir-g-a2c81.firebaseio.com/FrostVoice");
        frostref.child("State").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue().toString().equals("On"))
                {
                    UserDetails.frostvoice_flag=1;
                }
                else
                {
                    UserDetails.frostvoice_flag=0;
                }
            }
            @Override
            public void onCancelled(FirebaseError databaseError) {
            }
        });
    }


}
