package com.example.pingmesafe.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.pingemesafe.R;
import com.example.pingmesafe.Adapters.Disaster;
import com.example.pingmesafe.Adapters.MyAdapter_prepare;

import java.util.ArrayList;
import java.util.List;

public class fragment_prepare extends Fragment {

    public fragment_prepare() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__prepare, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        // Set up StaggeredGridLayoutManager with 2 columns and vertical orientation
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // Prepare data for RecyclerView
        List<Disaster> disasterList = prepareDisasterData();

        // Initialize Adapter
        MyAdapter_prepare adapter = new MyAdapter_prepare(disasterList);

        // Set Adapter to RecyclerView
        recyclerView.setAdapter(adapter);
    }

    private List<Disaster> prepareDisasterData() {
        // Here you can prepare your disaster data
        // For demonstration, I'm creating sample data
        List<Disaster> data = new ArrayList<>();
        data.add(new Disaster("Earthquake", R.drawable.earthquake));
        data.add(new Disaster("Landslide", R.drawable.landslide));
//        data.add(new Disaster("Blizzard", R.drawable.blizzard));
//        data.add(new Disaster("Heatwave", R.drawable.heatwave));
//        data.add(new Disaster("Landslide", R.drawable.landslide)); // Duplicate name, using same image for now
//        data.add(new Disaster("Drought", R.drawable.drought));
//        data.add(new Disaster("Avalanche", R.drawable.avalanche));
//        data.add(new Disaster("Volcanic eruption", R.drawable.volcanic_eruption));
//        data.add(new Disaster("Tornado", R.drawable.tornado));
//        data.add(new Disaster("Wildfire", R.drawable.wildfire));
//        data.add(new Disaster("Flood", R.drawable.flood));
//        data.add(new Disaster("Cyclone", R.drawable.cyclone));
//        data.add(new Disaster("Landslide", R.drawable.landslide)); // Duplicate name, using same image for now
//        data.add(new Disaster("Tsunami", R.drawable.tsunami));

        // Add more disasters as needed

        return data;
    }
}
