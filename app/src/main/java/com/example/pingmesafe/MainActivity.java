package com.example.pingmesafe;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pingmesafe.FireBase.User_Data_Google_Model;
import com.example.pingmesafe.FireBase.User_Data_Model;
import com.google.android.gms.auth.api.Auth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.credentials.Credential;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pingmesafe.Activities.HomeScreen_Activity;
import com.example.pingmesafe.Activities.Register_Activity;
import com.example.pingmesafe.FireBase.Login;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private ScrollView scrollView;
    FirebaseAuth mAuth;
    double latitude,longitude;
    private EditText edt_pass_login;
    private EditText edt_mailid_login;
    private TextView txt_register, txt_invalid_cred;
    private AppCompatButton btn_login;
    LinearLayout progressBar_layout;
    AppCompatButton btSignIngoogle;
    GoogleSignInClient googleSignInClient;
    DatabaseReference UserdatabaseReference = FirebaseDatabase.getInstance().getReference("User Data Google");
    GoogleSignInAccount account;
    FirebaseAuth firebaseAuth;
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

        //FirebaseApp.initializeApp(this);
        latitude = 0;
        longitude=0;

        getCurrentLocation();

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
                        scrollView.requestChildFocus(edt_pass_login, edt_pass_login);
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

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MainActivity.this, HomeScreen_Activity.class);
                            intent.putExtra("signInMethod", "InApp");
                            startActivity(intent);
                            finish();
                            progressBar_layout.setVisibility(View.VISIBLE);
                        } else {
                            txt_invalid_cred.setVisibility(View.VISIBLE);
                        }
                    });
        });

        btSignIngoogle = findViewById(R.id.btn_google_signin);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("864715165254-9por07pfc1h2c2tnvd2pmnlmukcrt9e1.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);

        btSignIngoogle.setOnClickListener((View.OnClickListener) view -> {
            // Initialize sign in intent
            Intent intent = googleSignInClient.getSignInIntent();
            // Start activity for result
            startActivityForResult(intent, 100);
        });

        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            startActivity(new Intent(MainActivity.this, HomeScreen_Activity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful initialize string
                String s = "Google sign in successful";
                // Display Toast
                displayToast(s);
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        account = googleSignInAccount;
                        // When sign in account is not equal to null initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // Start a new thread to perform Firebase authentication
                                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if (task.isSuccessful()) {
                                            sendUserDatatoDB(account.getDisplayName().split(" ")[0], account.getDisplayName().split(" ")[1], account.getEmail(), latitude, longitude, account.getPhotoUrl());
                                            progressBar_layout.setVisibility(View.VISIBLE);
                                            Intent intent = new Intent(MainActivity.this, HomeScreen_Activity.class);
                                            intent.putExtra("signInMethod","Google");
                                            intent.putExtra("GoogleAccount",account);
                                            Toast.makeText(MainActivity.this, "intent sent", Toast.LENGTH_SHORT).show();
                                            startActivity(intent);
                                            displayToast("Firebase authentication successful");
                                        } else {
                                            displayToast("Authentication Failed :" + task.getException().getMessage());
                                        }
                                    }
                                });
                            }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendUserDatatoDB(String fname, String lname, String email, double latitude, double longitude, Uri selectedProfileImage) {
        UserdatabaseReference.child(Objects.requireNonNull(fname+" "+lname)).setValue(new User_Data_Google_Model(fname, lname, email, latitude, longitude, selectedProfileImage));
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    });
        }
    }
}

