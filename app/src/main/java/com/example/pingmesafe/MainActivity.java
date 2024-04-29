package com.example.pingmesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;

import com.example.pingemesafe.R;
import com.example.pingmesafe.Activities.HomeScreen_Activity;

public class MainActivity extends AppCompatActivity {
    private ScrollView scrollView;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.scrollView);
        editText = findViewById(R.id.edt_pass);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to the EditText when clicked
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, editText.getTop());
                    }
                });
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