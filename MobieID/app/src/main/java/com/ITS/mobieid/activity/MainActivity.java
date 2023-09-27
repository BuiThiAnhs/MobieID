package com.ITS.mobieid.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "Phone Verifi";


    ImageView imageView;
    EditText edit_PhoneNumber, edit_Countries;
    Button btnAuthentic, btnVoiceOTP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
        initView();
        initIPification();

        btnVoiceOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOTP(edit_PhoneNumber.getText().toString());
            }
        });
    }

    void initWidget() {
        imageView = findViewById(R.id.imageView);
        edit_PhoneNumber = findViewById(R.id.edit_phoneNumber);
        edit_Countries = findViewById(R.id.edit_countries);
        btnAuthentic = findViewById(R.id.btnAuthentic);
        btnVoiceOTP = findViewById(R.id.btnVoiceOTP);
    }


    private void initView() {
        hideKeyboard(edit_Countries);
        btnAuthentic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callIPFlow();
            }
        });
        CountryPicker.Builder builder = new CountryPicker.Builder().with(this)
                .listener(new OnCountryPickerListener() {
                    @Override
                    public void onSelectCountry(Country country) {
                        Log.d(TAG, "country coe = " + country.getDialCode());
                        edit_Countries.setText(country.getDialCode());
//                        edit_PhoneNumber.setText(country.getDialCode());
                    }
                });
        CountryPicker picker = builder.build();
        Country country = picker.getCountryFromSIM();
        edit_Countries.setText(country.getDialCode());
        edit_Countries.setShowSoftInputOnFocus(false);
        edit_Countries.setOnFocusChangeListener((view12, b) -> {
            if (b) {
                picker.showBottomSheet(MainActivity.this);
                hideKeyboard(edit_Countries);
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
        Log.d(TAG, "openFailActivity:");
    }

    private void openSuccessActivity(String phonenumber) {
        Intent intent = new Intent(this, Notify_Success.class);
        intent.putExtra("success", phonenumber);
        startActivity(intent);
        Log.d(TAG, "openSuccessActivity:");
    }
    private void openOTP(String OTP) {
        Intent intent = new Intent(this, CodeOTP.class);
        intent.putExtra("OTP", OTP);
        startActivity(intent);
    }

    private void initIPification() {
        IPConfiguration.getInstance().setCustomUrls(true);
        IPConfiguration.getInstance().setAUTHORIZATION_URL(Uri.parse("https://api.smartbot.vn/stage/auth/start"));
        IPConfiguration.getInstance().setCOVERAGE_URL(Uri.parse("https://api.smartbot.vn/stage/coverage"));
        IPConfiguration.getInstance().setENV(IPEnvironment.SANDBOX);
        IPConfiguration.getInstance().setCLIENT_ID("60e8291b368fbf97f80fd055");
//        IPConfiguration.getInstance().setCLIENT_SETCRET("maitrongjthuaanf");
        IPConfiguration.getInstance().setREDIRECT_URI(Uri.parse("com.example.demo://path/"));

    }

    private void callIPFlow() {
        IPConfiguration.getInstance().setDebug(true);

        // check Coverage
        callCheckCoverage((isAvailable, operatorCode, errorMessage) -> {
            String country_code = edit_Countries.getText().toString();
            String user_input_phone_number = edit_PhoneNumber.getText().toString();
            String phoneNumber = country_code + user_input_phone_number;
            Log.d(TAG, "callIPFlow:  check Coverage +++" + isAvailable);
            if (isAvailable) {
                Log.d(TAG, "callIPFlow:  " + isAvailable.toString());
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
        IPificationCallback callback = new IPificationCallback() {
            @Override
            public void onSuccess(@NonNull AuthResponse authResponse) {
                Log.d(TAG, "onSuccess: AuthResponse" + authResponse.getCode().toString());
                String country_code = edit_Countries.getText().toString();
                String user_input_phone_number = edit_PhoneNumber.getText().toString();
                String phoneNumber = country_code + user_input_phone_number;
                if (authResponse.getCode() != null) {
//                    Log.d(TAG, "onSuccess:  getcode" + authResponse.getCode().toString());
                    callTokenExchange(authResponse.getCode());
                } else {
                    Log.e(TAG, "startAuth - error: code is empty");
                    openFailActivity(phoneNumber);
                }
            }

            @Override
            public void onError(@NonNull IPificationError iPificationError) {

            }

            @Override
            public void onIMCancel() {

            }
        };

        AuthRequest.Builder authRequestBuilder = new AuthRequest.Builder();
        String country_code = edit_Countries.getText().toString();
        String user_input_phone_number = edit_PhoneNumber.getText().toString();
        String phoneNumber = country_code + user_input_phone_number;
        authRequestBuilder.addQueryParam("login_hint", phoneNumber);
        authRequestBuilder.setScope("openid ip:phone_verify");
        IPificationServices.Factory.startAuthentication(this, authRequestBuilder.build(), callback);
        Log.d(TAG, "startAuth: " + country_code + user_input_phone_number);
    }

    private void callCheckCoverage(CoverageCallback coverageCallback) {
        String country_code = edit_Countries.getText().toString();
        String user_input_phone_number = edit_PhoneNumber.getText().toString();
        String phoneNumber = country_code + user_input_phone_number;
        IPificationServices.Factory.startCheckCoverage(phoneNumber,this, new CellularCallback<CoverageResponse>() {
            @Override
            public void onSuccess(CoverageResponse coverageResponse) {
                Log.d(TAG, "onSuccess: CoverageResponse coverageResponse" + coverageResponse);
                if (coverageResponse.isAvailable()) {
                    coverageCallback.result(true, coverageResponse.getOperatorCode(), "");
                    Log.d(TAG, "onSuccess:  true");
                } else {
                    coverageCallback.result(false, coverageResponse.getOperatorCode(), "");
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
                // don't thing
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
                Log.d(TAG, "callTokenExchange: "+ phoneNumber + " "+ phoneNumberVerified);
                if (phoneNumberVerified.equals("true")) {
                    Log.d(TAG, "callTokenExchange: " + phoneNumberVerified);
                    openSuccessActivity(phoneNumber);
                } else {
                    String country_code = edit_Countries.getText().toString();
                    String user_input_phone_number = edit_PhoneNumber.getText().toString();
                    String phoneNumbers = country_code + user_input_phone_number;
                    openFailActivity(phoneNumbers);
                }
            } else {
                String country_code = edit_Countries.getText().toString();
                String user_input_phone_number = edit_PhoneNumber.getText().toString();
                String phoneNumber = country_code + user_input_phone_number;
                openFailActivity(phoneNumber);
            }
        }));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMService.Factory.onActivityResult(requestCode, resultCode, data);
    }

    private void hideKeyboard(EditText countryCodeEditText) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(countryCodeEditText.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// API 5+ solution
            onBackPressed();
            return true;
        } else {
            super.onOptionsItemSelected(item);
        }
        return false;
    }

}