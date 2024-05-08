package com.example.pingmesafe;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pingemesafe.R;
import com.example.pingmesafe.Activities.HomeScreen_Activity;
import com.example.pingmesafe.Activities.Register_Activity;
import com.example.pingmesafe.FireBase.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ScrollView scrollView;
    FirebaseAuth mAuth;
    private EditText edt_pass_login;
    private EditText edt_mailid_login;
    private TextView txt_register, txt_invalid_cred;
    private AppCompatButton btn_login;
    LinearLayout progressBar_layout;

    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), HomeScreen_Activity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        scrollView = findViewById(R.id.scrollView);
        edt_pass_login = findViewById(R.id.edt_pass_login);
        edt_mailid_login = findViewById(R.id.edt_mailid_login);
        txt_register = findViewById(R.id.txt_register);
        txt_invalid_cred = findViewById(R.id.txt_invalid_cred);
        btn_login = findViewById(R.id.btnLogin);
        progressBar_layout = findViewById(R.id.layout_progressbar);
        progressBar_layout.setVisibility(View.GONE);

        edt_pass_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to the bottom of the scrollView
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.requestChildFocus(edt_pass_login, edt_pass_login);                    }
                });
            }
        });

        txt_register.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Register_Activity.class)));

        btn_login.setOnClickListener(v -> {
            String email, password;
            email = String.valueOf(edt_mailid_login.getText());
            password = String.valueOf(edt_pass_login.getText());

            if(TextUtils.isEmpty(email)){
                txt_invalid_cred.setVisibility(View.VISIBLE);
                return;
            }

            if(TextUtils.isEmpty(password)){
                txt_invalid_cred.setVisibility(View.VISIBLE);
                return;
            }

            txt_invalid_cred.setVisibility(View.GONE);
            progressBar_layout.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            txt_invalid_cred.setVisibility(View.VISIBLE);
                        }
                    });
        });
    }
}