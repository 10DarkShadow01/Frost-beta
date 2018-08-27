package com.example.hp.chatlive;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class welcome extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    Button a,b;
    Context aa=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.front);
        b=(Button)findViewById(R.id.button2);
        a=(Button)findViewById(R.id.button1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iny= new Intent(aa,Login.class);
                startActivity(iny);
            }
        });
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iny= new Intent(aa,Register.class);
                startActivity(iny);
            }
        });
    }
}