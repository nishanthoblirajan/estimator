package com.zaptrapp.estimator2.Models;

import android.os.Parcel;
import android.os.Parcelable;



//Not used yet
public class Rate implements Parcelable {
    public static final Parcelable.Creator<Rate> CREATOR = new Parcelable.Creator<Rate>() {
        @Override
        public Rate createFromParcel(Parcel source) {
            return new Rate(source);
        }

        @Override
        public Rate[] newArray(int size) {
            return new Rate[size];
        }
    };
    double goldRate;
    double silverRate;

    public Rate(double goldRate, double silverRate) {
        this.goldRate = goldRate;
        this.silverRate = silverRate;
    }

    protected Rate(Parcel in) {
        this.goldRate = in.readDouble();
        this.silverRate = in.readDouble();
    }

    public double getGoldRate() {
        return goldRate;
    }

    public void setGoldRate(double goldRate) {
        this.goldRate = goldRate;
    }

    public double getSilverRate() {
        return silverRate;
    }

    public void setSilverRate(double silverRate) {
        this.silverRate = silverRate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.goldRate);
        dest.writeDouble(this.silverRate);
    }
}
