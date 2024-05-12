package com.example.pingmesafe.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pingmesafe.Activities.Emergency_activity;
import com.example.pingmesafe.Activities.HomeScreen_Activity;
import com.example.pingmesafe.FireBase.UnSafe_Alert_Model;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import com.example.pingmesafe.R;

public class fragment_emergency extends Fragment {

    private double latitude;
    private double longitude;
    private final String deviceName = android.os.Build.MODEL;
    private String SOS_message = "";
    RelativeLayout btn_back;

    private String Time;
    private String SOSname;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private DatabaseReference AlertsdatabaseReference;
    private String alertID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emergency, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        AppCompatButton btn_needHelp = view.findViewById(R.id.btn_needHelp);
        btn_needHelp.setOnClickListener(v -> {
            ShowSOSMessageDialog();
        });


        AlertsdatabaseReference = FirebaseDatabase.getInstance().getReference("unsafe alerts");

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!fileExists("FireBaseAlretID.txt")) {
                    alertID = AlertsdatabaseReference.push().getKey();
                    assert alertID != null;
                    createTextFile(fragment_emergency.this, "FireBaseAlretID.txt", alertID);
                } else {
                    StringBuilder content = new StringBuilder();
                    try {
                        FileInputStream fis = requireContext().openFileInput("FireBaseAlretID.txt");
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
        }).start();

        btn_back = view.findViewById(R.id.layout_btn_back_emergency);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


    }

    private String getCurrentTime(){
        final String[] time = new String[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                time[0] = sdf.format(calendar.getTime());
            }
        }).start();
        return time[0];
    }

     private void ShowSOSMessageDialog() {
        Dialog dialog_SOS_message = new Dialog(requireActivity());
        dialog_SOS_message.setContentView(R.layout.sos_dialog_message);
        dialog_SOS_message.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText SOSMessage = dialog_SOS_message.findViewById(R.id.edt_SOSMsg);
        AppCompatButton btn_send_sos = dialog_SOS_message.findViewById(R.id.btn_send_sos);

        btn_send_sos.setOnClickListener(v1 -> {
            if(!SOSMessage.getText().toString().isEmpty()){
                SOS_message = SOSMessage.getText().toString().trim();
                SOSname = GoogleSignIn.getLastSignedInAccount(requireActivity()).getDisplayName();
                alertID = fetchAlertID();
                Time = getCurrentTime();
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
                SendSOSAlert(fusedLocationClient);
            }
            dialog_SOS_message.dismiss();
        });
        dialog_SOS_message.show();
    }

    private void createTextFile(fragment_emergency context, String fileName, String alertID) {
        try {
            FileOutputStream fos = context.requireActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(alertID.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean fileExists(String fileName) {
        File file = new File(requireContext().getFilesDir(), fileName);
        return file.exists();
    }

    private void SendSOSAlert(FusedLocationProviderClient fusedLocationClient) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            AlertsdatabaseReference.child(alertID).setValue(new UnSafe_Alert_Model(latitude, longitude, SOSname, SOS_message, deviceName, Time));
                        }
                    });
        }
    }


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
}