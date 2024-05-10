package com.example.pingmesafe.Fragments;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pingmesafe.Activities.Profile_Activity;
import com.example.pingmesafe.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;
import java.util.Objects;

public class fragment_home extends Fragment implements OnMapReadyCallback{
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("unsafe alerts");
    private static final String MAP_VIEW_STATE_KEY = "mapViewState";
    SupportMapFragment mapView;
    private GoogleMap googleMap;
    public AppCompatImageView profilePicture;
    SearchView searchView;


    View viewTemp;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    Boolean userSOS = false;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewTemp = view;
        mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapView == null) {
            mapView = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapView).commit();
        }

        profilePicture = view.findViewById(R.id.image_profile);
        searchView = view.findViewById(R.id.search_view);

        mapView.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        setProfilePic(user.getPhotoUrl());

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), Profile_Activity.class));
            }
        });
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.map_style));
        fetchDBData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.setQuery(query, false);
                searchView.requestFocusFromTouch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //googleMap.setMapType(MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(true);
            //googleMap.getUiSettings().setMapToolbarEnabled(true);
            //googleMap.getUiSettings().setCompassEnabled(true);
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
                fetchDBData();
                /*double latitude = snapshot.child("latitude").getValue(Double.class);
                double longitude = snapshot.child("longitude").getValue(Double.class);
                String name = snapshot.child("name").getValue(String.class);
                String message = snapshot.child("alertMessage").getValue(String.class);
                addMarker(latitude, longitude, name, message);*/
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (Objects.equals(snapshot.getKey(), fetchAlertID())) {
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

        if(googleMap!=null){
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Nullable
                @Override
                public View getInfoContents(@NonNull Marker marker) {
                    return null;
                }

                @NonNull
                @Override
                public View getInfoWindow(@NonNull Marker marker) {
                    View view = getLayoutInflater().inflate(R.layout.custom_info_window_marker,null);
                    TextView name = view.findViewById(R.id.Name);
                    TextView snippet = view.findViewById(R.id.Snippet);
                    TextView latitude = view.findViewById(R.id.Latitude);
                    TextView longitude = view.findViewById(R.id.Longitude);
                    AppCompatButton btn_call = view.findViewById(R.id.btn_info_window_call);
                    AppCompatButton btn_message = view.findViewById(R.id.btn_info_window_message);

                    LatLng coordinates = marker.getPosition();
                    name.setText(String.valueOf(marker.getTitle()));
                    snippet.setText(String.valueOf(marker.getSnippet()));
                    latitude.setText(String.valueOf(coordinates.latitude));
                    longitude.setText(String.valueOf(coordinates.longitude));
                    return view;
                }
            });
        }
        initiateSearch();
    }

    private void initiateSearch() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // When the search button is pressed, get the latitude and longitude of the searched location
                Geocoder geocoder = new Geocoder(requireContext());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(query, 1);
                    assert addresses != null;
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        double latitude = address.getLatitude();
                        double longitude = address.getLongitude();

                        // Add a marker on the searched location
                        addMarkerSearchLocation(latitude, longitude, "Searched Location", query);

                        // Move the camera to the searched location
                        LatLng latLng = new LatLng(latitude, longitude);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Close the SearchView
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.onActionViewCollapsed();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    public void addMarkerSearchLocation(double latitude, double longitude, String title, String snippet) {
        if (googleMap != null) {
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(snippet).snippet(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_location));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
            googleMap.addMarker(markerOptions);
        }
    }

    public void setProfilePic(Uri imageUri){
        Glide.with(this)
                .load(imageUri)
                .into(profilePicture);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_type_menu, menu);
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

    //function to fetch data from realtime databse and add marker for all other user's sos alerts
    public void fetchDBData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                googleMap.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
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

    //function to fetch user's unique alert ID from the internal device storage
    public String fetchAlertID() {
        StringBuilder content = new StringBuilder();
        if (getActivity() != null) {
            try {
                FileInputStream fis = getActivity().openFileInput("FireBaseAlretID.txt");
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
        } else {
            // Handle the case where getActivity() returns null
            // You can log an error or show a message to indicate the issue
        }
        return content.toString();
    }



    //function to add a marker on the map
    public void addMarker(double latitude, double longitude, String title, String alertMessage) {
        if (googleMap != null) {

            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(title).snippet(alertMessage).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_sos));
            googleMap.addMarker(markerOptions);
        }
    }


    //these 4 functions are used to sove and restore the state of the mpa when it is cosed and reopened
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
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        setProfilePic(user.getPhotoUrl());
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

//    public void setProfilePic(int image) {
//        profilePicture = viewTemp.findViewById(R.id.image_profile);
//        profilePicture.setImageResource(image);
//    }
//
//    public void setProfilePic(Uri image) {
//        Glide.with(this).load(image).into(profilePicture);
//    }
}