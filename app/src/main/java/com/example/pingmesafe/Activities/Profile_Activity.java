package com.example.pingmesafe.Activities;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.pingmesafe.MainActivity;
import com.example.pingmesafe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.makeramen.roundedimageview.RoundedImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity {
    TextView fName, lName, email, password, phoneNumber, dob;
    RelativeLayout layout_back;
    CircleImageView profile_pic, editProfilePicButton;
    private int selectedProfileImage;
    LinearLayout progressBar_layout;
    FirebaseAuth mAuth;
    FirebaseUser user;

    FirebaseUser account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        layout_back = findViewById(R.id.layout_btn_back);
        fName = findViewById(R.id.txt_fname_profile);
        lName = findViewById(R.id.txt_lname_profile);
        email = findViewById(R.id.txt_email_profile);
        password = findViewById(R.id.txt_pass_profile);
        phoneNumber = findViewById(R.id.txt_number);
        dob = findViewById(R.id.txt_dob_profile);
        profile_pic = findViewById(R.id.profileImage_profile);
        editProfilePicButton = findViewById(R.id.btn_edit_profile_pic);
        account = FirebaseAuth.getInstance().getCurrentUser();
        progressBar_layout = findViewById(R.id.layout_progressbarProfile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        editProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog select_profile_image_dialog = new Dialog(Profile_Activity.this);
                select_profile_image_dialog.setContentView(R.layout.select_profile_image_layout);

                RoundedImageView[] avatars = new RoundedImageView[10];
                avatars[0] = select_profile_image_dialog.findViewById(R.id.img_avatar1);
                avatars[1] = select_profile_image_dialog.findViewById(R.id.img_avatar2);
                avatars[2] = select_profile_image_dialog.findViewById(R.id.img_avatar3);
                avatars[3] = select_profile_image_dialog.findViewById(R.id.img_avatar4);
                avatars[4] = select_profile_image_dialog.findViewById(R.id.img_avatar5);
                avatars[5] = select_profile_image_dialog.findViewById(R.id.img_avatar6);
                avatars[6] = select_profile_image_dialog.findViewById(R.id.img_avatar7);
                avatars[7] = select_profile_image_dialog.findViewById(R.id.img_avatar8);
                avatars[8] = select_profile_image_dialog.findViewById(R.id.img_avatar9);
                avatars[9] = select_profile_image_dialog.findViewById(R.id.img_avatar10);

                for (int i = 0; i < avatars.length; i++) {
                    final int index = i;
                    avatars[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedProfileImage = getResources().getIdentifier("avatar" + (index + 1), "drawable", getPackageName());
                            profile_pic.setImageResource(selectedProfileImage);
                            updateProfilePicture(selectedProfileImage);
                            select_profile_image_dialog.dismiss(); // Close the dialog
                        }
                    });
                }

                select_profile_image_dialog.show();
            }
        });


        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile_Activity.super.getOnBackPressedDispatcher().onBackPressed();
            }
        });
        if(account!=null){
            fName.setText(account.getDisplayName().split(" ")[0]);
            lName.setText(account.getDisplayName().split(" ")[1]);
            email.setText(account.getEmail());
            password.setText("**********");
            phoneNumber.setText(account.getPhoneNumber());
            Glide.with(this).load(account.getPhotoUrl()).into(profile_pic);
        }

        AppCompatButton logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout(v);
            }
        });
    }

    public void updateProfilePicture(int profileImage) {
        progressBar_layout.setVisibility(View.VISIBLE);

        Uri imageUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + profileImage);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUri)
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar_layout.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        } else {
                            Log.w(TAG, "User profile update failed.", task.getException());
                        }
                    }
                });

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Profile_Activity.this, MainActivity.class));
        finish();
    }
}