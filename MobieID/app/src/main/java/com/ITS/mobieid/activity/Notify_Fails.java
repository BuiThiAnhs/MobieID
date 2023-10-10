package com.ITS.mobieid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ITS.mobieid.R;
import com.ITS.mobieid.service.CallService;


public class Notify_Fails extends AppCompatActivity {
    private final static String TAG="Notifi Fails";
    Button btn_VoiceOTP;
    TextView txt_error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_fails);
        init();
        String data="";
        Intent intent= getIntent();
        if (intent != null) {
            data = intent.getStringExtra("error");
        }
        String finalData = data;
        btn_VoiceOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOTP(finalData);
                openService();
            }
        });
    }
    private void init()
    {
        btn_VoiceOTP = findViewById(R.id.btn_SMSOTP);
        txt_error = findViewById(R.id.txtinput_error);
    }
    private void openOTP(String OTP) {
        //activity
        Intent intent = new Intent(this, CodeOTP.class);
        intent.putExtra("OTP", OTP);
        Toast.makeText(this, "Đang chuyển sang SMS OTP ...", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
    private void openService()
    {
        Intent callIntent = new Intent(this, CallService.class);
        startActivity(callIntent);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Intent serviceIntent = new Intent(this, CallService.class);
//        stopService(serviceIntent);
//    }
}