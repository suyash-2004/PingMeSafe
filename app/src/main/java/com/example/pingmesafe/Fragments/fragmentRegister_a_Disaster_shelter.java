package com.example.pingmesafe.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pingemesafe.R;

public class fragmentRegister_a_Disaster_shelter extends Fragment {

    public fragmentRegister_a_Disaster_shelter() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_a__disaster_shelter, container, false);
    }
}