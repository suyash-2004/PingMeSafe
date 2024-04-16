package com.example.pingmesafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.pingemesafe.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class HomeScreen_Activity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        loadFragment(new fragment_maps());

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

        initBottomSheet();
    }

    private void initBottomSheet() {
        findViewById(R.id.layoutBottomSheet).findViewById(R.id.layoutEmergency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen_Activity.this,Emergency_activity.class));
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayoutContainer, fragment);
        ft.commit();
    }
}