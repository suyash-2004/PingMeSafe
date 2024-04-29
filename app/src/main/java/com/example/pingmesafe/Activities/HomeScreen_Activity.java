package com.example.pingmesafe.Activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.pingemesafe.R;
import com.example.pingemesafe.databinding.ActivityMainBinding;
import com.example.pingmesafe.Adapters.MyAdapter;
import com.example.pingmesafe.FireBase.User_Location_Model;
import com.example.pingmesafe.Fragments.fragment_emergency;
import com.example.pingmesafe.Fragments.fragment_home;
import com.example.pingmesafe.Fragments.fragment_prepare;
import com.example.pingmesafe.Fragments.fragment_recover;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HomeScreen_Activity extends AppCompatActivity {
    double latitude;
    double longitude;
    private String alertID;

    FloatingActionButton fab_sos;
    MyAdapter adapter;
    RecyclerView recyclerView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final DatabaseReference UserCurrentLocationDatabaseReference = FirebaseDatabase.getInstance().getReference("UserCurrentLocation");
    List<List<String>> list= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_home_screen);


        fab_sos = findViewById(R.id.fab);
        fab_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen_Activity.this, Emergency_activity.class));
            }
        });
        //recyclerView =  findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> item1 = new ArrayList<>();
        item1.add("Disaster Name 1");
        item1.add("Disaster Details 1");
        item1.add("Disaster Time 1");
        list.add(item1);

        //adapter = new MyAdapter(list);
        //recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();


        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        loadFragment(new fragment_home());

        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        bottomNavigationView.setOnItemSelectedListener(item -> {
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


        fetchLocation();
        sendLocationToDatabase(latitude, longitude);
    }

    private void fetchLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                });
    }

    private void sendLocationToDatabase(double latitude, double longitude) {
        FetchAlertID();
        UserCurrentLocationDatabaseReference.child(alertID).setValue(new User_Location_Model(latitude, longitude));
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit);
        ft.replace(R.id.frame_layout, fragment);
        ft.addToBackStack(null);
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