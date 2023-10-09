package com.ITS.mobieid.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.ITS.mobieid.R;


import com.ITS.mobieid.data.SMS_OTP;
import com.ITS.mobieid.manager.APIManager;
import com.ITS.mobieid.service.OTPReceiver;
import com.ITS.mobieid.util.Util;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.its.mobileid.MobileID;
import com.its.mobileid.MobileIDEnv;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CodeOTP extends AppCompatActivity {

    private final static String TAG ="CodeOTP";
    private String data ="";
    EditText editCodeotp;
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_otp);
        initWidget();
        initMobileID();
        initView();
        addSMSOTP();
        new OTPReceiver().setEdit_otp(editCodeotp);


    }



    private void initView()
    {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void addSMSOTP()
    {
        initPhoneNumbers();
        Random random = new Random();
        int rd = random.nextInt(999999-100000+1) +100000;
        String rdr = String.valueOf(rd).toString();
        Log.d(TAG, "createOTP:  " +rdr);
        SMS_OTP sms_otp = new SMS_OTP();
        sms_otp.setOtpSms(rdr);
        sms_otp.setPhoneNumbers(data);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SMS_OTP")
                .add(sms_otp)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "createOTP: " + rdr );
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "createOTP; " +e.getMessage());
                });
    }
    //todo: check otp exist
    private static boolean checkOTP(String otp)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference otpCollection = db.collection("SMS_OTP");
        Query query = otpCollection.whereEqualTo("otp", otp);
        try{
            QuerySnapshot querySnapshot = query.get().getResult();
            if (!querySnapshot.isEmpty()) {
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private void checksData() {
        SMS_OTP smsOTP = new SMS_OTP();
    }
    private void readOTP()
    {
        final MutableLiveData<List<SMS_OTP>> mSMSOTP = new MutableLiveData<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SMS_OTP").addSnapshotListener((value, error) -> {
            try{
                if (value.isEmpty()){
                    List<SMS_OTP> sms_otps = new ArrayList<>();
                    for (QueryDocumentSnapshot doc: value) {
                        SMS_OTP sms_otp = doc.toObject(SMS_OTP.class);
                        sms_otps.add(sms_otp);
                    }
                    mSMSOTP.postValue(sms_otps);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
    private void initMobileID()
    {
        MobileID.Factory.setEnv(MobileIDEnv.PRODUCTION,"60e8291b368fbf97f80fd055","com.example.demo://path/" );
        Log.d(TAG, "initMobileID:  started");
    }
    public void initWidget()
    {
        btnConfirm = findViewById(R.id.btnConfirm);
        editCodeotp = findViewById(R.id.edit_codeotp);
    }
    public void initPhoneNumbers()
    {

        Intent intent= getIntent();
        if (intent != null) {
            data = intent.getStringExtra("sms");

        }
    }
    private void openSuccessActivity() {
        Intent intent = new Intent(this, Notify_Success.class);
        startActivity(intent);

    }

    private void openFailActivity(String phoneNumber) {
        Intent intent = new Intent(this, Notify_Fails.class);
        intent.putExtra("phonenumber", phoneNumber);
        startActivity(intent);
    }

}