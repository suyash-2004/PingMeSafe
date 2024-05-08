package com.example.pingmesafe.Activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pingemesafe.R;
import com.example.pingmesafe.Activities.HomeScreen_Activity;
import com.example.pingmesafe.Activities.Register_Activity;
import com.example.pingmesafe.FireBase.Login;
import com.example.pingmesafe.FireBase.UnSafe_Alert_Model;
import com.example.pingmesafe.FireBase.User_Data_Model;
import com.example.pingmesafe.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register_Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText  edt_pass_register,edt_fname_register, edt_lname_register, edt_email_register, edt_dob_register, edt_number_register;
    AppCompatButton btn_Register;
    String latitude = null, longitude = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private String alertID;
    LinearLayout progressBar_layout;


    CircleImageView profileImage,btn_edit_profile_pic;
    int selectedProfileImage;


    private final DatabaseReference UserdatabaseReference = FirebaseDatabase.getInstance().getReference("User Data");


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

        edt_fname_register = findViewById(R.id.edt_fname_register);
        edt_lname_register = findViewById(R.id.edt_lname_register);
        edt_dob_register = findViewById(R.id.edt_dob_register);
        edt_number_register = findViewById(R.id.edt_number);
        edt_email_register = findViewById(R.id.edt_email_register);
        edt_pass_register = findViewById(R.id.edt_pass_register);
        progressBar_layout = findViewById(R.id.layout_progressbar);
        ScrollView scrollView = findViewById(R.id.scrollview_register);
        progressBar_layout.setVisibility(View.GONE);


        btn_Register = findViewById(R.id.btnRegister);
        btn_edit_profile_pic = findViewById(R.id.btn_edit_profile_pic);
        profileImage = findViewById(R.id.profileImage);

        selectedProfileImage = R.drawable.defaultprofileimage;
        profileImage.setImageResource(selectedProfileImage);
        btn_edit_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog select_profile_image_dialog = new Dialog(Register_Activity.this);
                select_profile_image_dialog.setContentView(R.layout.select_profile_image_layout);

                RoundedImageView avatar1, avatar2, avatar3, avatar4, avatar5, avatar6, avatar7, avatar8, avatar9, avatar10;

                avatar1 = select_profile_image_dialog.findViewById(R.id.img_avatar1);
                avatar2 = select_profile_image_dialog.findViewById(R.id.img_avatar2);
                avatar3 = select_profile_image_dialog.findViewById(R.id.img_avatar3);
                avatar4 = select_profile_image_dialog.findViewById(R.id.img_avatar4);
                avatar5 = select_profile_image_dialog.findViewById(R.id.img_avatar5);
                avatar6 = select_profile_image_dialog.findViewById(R.id.img_avatar6);
                avatar7 = select_profile_image_dialog.findViewById(R.id.img_avatar7);
                avatar8 = select_profile_image_dialog.findViewById(R.id.img_avatar8);
                avatar9 = select_profile_image_dialog.findViewById(R.id.img_avatar9);
                avatar10 = select_profile_image_dialog.findViewById(R.id.img_avatar10);

                avatar1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setImageResource(R.drawable.avatar1);
                        select_profile_image_dialog.dismiss(); // Close the dialog
                    }
                });

                avatar2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setImageResource(R.drawable.avatar2);
                        select_profile_image_dialog.dismiss(); // Close the dialog
                    }
                });

                avatar3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setImageResource(R.drawable.avatar3);
                        select_profile_image_dialog.dismiss(); // Close the dialog
                    }
                });

                avatar4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setImageResource(R.drawable.avatar4);
                        select_profile_image_dialog.dismiss(); // Close the dialog
                    }
                });

                avatar5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setImageResource(R.drawable.avatar5);
                        select_profile_image_dialog.dismiss(); // Close the dialog
                    }
                });

                avatar6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setImageResource(R.drawable.avatar6);
                        select_profile_image_dialog.dismiss(); // Close the dialog
                    }
                });

                avatar7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setImageResource(R.drawable.avatar7);
                        select_profile_image_dialog.dismiss(); // Close the dialog
                    }
                });

                avatar8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setImageResource(R.drawable.avatar8);
                        select_profile_image_dialog.dismiss(); // Close the dialog
                    }
                });

                avatar9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setImageResource(R.drawable.avatar9);
                        select_profile_image_dialog.dismiss(); // Close the dialog
                    }
                });

                avatar10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setImageResource(R.drawable.avatar10);
                        select_profile_image_dialog.dismiss(); // Close the dialog
                    }
                });
                select_profile_image_dialog.show();


            }
        });


        edt_pass_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to the bottom of the scrollView
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.requestChildFocus(edt_pass_register, edt_pass_register);
                    }
                });
            }
        });

        edt_dob_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to the bottom of the scrollView
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.requestChildFocus(edt_dob_register, edt_dob_register);
                    }
                });
            }
        });

        edt_number_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to the bottom of the scrollView
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.requestChildFocus(edt_number_register, edt_number_register);
                    }
                });
            }
        });

        btn_Register.setOnClickListener(v -> {
            String fname, lname, email, password, dob, number;
            fname = String.valueOf(edt_fname_register.getText());
            lname = String.valueOf(edt_lname_register.getText());
            dob = String.valueOf(edt_dob_register.getText());
            number = String.valueOf(edt_number_register.getText());
            email = String.valueOf(edt_email_register.getText());
            password = String.valueOf(edt_pass_register.getText());

            if(TextUtils.isEmpty(fname)){
                Toast.makeText(Register_Activity.this, "First Name cannot be Empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(lname)){
                Toast.makeText(Register_Activity.this, "Last Name cannot be Empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(email)){
                Toast.makeText(Register_Activity.this, "Email cannot be Empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(password)){
                Toast.makeText(Register_Activity.this, "Password cannot be Empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(number)){
                Toast.makeText(Register_Activity.this, "Phone Number cannot be Empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FetchAlertID();
                                getCurrentLocation();
                                sendUserDatatoDB(fname, lname, email, dob, number, latitude, longitude, selectedProfileImage);
                                progressBar_layout.setVisibility(View.VISIBLE);
                                startActivity(new Intent(Register_Activity.this,MainActivity.class));
                            } else {
                                Toast.makeText(Register_Activity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    private void sendUserDatatoDB(String fname, String lname, String email, String dob, String number, String latitude, String longitude, int selectedProfileImage) {
        UserdatabaseReference.child(alertID).setValue(new User_Data_Model(fname, lname, email, dob, number, latitude, longitude, selectedProfileImage));
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        }
                    });
        }
    }

    private void FetchAlertID(){
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream fis = getApplicationContext().openFileInput("FireBaseAlretID.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        alertID = content.toString();
    }
}