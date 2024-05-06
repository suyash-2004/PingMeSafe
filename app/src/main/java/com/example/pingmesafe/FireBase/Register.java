package com.example.pingmesafe.FireBase;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pingemesafe.R;
import com.example.pingmesafe.Activities.HomeScreen_Activity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText edt_mail_register , edt_pass_register;
    AppCompatButton btn_Register;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeScreen_Activity.class);
            startActivity(intent);
            finish();
        }
    }
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


        mAuth = FirebaseAuth.getInstance();
        edt_mail_register = findViewById(R.id.edt_email_register);
        edt_pass_register = findViewById(R.id.edt_pass_register);
        btn_Register = findViewById(R.id.btnRegister);


    }
}