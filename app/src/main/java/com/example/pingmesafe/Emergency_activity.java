package com.example.pingmesafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.example.pingemesafe.R;
import com.example.pingmesafe.FireBase.UnSafe_Alert_Model;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Emergency_activity extends AppCompatActivity {

    AppCompatButton btn_SOS;
    AppCompatButton btn_call;
    AppCompatButton btn_sos_dialog_YES;
    AppCompatButton btn_sos_dialog_Cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        btn_SOS = findViewById(R.id.btn_SOS);
        btn_call = findViewById(R.id.btn_Call);

        btn_SOS.setOnClickListener(v -> {
            Dialog dialog_SOS = new Dialog(this);
            dialog_SOS.setContentView(R.layout.sos_dialog);

            btn_sos_dialog_YES = dialog_SOS.findViewById(R.id.btn_YES);
            btn_sos_dialog_Cancel = dialog_SOS.findViewById(R.id.btn_Cancel);

            btn_sos_dialog_YES.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("unsafe alerts");
                    String alertID = databaseReference.push().getKey();
                    databaseReference.child(alertID).setValue(new UnSafe_Alert_Model(542.3,642.4,"Suyash","TestMessage"));
                }
            });

            dialog_SOS.show();

        });
    }
}