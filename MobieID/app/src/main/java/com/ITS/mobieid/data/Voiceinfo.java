package com.ITS.mobieid.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Voiceinfo implements Parcelable {
    private String Otp;
    private String Calling; //its
    private String Called; // user

    public Voiceinfo(String otp, String calling, String called) {
        Otp = otp;
        Calling = calling;
        Called = called;
    }

    protected Voiceinfo(Parcel in) {
        Otp = in.readString();
        Calling = in.readString();
        Called = in.readString();
    }

    public static final Creator<Voiceinfo> CREATOR = new Creator<Voiceinfo>() {
        @Override
        public Voiceinfo createFromParcel(Parcel in) {
            return new Voiceinfo(in);
        }

        @Override
        public Voiceinfo[] newArray(int size) {
            return new Voiceinfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(Otp);
        dest.writeString(Calling);
        dest.writeString(Called);
    }
}
