package com.example.pingmesafe.Fragments;

import com.google.android.gms.maps.GoogleMap;

public interface MapsFragmentListener {
    void onMapReady(GoogleMap map);

    void addMarker(double latitude, double longitude, String title);
}
