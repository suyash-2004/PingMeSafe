package com.example.pingmesafe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pingemesafe.R;

public class FragmentBecomeAware_NavigationView extends Fragment {

    public FragmentBecomeAware_NavigationView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment__become__aware, container, false);
    }
}