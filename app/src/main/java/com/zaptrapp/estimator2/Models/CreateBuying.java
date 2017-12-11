package com.zaptrapp.estimator2.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nishanth on 10/12/17.
 */

public class CreateBuying implements Parcelable {
    public boolean buyingItem;
    public double extraInput;
    public double estimateBuyingPrice;
    public String estimateBuyingDesc;
    public double estimateBuyingGrossWeight;
    public double estimateBuyingNetWeight;

    @Override
    public String toString() {
        return "CreateBuying{" +
                "buyingItem=" + buyingItem +
                ", extraInput=" + extraInput +
                ", estimateBuyingPrice=" + estimateBuyingPrice +
                ", estimateBuyingDesc='" + estimateBuyingDesc + '\'' +
                ", estimateBuyingGrossWeight=" + estimateBuyingGrossWeight +
                ", estimateBuyingNetWeight=" + estimateBuyingNetWeight +
                '}';
    }

    public boolean isBuyingItem() {
        return buyingItem;
    }

    public void setBuyingItem(boolean buyingItem) {
        this.buyingItem = buyingItem;
    }

    public double getExtraInput() {
        return extraInput;
    }

    public void setExtraInput(double extraInput) {
        this.extraInput = extraInput;
    }

    public double getEstimateBuyingPrice() {
        return estimateBuyingPrice;
    }

    public void setEstimateBuyingPrice(double estimateBuyingPrice) {
        this.estimateBuyingPrice = estimateBuyingPrice;
    }

    public String getEstimateBuyingDesc() {
        return estimateBuyingDesc;
    }

    public void setEstimateBuyingDesc(String estimateBuyingDesc) {
        this.estimateBuyingDesc = estimateBuyingDesc;
    }

    public double getEstimateBuyingGrossWeight() {
        return estimateBuyingGrossWeight;
    }

    public void setEstimateBuyingGrossWeight(double estimateBuyingGrossWeight) {
        this.estimateBuyingGrossWeight = estimateBuyingGrossWeight;
    }

    public double getEstimateBuyingNetWeight() {
        return estimateBuyingNetWeight;
    }

    public void setEstimateBuyingNetWeight(double estimateBuyingNetWeight) {
        this.estimateBuyingNetWeight = estimateBuyingNetWeight;
    }

    public CreateBuying() {
    }

    public CreateBuying(boolean buyingItem, double extraInput, double estimateBuyingPrice, String estimateBuyingDesc, double estimateBuyingGrossWeight, double estimateBuyingNetWeight) {
        this.buyingItem = buyingItem;
        this.extraInput = extraInput;
        this.estimateBuyingPrice = estimateBuyingPrice;
        this.estimateBuyingDesc = estimateBuyingDesc;
        this.estimateBuyingGrossWeight = estimateBuyingGrossWeight;
        this.estimateBuyingNetWeight = estimateBuyingNetWeight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.buyingItem ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.extraInput);
        dest.writeDouble(this.estimateBuyingPrice);
        dest.writeString(this.estimateBuyingDesc);
        dest.writeDouble(this.estimateBuyingGrossWeight);
        dest.writeDouble(this.estimateBuyingNetWeight);
    }

    protected CreateBuying(Parcel in) {
        this.buyingItem = in.readByte() != 0;
        this.extraInput = in.readDouble();
        this.estimateBuyingPrice = in.readDouble();
        this.estimateBuyingDesc = in.readString();
        this.estimateBuyingGrossWeight = in.readDouble();
        this.estimateBuyingNetWeight = in.readDouble();
    }

    public static final Parcelable.Creator<CreateBuying> CREATOR = new Parcelable.Creator<CreateBuying>() {
        @Override
        public CreateBuying createFromParcel(Parcel source) {
            return new CreateBuying(source);
        }

        @Override
        public CreateBuying[] newArray(int size) {
            return new CreateBuying[size];
        }
    };
}
