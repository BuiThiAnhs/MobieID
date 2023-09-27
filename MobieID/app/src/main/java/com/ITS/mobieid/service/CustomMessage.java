package com.ITS.mobieid.service;

import android.util.Log;

import androidx.annotation.NonNull;


import com.ITS.mobieid.R;
import com.ITS.mobieid.manager.APIManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ipification.mobile.sdk.im.IMService;

public class CustomMessage extends FirebaseMessagingService {
    private final static String TAG ="Custom FBM";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }
    private  void sendRegistrationToServer(String token){
        APIManager.currentToken = token;
    }
    private void sendNotification(String messageBody) {
        // TODO check if notification is from IPification Service
//        if(data["source"] == "ipification"){
        IMService.Factory.showIPNotification(this, getString(R.string.app_name), messageBody, R.mipmap.ic_launcher);
//        }
    }
}
