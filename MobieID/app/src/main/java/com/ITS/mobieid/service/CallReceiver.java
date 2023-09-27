package com.ITS.mobieid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
    private final static String TAG ="Call Receiver ";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
        {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(TAG, "onReceive: "+ state);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state) ) {
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            }
        }
    }
}
