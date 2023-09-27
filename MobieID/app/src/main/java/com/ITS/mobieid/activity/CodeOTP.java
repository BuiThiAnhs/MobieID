package com.ITS.mobieid.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ITS.mobieid.R;

import com.ITS.mobieid.callback.CoverageCallback;
import com.ITS.mobieid.manager.APIManager;
import com.ITS.mobieid.util.Util;
import com.ipification.mobile.sdk.android.IPConfiguration;
import com.ipification.mobile.sdk.android.IPEnvironment;
import com.ipification.mobile.sdk.android.IPificationServices;
import com.ipification.mobile.sdk.android.callback.CellularCallback;
import com.ipification.mobile.sdk.android.callback.IPificationCallback;
import com.ipification.mobile.sdk.android.exception.CellularException;
import com.ipification.mobile.sdk.android.exception.IPificationError;
import com.ipification.mobile.sdk.android.request.AuthRequest;
import com.ipification.mobile.sdk.android.response.AuthResponse;
import com.ipification.mobile.sdk.android.response.CoverageResponse;
import com.ipification.mobile.sdk.im.IMService;


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
        initData();
        initPhoneCall();
        initView();

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
        Log.d(TAG, "initData: "+ data);
    }
    private void initView()
    {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callIPFlow();

            }
        });
        Log.d(TAG, "initView:  start");
    }
    private void initPhoneCall()
    {
        IPConfiguration.getInstance().setCustomUrls(true);
        IPConfiguration.getInstance().setAUTHORIZATION_URL(Uri.parse("https://api.smartbot.vn/stage/auth"));
        IPConfiguration.getInstance().setCOVERAGE_URL(Uri.parse("https://api.smartbot.vn/voiceotp/call"));
        IPConfiguration.getInstance().setENV(IPEnvironment.SANDBOX);
        IPConfiguration.getInstance().setCLIENT_ID("maitrongjthuaanf");
        IPConfiguration.getInstance().setREDIRECT_URI(Uri.parse("com.example.demo://path/"));
    }
    private void openSuccessActivity() {
        Intent intent = new Intent(this, Notify_Success.class);
        startActivity(intent);
        Log.d(TAG, "openSuccessActivity:");
    }
    private  void callIPFlow()
    {
        Log.d(TAG, "callIPFlow: start");
        IPConfiguration.getInstance().setDebug(true);
        callCheckCoverage(((isAvaiable, operatorCode, errorMessage) -> {
            if (isAvaiable) {
                startAuth();
            }
            else
            {
                //fail
            }
        }));

    }
    private void startAuth()
    {
        IPificationCallback callback = new IPificationCallback() {
            @Override
            public void onSuccess(@NonNull AuthResponse authResponse) {
                if (authResponse.getCode() !=null) {
                    callTokenExchange(authResponse.getCode());
                }
                else {
                    //fail
                }
            }

            @Override
            public void onError(@NonNull IPificationError iPificationError) {

            }

            @Override
            public void onIMCancel() {

            }
        };
        AuthRequest.Builder auRequestBuilder = new AuthRequest.Builder();
        auRequestBuilder.addQueryParam("called", data);
        auRequestBuilder.setScope("openid ip:phone_verify");
        IPificationServices.Factory.startAuthentication(this, auRequestBuilder.build(), callback);
        Log.d(TAG, "startAuth: " + data);

    }
    private void callTokenExchange(String code)
    {
        APIManager.doPostOTP(code, (((response, errorMessage) -> {
            if (!response.equals("")) {
                String called = Util.parseUserInfoJSON(response, "called");
                String calling = Util.parseUserInfoJSON(response, "calling");
                Log.e(TAG, "phoneNumberVerified :" + called + "phone_number:" + calling);
                if (called.equals("true") || !calling.equals("")) {
                    Log.d(TAG, "callTokenExchange: " + called);
                    openSuccessActivity();
                } else {
//                    openFailActivity("error: phone numberverify");
                }
            } else {
//                openFailActivity("error: phone number");
            }
        })));
    }
    private void callCheckCoverage(CoverageCallback coverageCallback)
    {
        IPificationServices.Factory.startCheckCoverage(data, this, new CellularCallback<CoverageResponse>() {
            @Override
            public void onSuccess(CoverageResponse coverageResponse) {
                if (coverageResponse.isAvailable()) {
                    coverageCallback.result(true, coverageResponse.getOperatorCode(),"");
                    Log.d(TAG, "onSuccess:  true");
                }
                else {
                    coverageCallback.result(false, coverageResponse.getOperatorCode(),"");
                    Log.e(TAG, "CheckCoverage Failed: Not supported");
                }
            }

            @Override
            public void onError(@NonNull CellularException e) {
                Log.e(TAG, "Check Error: " + e.getErrorMessage());
                coverageCallback.result(false, "", e.getErrorMessage());
            }

            @Override
            public void onIMCancel() {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMService.Factory.onActivityResult(requestCode, resultCode, data);
    }




}