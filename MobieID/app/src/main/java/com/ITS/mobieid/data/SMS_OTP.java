package com.ITS.mobieid.data;

public class SMS_OTP {
    private String phonenumber;
    private String otp;

    public SMS_OTP() {
    }

    public SMS_OTP(String phoneNumbers, String otpSms) {
        phonenumber = phoneNumbers;
        otp = otpSms;
    }

    public String getPhoneNumbers() {
        return phonenumber;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        phonenumber = phoneNumbers;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}
