package com.ITS.mobieid.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.ITS.mobieid.R;


import com.ITS.mobieid.callback.CoverageCallback;
import com.ITS.mobieid.data.SMS_OTP;
import com.ITS.mobieid.manager.APIManager;
import com.ITS.mobieid.service.OTPReceiver;
import com.ITS.mobieid.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ipification.mobile.sdk.android.request.AuthRequest;
import com.ipification.mobile.sdk.android.response.AuthResponse;
import com.its.mobileid.MobileID;
import com.its.mobileid.MobileIDEnv;
import com.its.mobileid.callback.MobileIDCallback;
import com.its.mobileid.error.MobileIDError;
import com.its.mobileid.response.MobileIDAuthResponse;
import com.its.mobileid.response.MobileIDCoverageResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class CodeOTP extends AppCompatActivity {

    private final static String TAG ="CodeOTP";
    EditText editCodeotp;
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_otp);
        initWidget();
        initMobileID();
        addSMSOTP();
        callTokenExchange(initPhoneNumbers());
        initView();
        new OTPReceiver().setEdit_otp(editCodeotp);


    }



    private void initView()
    {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // DOc csdl so sanh xem no co trung voi so dien thoai nhap co tring nhsu khong
            }
        });
    }
    private void  callTokenExchange(String phonenumber){
        SMS_OTP sms_otp = new SMS_OTP();
        APIManager.doPostTokenSMS( phonenumber, ((response, errorMessage) -> {
            if (!response.equals("")) {
                String phoneNumber = Util.parseUserInfoJSON(response, "phone");
                Log.d(TAG,  "phone_number:" + phoneNumber);
                if (phoneNumber == sms_otp.getPhoneNumbers()) {
                    String s = editCodeotp.getText().toString();
                    if (s == sms_otp.getOtp().toString())
                    {
                        openSuccessActivity();
                    }
                } else {
                    String user_input_phone_number = initPhoneNumbers();
                    openFailActivity(user_input_phone_number);
                }
            } else {
                String phoneNumber =initPhoneNumbers();
                openFailActivity(phoneNumber);
            }
        }));
    }
    private void addSMSOTP()
    {
        Random random = new Random();
        int rd = random.nextInt(999999-100000+1) +100000;
        String rdr = String.valueOf(rd).toString();
        Log.d(TAG, "createOTP:  " +rdr);
        SMS_OTP sms_otp = new SMS_OTP();
        sms_otp.setOtp(rdr);
        sms_otp.setPhoneNumbers(initPhoneNumbers());
        Map<String, Object> SMS_OTP = new HashMap<>();
        SMS_OTP.put("phonenumber", sms_otp.getPhoneNumbers());
        SMS_OTP.put("otp", sms_otp.getOtp());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SMS_OTP")
                  .add(SMS_OTP)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Log.d(TAG, "onSuccess: " + documentReference.get());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: ", e);
                    }
                });
    }
    private void initMobileID()
    {
        MobileID.Factory.setEnv(MobileIDEnv.PRODUCTION,"60e8291b368fbf97f80fd055","com.example.demo://path/" );
    }
    public void initWidget()
    {
        btnConfirm = findViewById(R.id.btnConfirm);
        editCodeotp = findViewById(R.id.edit_codeotp);
    }
    public String initPhoneNumbers()
    {
        Intent intent= getIntent();
        if (intent != null) {
            String data = intent.getStringExtra("OTP");
            Log.d(TAG, "initPhoneNumbers:  " + data);
            return  data;
        }
        return null;
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
    //todo: check otp exist
//    private static boolean checkOTP(String otp)
//    {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference otpCollection = db.collection("SMS_OTP");
//        Query query = otpCollection.whereEqualTo("otp", otp);
//        try{
//            QuerySnapshot querySnapshot = query.get().getResult();
//            if (!querySnapshot.isEmpty()) {
//                return true;
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        return false;
//    }
//    private void readOTP()
//    {
//        final MutableLiveData<List<SMS_OTP>> mSMSOTP = new MutableLiveData<>();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("SMS_OTP").addSnapshotListener((value, error) -> {
//            try{
//                if (value.isEmpty()){
//                    List<SMS_OTP> sms_otps = new ArrayList<>();
//                    for (QueryDocumentSnapshot doc: value) {
//                        SMS_OTP sms_otp = doc.toObject(SMS_OTP.class);
//                        sms_otps.add(sms_otp);
//                    }
//                    mSMSOTP.postValue(sms_otps);
//                }
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        });
//    }
}