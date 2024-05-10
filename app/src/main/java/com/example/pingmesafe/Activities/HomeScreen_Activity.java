package com.example.pingmesafe.Activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.pingmesafe.FireBase.User_Data_Model;
import com.example.pingmesafe.R;
import com.example.pingmesafe.Adapters.MyAdapter;
import com.example.pingmesafe.Fragments.fragment_emergency;
import com.example.pingmesafe.Fragments.fragment_home;
import com.example.pingmesafe.Fragments.fragment_prepare;
import com.example.pingmesafe.Fragments.fragment_recover;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HomeScreen_Activity extends AppCompatActivity {
    private FloatingActionButton sosFab;
    private BottomAppBar appBar;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference userDatabaseReference_Google;

    private BottomNavigationView navView;
    private RecyclerView recyclerView;
    int profileimage;
    private MyAdapter adapter;
    String userId;
    private List<List<String>> dataList = new ArrayList<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_home_screen);

        mAuth = FirebaseAuth.getInstance();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("User Data");
        userDatabaseReference_Google = FirebaseDatabase.getInstance().getReference("User Data Google");

        setupUI();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = getUserId();
            getUserDataFromDatabase(userId);
            //setProfilePic(profileimage);
        } else {
            // User is not signed in, handle accordingly (e.g., redirect to login screen)
        }

        userId = getUserId();

//        if (getIntent() != null) {
//            String type = getIntent().getStringExtra("signInMethod");
//            if (type != null && type.equals("Google")) {
//                // Now you can work with the GoogleSignInAccount object
//                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//                if (account != null) {
//                    setProfilePic(account.getPhotoUrl());
//                }
//            } else if (type != null && type.equals("inApp")) {
//                getUserDataFromDatabase(userId);
//                setProfilePic(profileimage);
//            }
//        }
        requestPermissions();
    }



    private void getUserDataFromDatabase(String userId) {
        DatabaseReference currentUserRef = userDatabaseReference.child(userId);
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User_Data_Model userData = dataSnapshot.getValue(User_Data_Model.class);
                    assert userData != null;
                    profileimage = userData.getSelected_profile_image();
                } else {
                    // Handle scenario where user data does not exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }


    private void setupUI() {
        sosFab = findViewById(R.id.fab);
        appBar = findViewById(R.id.bottomAppBar);
        navView = findViewById(R.id.bottomNavigationView);
        loadFragment(new fragment_home());


        sosFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navView.getMenu().getItem(4).setChecked(true);
                loadFragment(new fragment_emergency());
            }
        });

        appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        navView.setBackground(null);
        navView.getMenu().getItem(2).setEnabled(false);
        navView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                loadFragment(new fragment_home());
            } else if (id == R.id.menu_prepare) {
                loadFragment(new fragment_prepare());
            } else if (id == R.id.menu_recover) {
                loadFragment(new fragment_recover());
            } else if (id == R.id.menu_Emergency) {
                loadFragment(new fragment_emergency());
            }
            return true;
        });
    }


    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null); // Add the transaction to the back stack with a null name
        transaction.setCustomAnimations(R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out);
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

//    public void setProfilePic(int image) {
//        fragment_home home = new fragment_home();
//        home.setProfilePic(R.drawable.avatar8);
//    }
//
//    public void setProfilePic(Uri image) {
//        fragment_home home = new fragment_home();
//        home.setProfilePic(image);
//    }

    public String getUserId(){
        String userId;
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
        return userId;
    }
}