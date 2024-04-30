package com.example.pingmesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pingemesafe.R;
import com.example.pingmesafe.Activities.HomeScreen_Activity;
import com.example.pingmesafe.Activities.Register_Activity;

public class MainActivity extends AppCompatActivity {
    private ScrollView scrollView;
    private EditText edt_pass_login;
    private EditText edt_mailid_login;
    private TextView txt_register;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.scrollView);
        edt_pass_login = findViewById(R.id.edt_pass_login);
        edt_mailid_login = findViewById(R.id.edt_mailid_login);
        txt_register = findViewById(R.id.txt_register);
        edt_pass_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to the EditText when clicked
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, edt_pass_login.getTop());
                    }
                });
            }
        });



        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register_Activity.class));
            }
        });
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HomeScreen_Activity.class));
            }
        });


    }
}