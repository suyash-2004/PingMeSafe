package com.example.pingmesafe;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;


import com.example.pingemesafe.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.Executor;

public class fragment_maps extends Fragment implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("unsafe alerts");
    private final DatabaseReference UserCurrentLocationDatabaseReference = FirebaseDatabase.getInstance().getReference("UserCurrentLocation");
    private static final String MAP_VIEW_STATE_KEY = "mapViewState";
    private FloatingActionButton fab;
    SupportMapFragment mapView;
    private GoogleMap googleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    double latitude = 0;
    double longitude = 0;
    Boolean userSOS = false;
    private FusedLocationProviderClient fusedLocationClient;
    private OnMapReadyCallback callback = this;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> showMapTypeMenu());
        mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapView == null) {
            mapView = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapView).commit();
        }
        mapView.getMapAsync(this);
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        fetchDBData();
        googleMap.setMyLocationEnabled(!isUserSOS());
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMapType(MAP_TYPE_NORMAL);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle new SOS alert added
                double latitude = snapshot.child("latitude").getValue(Double.class);
                double longitude = snapshot.child("longitude").getValue(Double.class);
                String name = snapshot.child("name").getValue(String.class);
                String message = snapshot.child("alertMessage").getValue(String.class);
                addMarker(latitude, longitude, name, message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                double latitude = snapshot.child("latitude").getValue(Double.class);
                double longitude = snapshot.child("longitude").getValue(Double.class);
                String name = snapshot.child("name").getValue(String.class);
                String message = snapshot.child("alertMessage").getValue(String.class);
                addMarker(latitude, longitude, name, message);
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (Objects.equals(snapshot.getKey(), fetchAlertID())) {
                    userSOS=false;
                    googleMap.setMyLocationEnabled(true);
                }
                fetchDBData();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle movement of SOS alerts (if needed)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors (if needed)
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_type_menu, menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_normal) {
            googleMap.setMapType(MAP_TYPE_NORMAL);
            return true;
        } else if (id == R.id.menu_satellite) {
            googleMap.setMapType(MAP_TYPE_SATELLITE);
            return true;
        } else if (id == R.id.menu_hybrid) {
            googleMap.setMapType(MAP_TYPE_HYBRID);
            return true;
        } else if (id == R.id.menu_terrain) {
            googleMap.setMapType(MAP_TYPE_TERRAIN);
            return true;
        } else {
            return true;
        }
    }

    private void showMapTypeMenu() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), fab);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.map_type_menu);
        popupMenu.show();
    }

    private boolean isUserSOS() {
        databaseReference.child(fetchAlertID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userSOS = snapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return userSOS;
    }


    public void fetchDBData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                googleMap.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (Objects.equals(dataSnapshot.getKey(), fetchAlertID())) {
                        googleMap.setMyLocationEnabled(false);
                    }
                            double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                            double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                            String name = dataSnapshot.child("name").getValue(String.class);
                            String message = dataSnapshot.child("alertMessage").getValue(String.class);
                            addMarker(latitude, longitude, name, message);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String fetchAlertID(){
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream fis = requireActivity().openFileInput("FireBaseAlretID.txt");
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
        return content.toString();
    }

    public void addMarker(double latitude, double longitude, String title, String alertMessage) {
        if (googleMap != null) {

            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(title).snippet(alertMessage);
            googleMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getChildFragmentManager().putFragment(outState, MAP_VIEW_STATE_KEY, mapView);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mapView = (SupportMapFragment) getChildFragmentManager().getFragment(savedInstanceState, MAP_VIEW_STATE_KEY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDBData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }
}