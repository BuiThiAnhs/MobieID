package com.ITS.mobieid.service;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ITS.mobieid.data.SMS_OTP;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SMSOTP {
    private final MutableLiveData<List<SMS_OTP>>mSMSOTP = new MutableLiveData<>();

    public LiveData<List<SMS_OTP>> getSMSOTP()
    {
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
        return mSMSOTP;
    }

}
