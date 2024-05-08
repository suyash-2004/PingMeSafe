package com.example.pingmesafe.Activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

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

import com.example.pingemesafe.R;
import com.example.pingmesafe.Adapters.MyAdapter;
import com.example.pingmesafe.Fragments.fragment_emergency;
import com.example.pingmesafe.Fragments.fragment_home;
import com.example.pingmesafe.Fragments.fragment_prepare;
import com.example.pingmesafe.Fragments.fragment_recover;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen_Activity extends AppCompatActivity {
    private String alertID;

    FloatingActionButton fab_sos;

    MyAdapter adapter;
    RecyclerView recyclerView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    List<List<String>> list= new ArrayList<>();

    public HomeScreen_Activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_home_screen);

        fab_sos = findViewById(R.id.fab);
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);


        fab_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.getMenu().getItem(4).setChecked(true);
                loadFragment(new fragment_emergency());
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

        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
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

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //fm.popBackStack();
        transaction.setCustomAnimations(R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out);
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
}