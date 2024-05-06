package com.example.pingmesafe.Activities;

import static android.content.ContentValues.TAG;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pingemesafe.R;
import com.example.pingmesafe.Activities.HomeScreen_Activity;
import com.example.pingmesafe.Activities.Register_Activity;
import com.example.pingmesafe.FireBase.Login;
import com.example.pingmesafe.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register_Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText edt_mail_register , edt_pass_register;
    AppCompatButton btn_Register;

    @Override
    public void onStart() {
        super.onStart();
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        edt_mail_register = findViewById(R.id.edt_email_register);
        edt_pass_register = findViewById(R.id.edt_pass_register);
        btn_Register = findViewById(R.id.btnRegister);

        btn_Register.setOnClickListener(v -> {
            String email, password;
            email = String.valueOf(edt_mail_register.getText());
            password = String.valueOf(edt_pass_register.getText());

            if(TextUtils.isEmpty(email)){
                Toast.makeText(Register_Activity.this, "Enter Email",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(password)){
                Toast.makeText(Register_Activity.this, "Enter Password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register_Activity.this, "Registered",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register_Activity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}