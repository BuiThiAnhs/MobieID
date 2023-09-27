package com.ITS.mobieid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ITS.mobieid.R;

public class Notify_Success extends AppCompatActivity {
    TextView txtPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_success);init();
        String data="";
        Intent intent= getIntent();
        if (intent != null) {
            data = intent.getStringExtra("success");
            txtPhoneNumber.setText(data);
        }
    }
    private  void init()
    {
        txtPhoneNumber = findViewById(R.id.txt_phonenumber);
    }
}