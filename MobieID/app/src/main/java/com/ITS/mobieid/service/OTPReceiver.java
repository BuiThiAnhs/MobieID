package com.ITS.mobieid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;

public class OTPReceiver extends BroadcastReceiver {
    private final static String TAG ="Call Receiver ";

    private static EditText edit_otp;
    public void setEdit_otp(EditText edit_otp) {
        OTPReceiver.edit_otp = edit_otp;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsManagers = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage smsMessage: smsManagers  ) {
            String message_body = smsMessage.getMessageBody();
            String getOTP = message_body.split(":")[1];
            edit_otp.setText(getOTP);
        }

    }
}
