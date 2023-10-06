package com.ITS.mobieid.activity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import com.ITS.mobieid.R;


import com.ITS.mobieid.service.OTPReceiver;



public class CodeOTP extends AppCompatActivity {

    private final static String TAG ="CodeOTP";
    private String data="";
    EditText editCodeotp;
    Button btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_otp);
        initWidget();
        requestPermissions();
        new OTPReceiver().setEdit_otp(editCodeotp);
        initData();

    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(CodeOTP.this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CodeOTP.this, new String[]{
                    Manifest.permission.RECEIVE_SMS
            }, 100);
        }
    }

    public void initWidget()
    {
        btnConfirm = findViewById(R.id.btnConfirm);
        editCodeotp = findViewById(R.id.edit_codeotp);
    }
    public void initData()
    {

        Intent intent= getIntent();
        if (intent != null) {
            data = intent.getStringExtra("sms");

        }
    }
    private void initView()
    {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callIPFlow();

            }
        });

    }

    private void openSuccessActivity() {
        Intent intent = new Intent(this, Notify_Success.class);
        startActivity(intent);

    }



}