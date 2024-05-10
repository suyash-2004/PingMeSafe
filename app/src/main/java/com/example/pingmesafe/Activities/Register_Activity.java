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
    import android.content.ContentResolver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.telephony.PhoneNumberUtils;
    import android.text.TextUtils;
    import android.util.Log;
    import android.view.View;
    import android.view.WindowManager;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.RelativeLayout;
    import android.widget.ScrollView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.example.pingmesafe.FireBase.User_Data_Google_Model;
    import com.example.pingmesafe.R;
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
    import com.google.firebase.FirebaseException;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.auth.PhoneAuthCredential;
    import com.google.firebase.auth.PhoneAuthProvider;
    import com.google.firebase.auth.UserProfileChangeRequest;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.makeramen.roundedimageview.RoundedImageView;

    import java.io.BufferedReader;
    import java.io.File;
    import java.io.FileInputStream;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.util.concurrent.TimeUnit;

    import de.hdodenhof.circleimageview.CircleImageView;

    public class Register_Activity extends AppCompatActivity {
        private FirebaseAuth mAuth;
        private RelativeLayout backButton;
        private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, dobEditText, numberEditText;
        private AppCompatButton registerButton;
        private CircleImageView profileImage, editProfilePicButton;
        private int selectedProfileImage;
        private double latitude, longitude;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
        private String alertId;
        LinearLayout progressBar_layout;
        private final DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("User Data");


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
            setContentView(R.layout.activity_register);

            mAuth = FirebaseAuth.getInstance();

            backButton = findViewById(R.id.layout_btn_back);
            firstNameEditText = findViewById(R.id.edt_fname_register);
            lastNameEditText = findViewById(R.id.edt_lname_register);
            emailEditText = findViewById(R.id.edt_email_register);
            passwordEditText = findViewById(R.id.edt_pass_register);
            dobEditText = findViewById(R.id.edt_dob_register);
            numberEditText = findViewById(R.id.edt_number);
            registerButton = findViewById(R.id.btnRegister);
            profileImage = findViewById(R.id.profileImage);
            editProfilePicButton = findViewById(R.id.profile_edt_register);
            progressBar_layout = findViewById(R.id.layout_progressbarRegister);



            backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

            registerButton = findViewById(R.id.btnRegister);
            profileImage = findViewById(R.id.profileImage);

            selectedProfileImage = R.drawable.defaultprofileimage;
            profileImage.setImageResource(selectedProfileImage);

            editProfilePicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog select_profile_image_dialog = new Dialog(Register_Activity.this);
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
                                profileImage.setImageResource(selectedProfileImage);
                                select_profile_image_dialog.dismiss(); // Close the dialog
                            }
                        });
                    }

                    select_profile_image_dialog.show();
                }
            });


            registerButton.setOnClickListener(v -> {
                String firstName, lastName, email, password, dob, number;
                firstName = firstNameEditText.getText().toString();
                lastName = lastNameEditText.getText().toString();
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                dob = dobEditText.getText().toString();
                number = numberEditText.getText().toString();

                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(this, "First Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(this, "Last Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(this, "Phone Number cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar_layout.setVisibility(View.VISIBLE);
                                    setUserDetails(firstName, lastName, selectedProfileImage, email, number);
                                    sendUserDataToDB(firstName, lastName, email, dob, number, latitude, longitude, selectedProfileImage);
                                    getOnBackPressedDispatcher().onBackPressed();
                                } else {
                                    Toast.makeText(Register_Activity.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });;
            });
        }

        private void setUserDetails(String firstName, String lastName, int profileImage, String email, String phoneNumber) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(firstName + " " + lastName)
                    .setPhotoUri(getImageUri(profileImage))
                    .build();

            FirebaseUser user = mAuth.getCurrentUser();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            } else {
                                Log.w(TAG, "User profile update failed.", task.getException());
                            }
                        }
                    });

            user.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email updated.");
                            } else {
                                Log.w(TAG, "User email update failed.", task.getException());
                            }
                        }
                    });

            updateUserPhoneNumber(phoneNumber);
        }

        private void updateUserPhoneNumber(String phoneNumber) {
            PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly verified without needing to send a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically detect the incoming verification SMS and perform verification without user action.

                    // Update the user's phone number with the PhoneAuthCredential object
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePhoneNumber(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User phone number updated.");
                                    } else {
                                        Log.w(TAG, "User phone number update failed.", task.getException());
                                    }
                                }
                            });
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    // Handle verification failed error
                    Log.w(TAG, "Phone number verification failed.", e);
                }

                @Override
                public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                    // Save the verification ID
                    String mVerificationId = verificationId;

                    // ...
                }

                // ...
            };

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    0,                 // Timeout duration (0 means no timeout)
                    TimeUnit.MILLISECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);
        }

        private void sendUserDataToDB(String firstName, String lastName, String email, String dob, String number, double latitude, double longitude, int selectedProfileImage) {
            String userId = getUserId();
            userDatabaseReference.child(userId).setValue(new User_Data_Model(firstName, lastName, email, dob, number, latitude, longitude, selectedProfileImage));
        }
        private Uri getImageUri(int imageResourceId) {
            return Uri.parse("android.resource://" + getPackageName() + "/" + imageResourceId);
        }

        public String getUserId(){
            String userId;
            if (!fileExists("UserID.txt")) {
                userId = userDatabaseReference.push().getKey();
                createTextFile(Register_Activity.this, "UserID.txt",userId);
            } else {
                StringBuilder content = new StringBuilder();
                try {
                    FileInputStream fis = getApplicationContext().openFileInput("UserID.txt");
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
                userId = content.toString();
            }
            return userId;
        }

        private void createTextFile(Register_Activity context, String fileName, String alertID) {
            try {
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.write(alertID.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean fileExists(String fileName) {
            File file = new File(getApplicationContext().getFilesDir(), fileName);
            return file.exists();
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

        private void fetchAlertID() {
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
            alertId = content.toString();
        }
    }