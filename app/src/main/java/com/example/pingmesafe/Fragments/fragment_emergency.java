package com.example.pingmesafe.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.pingmesafe.Activities.HomeScreen_Activity;
import com.example.pingmesafe.FireBase.UnSafe_Alert_Model;
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


import com.example.pingemesafe.R;

public class fragment_emergency extends Fragment {

    private AppCompatButton btn_sos_dialog_YES;
    private AppCompatButton btn_sos_dialog_Cancel;
    private double latitude;
    private double longitude;
    private final String deviceName = android.os.Build.MODEL;
    private String SOS_message = "";
    private final String currentTime = getCurrentTime();
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
        AlertsdatabaseReference = FirebaseDatabase.getInstance().getReference("unsafe alerts");

        if (!fileExists("FireBaseAlretID.txt")) {
            alertID = AlertsdatabaseReference.push().getKey();
            createTextFile("FireBaseAlretID.txt", alertID);
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

        btn_needHelp.setOnClickListener(v -> {
            showSOSMessageDialog();
        });
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(calendar.getTime());
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        //fm.popBackStack();
        transaction.setCustomAnimations(R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out);
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void showSOSMessageDialog() {
        Dialog dialog_SOS_message = new Dialog(requireContext());
        dialog_SOS_message.setContentView(R.layout.sos_dialog_message);

        EditText SOSMessage = dialog_SOS_message.findViewById(R.id.edt_SOSMsg);
        EditText name = dialog_SOS_message.findViewById(R.id.edt_name);
        AppCompatButton btn_send_sos = dialog_SOS_message.findViewById(R.id.btn_send_sos);
        AppCompatButton btn_cancel = dialog_SOS_message.findViewById(R.id.btn_send_sos);

        btn_send_sos.setOnClickListener(v1 -> {
            if (!SOSMessage.getText().toString().isEmpty()) {
                SOS_message = SOSMessage.getText().toString().trim();
                SOSname = name.getText().toString().trim();
                getCurrentLocation();
                loadFragment(new fragment_home());
            }
            dialog_SOS_message.dismiss();
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog_SOS_message.dismiss();
            }
        });
        dialog_SOS_message.show();
    }

    private void createTextFile(String fileName, String alertID) {
        try {
            FileOutputStream fos = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(alertID.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean fileExists(String fileName) {
        File file = new File(requireContext().getFilesDir(), fileName);
        return file.exists();
    }

    private void sendSOSAlert() {
        AlertsdatabaseReference.child(alertID).setValue(new UnSafe_Alert_Model(latitude, longitude, SOSname, SOS_message, deviceName, currentTime));
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            sendSOSAlert();
                        }
                    });
        }
    }
}