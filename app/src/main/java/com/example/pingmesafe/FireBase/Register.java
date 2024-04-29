package com.example.pingmesafe.FireBase;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pingemesafe.R;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail , editTextPassword;
    Button buttonReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnLogin), (v, insets) -> {
        //    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //    return insets;
        //});

        //editTextEmail = findViewById(R.id.edtEmail);
        //editTextPassword = findViewById((R.id.edtPassword);

        //buttonReg = findViewById(R.id.btn)
    }
}