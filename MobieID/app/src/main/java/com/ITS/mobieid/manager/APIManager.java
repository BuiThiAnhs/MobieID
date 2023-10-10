package com.ITS.mobieid.manager;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;


import com.ITS.mobieid.Constant;
import com.ITS.mobieid.callback.OTPCallback;
import com.ITS.mobieid.callback.TokenCallback;
import com.ITS.mobieid.util.Util;
import com.ipification.mobile.sdk.android.IPConfiguration;
import com.its.mobileid.MobileID;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIManager {
    private final static String TAG = "Phone Verifi";
    private final static String currentState = null;
    public static String currentToken = null;

    public static void doPostToken(String code, final TokenCallback callback) {
        try {
            String url = Constant.ACCESS_TOKEN_URL;
            RequestBody body = new FormBody.Builder()
                    .add("api_key", "maitrongjthuaanf")
                    .build();
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url(url).post(body).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.result("", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful( ) && response.body() != null) {
                        String accessToken = Util.parseAccessTokenFromJSON(response.body().string());
                        Log.e(TAG, "onResponse: accessToken: "+ accessToken );
                        if (!accessToken.equals("")) {
                            doVerify(accessToken, code, callback);


                        } else {
                            callback.result("", response.body().string());
                        }
                    } else {
                        try {
                            if (response.body() != null) {
                                callback.result("", response.body().string());
                            } else {
                                callback.result("", "error body is null" + response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "token exchange error: " + e.getMessage());
            e.printStackTrace();
            callback.result("", e.getMessage());
        }
    }


    public static void doVerify(String access_token, String code, final TokenCallback callback) {
        try {
            String url = Constant.USER_INFO_URL;
            RequestBody body = new FormBody.Builder()
                    .add("code", code)
                    .build();

            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url(url).post(body).addHeader("Authorization", "Bearer " + access_token)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string(); // Đọc dữ liệu từ Response
                        callback.result(responseBody, "");
                        Log.d(TAG, "onResponse: " +responseBody);
                    } else {
                        Log.d(TAG, "onResponse: " + response.body().string());
                        try {
                            if (response.body() != null) {
                                String errorBody = response.body().string(); // Đọc dữ liệu từ Response khi xảy ra lỗi
                                callback.result("", errorBody);

                            } else {
                                callback.result("", "error body is null " + response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    callback.result("", e.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callback.result("", e.getMessage());

        }
    }


    public static void doPostTokenSMS(String phonenumber, final TokenCallback callback) {
        try {
            String url = Constant.ACCESS_TOKEN_URL;
            RequestBody body = new FormBody.Builder()
                    .add("api_key", "maitrongjthuaanf")
                    .build();
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url(url).post(body).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.result("", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful( ) && response.body() != null) {
                        String accessToken = Util.parseAccessTokenFromJSON(response.body().string());
                        Log.d(TAG, "onResponse: accessToken ========: "+ accessToken );
                        if (!accessToken.equals("")) {
                            doSMSOTP(accessToken, phonenumber, callback);


                        } else {
                            callback.result("", response.body().string());
                        }
                    } else {
                        try {
                            if (response.body() != null) {
                                callback.result("", response.body().string());
                            } else {
                                callback.result("", "error body is null" + response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "token exchange error: " + e.getMessage());
            e.printStackTrace();
            callback.result("", e.getMessage());
        }
    }


    public static void doSMSOTP(String access_token, String phonenumber, final TokenCallback callback) {
        try {
            MediaType mediaType = MediaType.parse("application/json");
            String url = Constant.USER_SMS_OTP;
//            chuần hóa đầu vào
            String sms = "Mã OTP của bạn là: " + phonenumber;
            // mã hóa sms về base64
            String smsText = Base64.getEncoder().encodeToString(sms.getBytes(StandardCharsets.UTF_8));
            String sessionId = "5c22be0c03245526";
            String phone = phonenumber;
            String requestId = "tranID-Core01-9876545355";
// Tạo một chuỗi JSON với các biến
            String json = "{\r\n    \"session_id\":\"" + sessionId + "\",\r\n    \"phone\":\"" + phone + "\",\r\n    \"message\":\"" + smsText + "\",\r\n    \"requestId\":\"" + requestId + "\"\r\n}";

// Tạo RequestBody từ chuỗi JSON
            RequestBody body = RequestBody.create(mediaType, json);

            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url(url).post(body).addHeader("Content-Type", "application/json").addHeader("Authorization", "Bearer " + access_token)
                    .build();
            Call call = client.newCall(request);
            Log.d(TAG, "doSMSOTP ===: " + json);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string(); // Đọc dữ liệu từ Response
                        callback.result(responseBody, "");
                        Log.d(TAG, "onResponses: " +responseBody);
                    } else {
                        Log.d(TAG, "onResponse: " + response.body());
                        try {
                            if (response.body() != null) {
                                String errorBody = response.body().string(); // Đọc dữ liệu từ Response khi xảy ra lỗi
                                callback.result("", errorBody);

                            } else {
                                callback.result("", "error body is null " + response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    callback.result("", e.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callback.result("", e.getMessage());

        }
    }

}
