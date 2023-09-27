package com.ITS.mobieid.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TokenInfo implements Parcelable {
    public boolean phoneNumberVerified;
    public String phoneNumber;
    public String mobileID;
    public String voidOTP;
    public String loginHint;
    public String sub;

    public TokenInfo(boolean phoneNumberVerified, String phoneNumber, String mobileID, String voidOTP, String loginHint, String sub) {
        this.phoneNumberVerified = phoneNumberVerified;
        this.phoneNumber = phoneNumber;
        this.mobileID = mobileID;
        this.voidOTP = voidOTP;
        this.loginHint = loginHint;
        this.sub = sub;
    }

    protected TokenInfo(Parcel in) {
        phoneNumberVerified = in.readByte() != 0;
        phoneNumber = in.readString();
        mobileID = in.readString();
        voidOTP = in.readString();
        loginHint = in.readString();
        sub = in.readString();
    }

    public static final Creator<TokenInfo> CREATOR = new Creator<TokenInfo>() {
        @Override
        public TokenInfo createFromParcel(Parcel in) {
            return new TokenInfo(in);
        }

        @Override
        public TokenInfo[] newArray(int size) {
            return new TokenInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (phoneNumberVerified ? 1 : 0));
        dest.writeString(phoneNumber);
        dest.writeString(mobileID);
        dest.writeString(voidOTP);
        dest.writeString(loginHint);
        dest.writeString(sub);
    }
}
