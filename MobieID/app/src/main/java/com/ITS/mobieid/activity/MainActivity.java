package com.ITS.mobieid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.ITS.mobieid.R;
import com.ITS.mobieid.callback.CoverageCallback;
import com.ITS.mobieid.manager.APIManager;
import com.ITS.mobieid.util.Util;
import com.ipification.mobile.sdk.android.IPConfiguration;
import com.ipification.mobile.sdk.android.request.AuthRequest;
import com.ipification.mobile.sdk.im.IMService;
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
                openOTP(edit_PhoneNumber.getText().toString());
            }
        });
    }

    void initWidget() {
        imageView = findViewById(R.id.imageView);
        edit_PhoneNumber = findViewById(R.id.edit_phoneNumber);
//        edit_Countries = findViewById(R.id.edit_countries);
        btnAuthentic = findViewById(R.id.btnAuthentic);
        btnVoiceOTP = findViewById(R.id.btnVoiceOTP);
        progressBar =  findViewById(R.id.progress_circular);
    }


    private void initView() {
//        hideKeyboard(edit_Countries);
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
//        CountryPicker.Builder builder = new CountryPicker.Builder().with(this)
//                .listener(new OnCountryPickerListener() {
//                    @Override
//                    public void onSelectCountry(Country country) {
//                        Log.d(TAG, "country coe = " + country.getDialCode());
//                        edit_Countries.setText(country.getDialCode());
////                        edit_PhoneNumber.setText(country.getDialCode());
//                    }
//                });
//        CountryPicker picker = builder.build();
//        Country country = picker.getCountryFromSIM();
//        edit_Countries.setText(country.getDialCode());
//        edit_Countries.setShowSoftInputOnFocus(false);
//        edit_Countries.setOnFocusChangeListener((view12, b) -> {
//            if (b) {
//                picker.showBottomSheet(MainActivity.this);
//                hideKeyboard(edit_Countries);
//            }
//        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void openFailActivity(String error) {
        Intent intent = new Intent(this, Notify_Fails.class);
        intent.putExtra("error", error);
        startActivity(intent);
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

    private void initMobileID() {
        MobileID.Factory.setEnv(MobileIDEnv.PRODUCTION,"60e8291b368fbf97f80fd055","com.example.demo://path/" );

//        IPConfiguration.getInstance().setCustomUrls(true);
//        IPConfiguration.getInstance().setAUTHORIZATION_URL(Uri.parse("https://api.smartbot.vn/stage/auth/start"));
//        IPConfiguration.getInstance().setCOVERAGE_URL(Uri.parse("https://api.smartbot.vn/stage/coverage"));
//        IPConfiguration.getInstance().setENV(IPEnvironment.SANDBOX);
//        IPConfiguration.getInstance().setCLIENT_ID("60e8291b368fbf97f80fd055");
////        IPConfiguration.getInstance().setCLIENT_SETCRET("maitrongjthuaanf");
//        IPConfiguration.getInstance().setREDIRECT_URI(Uri.parse("com.example.demo://path/"));

    }

    private void callIPFlow() {
        IPConfiguration.getInstance().setDebug(true);

        // check Coverage
        callCheckCoverage((isAvailable, operatorCode, errorMessage) -> {
//            String country_code = edit_Countries.getText().toString();
            String phoneNumber = edit_PhoneNumber.getText().toString();
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
        String user_input_phone_number = edit_PhoneNumber.getText().toString();
        String phoneNumbers ="";
        if (user_input_phone_number.startsWith("0")) {
            phoneNumbers = "+84" + user_input_phone_number.substring(1);
        }
          MobileIDCallback callback = new MobileIDCallback<MobileIDAuthResponse>() {
              @Override
              public void onSuccess(MobileIDAuthResponse response) {
                  Log.d(TAG, "onSuccess:  getcode " + response.getCode().toString());
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
//        String country_code = edit_Countries.getText().toString();
        authRequestBuilder.addQueryParam("login_hint", phoneNumbers);
        authRequestBuilder.setScope("openid ip:phone_verify");
        MobileID.Factory.startAuthenticate(MainActivity.this, phoneNumbers, callback);
        Log.d(TAG, "startAuths: " + phoneNumbers);
    }

    private void callCheckCoverage(CoverageCallback coverageCallback) {
//        String country_code = edit_Countries.getText().toString();
        String user_input_phone_number = edit_PhoneNumber.getText().toString();
        String phoneNumbers =null;
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
           public void onSuccess(MobileIDCoverageResponse response) {
               Log.d(TAG, "onSuccess: CoverageResponse coverageResponse" + response);
               if (response.isAvailable()) {
                   coverageCallback.result(true, response.getOperatorCode(), "");
                   Log.d(TAG, "onSuccess:  true"+response.getOperatorCode().toString());
               } else {
                   coverageCallback.result(false, response.getOperatorCode(), "");
                   Log.e(TAG, "CheckCoverage Failed: Not supported");
               }
           }

        });
        Log.d(TAG, "callCheckCoverage: ");
    }
//0382854418
    private void callTokenExchange(String code) {
        APIManager.doPostToken(code, ((response, errorMessage) -> {
            if (!response.equals("")) {
                String phoneNumberVerified = Util.parseUserInfoJSON(response, "phone_number_verified");
                String phoneNumber = Util.parseUserInfoJSON(response, "login_hint");
                Log.e(TAG, "phoneNumberVerified :" + phoneNumberVerified + "phone_number:" + phoneNumber);
                if (phoneNumberVerified.equals("true")) {
                    Log.d(TAG, "callTokenExchange: " + phoneNumberVerified);
                    openSuccessActivity(phoneNumber);
                } else {
//                    String country_code = edit_Countries.getText().toString();
                    String user_input_phone_number = edit_PhoneNumber.getText().toString();
                    openFailActivity(user_input_phone_number);
                }
            } else {
//                String country_code = edit_Countries.getText().toString();
                String phoneNumber = edit_PhoneNumber.getText().toString();
                openFailActivity(phoneNumber);

                
            }
        }));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMService.Factory.onActivityResult(requestCode, resultCode, data);
    }

//    private void hideKeyboard(EditText countryCodeEditText) {
//        InputMethodManager inputMethodManager =
//                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(countryCodeEditText.getWindowToken(), 0);
//    }

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