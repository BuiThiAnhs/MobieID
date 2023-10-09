package com.ITS.mobieid.data;

public class SMS_OTP {
    private String PhoneNumbers;
    private String OtpSms;

    public SMS_OTP() {
    }

    public SMS_OTP(String phoneNumbers, String otpSms) {
        PhoneNumbers = phoneNumbers;
        OtpSms = otpSms;
    }

    public String getPhoneNumbers() {
        return PhoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        PhoneNumbers = phoneNumbers;
    }

    public String getOtpSms() {
        return OtpSms;
    }

    public void setOtpSms(String otpSms) {
        OtpSms = otpSms;
    }

}
