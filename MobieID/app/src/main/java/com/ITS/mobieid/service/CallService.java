package com.ITS.mobieid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

public class CallService extends Service {
    private static final String TAG = "CallService";
    private CallReceiver callReceiver;
    private TelephonyManager telephonyManager;
    private PhoneStateListener callStateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service started");
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        callStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                // Xử lý trạng thái cuộc gọi ở đây
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(TAG, "onCallStateChanged: ");
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        // Đang trong cuộc gọi
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Cuộc gọi kết thúc
                        break;
                }
            }
        };
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service stopped");
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
