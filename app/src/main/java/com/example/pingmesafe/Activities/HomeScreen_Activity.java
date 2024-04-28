package com.example.pingmesafe.Activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.pingemesafe.R;
import com.example.pingmesafe.FireBase.User_Location_Model;
import com.example.pingmesafe.Fragments.fragmentRegister_a_Disaster_shelter;
import com.example.pingmesafe.Fragments.fragment_Become_Aware;
import com.example.pingmesafe.Fragments.fragment_maps;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.material.navigation.NavigationView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class HomeScreen_Activity extends AppCompatActivity {
    double latitude;
    double longitude;
    private String alertID;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    fragment_maps mapsFragment;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final DatabaseReference UserCurrentLocationDatabaseReference = FirebaseDatabase.getInstance().getReference("UserCurrentLocation");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        View bottomSheet = findViewById(R.id.layoutBottomSheet);

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        //finding IDs from xml file
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        //setting Action Bar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting Navigation View in Drawer layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //loading default map fragment
        loadFragment(new fragment_maps());

        //change fragments when navigation view items are clicked
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.itemMap) {
                loadFragment(new fragment_maps());
            } else if (id == R.id.itemBecomeAware) {
                loadFragment(new fragment_Become_Aware());
            } else if (id == R.id.itemRegisterShelter) {
                loadFragment(new fragmentRegister_a_Disaster_shelter());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        //initializing BottomSheet in Home Screen
        initBottomSheet();

        fetchAndSendLocation();

    }


    private void fetchAndSendLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        // Send the location to the Realtime Database
                        sendLocationToDatabase(latitude, longitude);
                    }
                });
    }

    private void sendLocationToDatabase(double latitude, double longitude) {
        FetchAlertID();
        UserCurrentLocationDatabaseReference.child(alertID).setValue(new User_Location_Model(latitude, longitude));
    }
    private void initBottomSheet() {
        findViewById(R.id.layoutBottomSheet).findViewById(R.id.layoutEmergency).setOnClickListener(v -> startActivity(new Intent(HomeScreen_Activity.this,Emergency_activity.class)));
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayoutContainer, fragment);
        ft.commit();
    }

    private void FetchCurLocation(){
        FusedLocationProviderClient fusedLocationClient;
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    });
        }
    }

    private void FetchAlertID(){
        if(!fileExists("FireBaseAlretID.txt")) {
            alertID = UserCurrentLocationDatabaseReference.push().getKey();
            assert alertID != null;
            createTextFile(HomeScreen_Activity.this, "FireBaseAlretID.txt", alertID);
        }else{
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

    public boolean fileExists(String fileName) {
        File file = new File(getApplicationContext().getFilesDir(), fileName);
        return file.exists();
    }

    private void createTextFile(HomeScreen_Activity context, String fileName, String alertID) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(alertID.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}