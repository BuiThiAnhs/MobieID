package com.ITS.mobieid.util;

import com.ITS.mobieid.data.TokenInfo;
import com.auth0.android.jwt.JWT;


import org.json.JSONException;
import org.json.JSONObject;

public class Util {
    public static TokenInfo parseAccessToken(String accessToken) {
        if (accessToken == null || accessToken.equals("")) {
            return null;
        }
        JWT jwt = null;
        String error = null;
        try {
            jwt = new JWT(accessToken);
        } catch (Exception e) {
            error = e.getLocalizedMessage();
        }
        if (error != null && !error.equals("") || jwt == null) {
            return null;
        } else {
            String phoneVerify = jwt.getClaim("phone_number_verified").asString();
            String phoneNumber = jwt.getClaim("phone_number").asString();
            String loginHint = jwt.getClaim("login_hint").asString();
            String sub = jwt.getClaim("sub").asString();
            String mobileID = jwt.getClaim("mobile_id").asString();
            String voiceOTP = jwt.getClaim("voice_OTP").asString();
            return new TokenInfo(
                    phoneNumber == null || phoneVerify.equals("true"),
                    phoneNumber,
                    mobileID,
                    voiceOTP,
                    loginHint,
                    sub);
        }
    }

    public static String parseAccessTokenFromJSON(String jsonStr) {
        try {
            JSONObject jObject = new JSONObject(jsonStr);
            return jObject.getString("access_token");
        } catch (JSONException e) {
            return "";
        }
    }

    public static String parseUserInfoJSON(String jsonStr, String pattern) {
        try {
            JSONObject jObject = new JSONObject(jsonStr);
            return jObject.getString(pattern);
        } catch (Exception e) {
            return "";
        }
    }
}
