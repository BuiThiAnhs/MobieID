package com.ITS.mobieid.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.ITS.mobieid.R;
import com.ITS.mobieid.callback.CoverageCallback;
import com.ITS.mobieid.manager.APIManager;
import com.ITS.mobieid.util.Util;
import com.ipification.mobile.sdk.android.IPConfiguration;
import com.ipification.mobile.sdk.android.request.AuthRequest;
import com.its.mobileid.MobileID;
import com.its.mobileid.MobileIDEnv;
import com.its.mobileid.callback.MobileIDCallback;
import com.its.mobileid.error.MobileIDError;
import com.its.mobileid.response.MobileIDAuthResponse;
import com.its.mobileid.response.MobileIDCoverageResponse;


public class MainActivity extends AppCompatActivity {
    public final static String TAG = "Phone Verifi";


    ImageView imageView;
    EditText edit_PhoneNumber;
    Button btnAuthentic, btnVoiceOTP;
    ProgressBar progressBar;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
        initView();
        initMobileID();

        btnVoiceOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.RECEIVE_SMS

                    }, 100);
                }
                openOTP(edit_PhoneNumber.getText().toString());
            }
        });
    }

    void initWidget() {
        imageView = findViewById(R.id.imageView);
        edit_PhoneNumber = findViewById(R.id.edit_phoneNumber);
        btnAuthentic = findViewById(R.id.btnAuthentic);
        btnVoiceOTP = findViewById(R.id.btnVoiceOTP);
        progressBar =  findViewById(R.id.progress_circular);
    }


    private void initView() {
        btnAuthentic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        callIPFlow();
                    }
                }, 300);

            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void openFailActivity(String error) {
        Intent intent = new Intent(this, Notify_Fails.class);
        intent.putExtra("error", error);
        startActivity(intent);
        Toast.makeText(MainActivity.this, "Xác thực thất bại.", Toast.LENGTH_SHORT).show();
    }

    private void openSuccessActivity(String phonenumber) {
        Intent intent = new Intent(this, Notify_Success.class);
        intent.putExtra("success", phonenumber);
        startActivity(intent);
        Toast.makeText(MainActivity.this, "Xác thực thành công.", Toast.LENGTH_SHORT).show();
    }
    private void openOTP(String OTP) {
        Intent intent = new Intent(this, CodeOTP.class);

        intent.putExtra("OTP", OTP);
        startActivity(intent);
        Toast.makeText(MainActivity.this, "Đang chuyển sang SMS OTP ...", Toast.LENGTH_SHORT).show();

    }

    private void initMobileID() {
        MobileID.Factory.setEnv(MobileIDEnv.PRODUCTION,"60e8291b368fbf97f80fd055","com.example.demo://path/" );

    }

    private void callIPFlow() {
        IPConfiguration.getInstance().setDebug(true);

        // check Coverage
        callCheckCoverage((isAvailable, operatorCode, errorMessage) -> {
            String phoneNumber = edit_PhoneNumber.getText().toString();
            Log.d(TAG, "callIPFlow:  check Coverage +++" + isAvailable);
            if (isAvailable) {
                Log.d(TAG, "callIPFlow:  " + isAvailable);
                startAuth();

            } else {
                // TODO fallback to other service
                Log.e(TAG, "callCheckCoverage failed");
                openFailActivity(phoneNumber);
            }
        });
        Log.d(TAG, "callIPFlow:");
    }

    private void startAuth() {
        String user_input_phone_number = edit_PhoneNumber.getText().toString();
        String phoneNumbers ="";
        if (user_input_phone_number.startsWith("0")) {
            phoneNumbers = "+84" + user_input_phone_number.substring(1);
        }
          MobileIDCallback callback = new MobileIDCallback<MobileIDAuthResponse>() {
              @Override
              public void onSuccess(MobileIDAuthResponse response) {
                  Log.d(TAG, "onSuccess:  getcode " + response.getCode());
                  callTokenExchange(response.getCode());
              }

              @Override
              public void onError(@NonNull MobileIDError error) {
                  String phoneNumber = edit_PhoneNumber.getText().toString();
                  Log.d(TAG, "onError:  false" + error.getErrorMessage()) ;
                  openFailActivity(phoneNumber);
              }

          };


        AuthRequest.Builder authRequestBuilder = new AuthRequest.Builder();
        authRequestBuilder.addQueryParam("login_hint", phoneNumbers);
        authRequestBuilder.setScope("openid ip:phone_verify");
        MobileID.Factory.startAuthenticate(MainActivity.this, phoneNumbers, callback);
        Log.d(TAG, "startAuths: " + phoneNumbers);
    }

    private void callCheckCoverage(CoverageCallback coverageCallback) {
        String user_input_phone_number = edit_PhoneNumber.getText().toString();
        String phoneNumbers ="";
        if (user_input_phone_number.startsWith("0")) {
            phoneNumbers = "84" + user_input_phone_number.substring(1);
        }
        Log.d(TAG, "callCheckCoverage: log sdt" + phoneNumbers);
        MobileID.Factory.startCheckCoverage(MainActivity.this, phoneNumbers, new MobileIDCallback<MobileIDCoverageResponse>() {
           @Override
           public void onError(@NonNull MobileIDError error) {
               Log.e(TAG, "Check Error: " + error.getErrorMessage());
               coverageCallback.result(false, "", error.getErrorMessage());
           }

           @Override
           public void onSuccess(MobileIDCoverageResponse response) {Log.d(TAG, "onSuccess: CoverageResponse coverageResponse" + response);
               if (response.isAvailable()) {
                   coverageCallback.result(true, response.getOperatorCode(), "");
                   Log.d(TAG, "onSuccess:  true"+response.getOperatorCode());
               } else {
                   coverageCallback.result(false, response.getOperatorCode(), "");
                   Log.e(TAG, "CheckCoverage Failed: Not supported");
               }
           }

        });
        Log.d(TAG, "callCheckCoverage: ");
    }
    private void callTokenExchange(String code) {
        APIManager.doPostToken(code, ((response, errorMessage) -> {
            if (!response.equals("")) {
                String phoneNumberVerified = Util.parseUserInfoJSON(response, "phone_number_verified");
                String phoneNumber = Util.parseUserInfoJSON(response, "login_hint");
                Log.e(TAG, "phoneNumberVerified :" + phoneNumberVerified + "phone_number:" + phoneNumber);
                if (phoneNumberVerified.equals("true")) {
                    openSuccessActivity(phoneNumber);
                } else {
                    String user_input_phone_number = edit_PhoneNumber.getText().toString();
                    openFailActivity(user_input_phone_number);
                }
            } else {
                String phoneNumber = edit_PhoneNumber.getText().toString();
                openFailActivity(phoneNumber);

                
            }
        }));
    }

}