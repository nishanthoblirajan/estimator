package com.zaptrapp.estimator2.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class CreateEstimate implements Parcelable {
    public String material;
    public String printer;
    public double gramRate;
    public double estimateProductGram;
    public double estimateVaPercent;
    public double estimateVaNumber;
    public double sgst;
    public double cgst;
    public boolean buyingItem;

    public double estimateBuyingPrice;
    public double estimateBuyingGrossWeight;
    public double estimateBuyingNetWeight;



    public CreateEstimate(String material, String printer, double gramRate, double estimateProductGram, double estimateVaPercent, double estimateVaNumber, double sgst, double cgst, boolean buyingItem, double estimateBuyingPrice, double estimateBuyingGrossWeight, double estimateBuyingNetWeight) {
        this.material = material;
        this.printer = printer;
        this.gramRate = gramRate;
        this.estimateProductGram = estimateProductGram;
        this.estimateVaPercent = estimateVaPercent;
        this.estimateVaNumber = estimateVaNumber;
        this.sgst = sgst;
        this.cgst = cgst;
        this.buyingItem = buyingItem;
        this.estimateBuyingPrice = estimateBuyingPrice;
        this.estimateBuyingGrossWeight = estimateBuyingGrossWeight;
        this.estimateBuyingNetWeight = estimateBuyingNetWeight;
    }

    public double getEstimateBuyingPrice() {
        return estimateBuyingPrice;
    }

    public void setEstimateBuyingPrice(double estimateBuyingPrice) {
        this.estimateBuyingPrice = estimateBuyingPrice;
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

    @Override
    public String toString() {
        return "CreateEstimate{" +
                "material='" + material + '\'' +
                ", printer='" + printer + '\'' +
                ", gramRate=" + gramRate +
                ", estimateProductGram=" + estimateProductGram +
                ", estimateVaPercent=" + estimateVaPercent +
                ", estimateVaNumber=" + estimateVaNumber +
                ", sgst=" + sgst +
                ", cgst=" + cgst +
                ", buyingItem=" + buyingItem +
                '}';
    }

    public CreateEstimate(String material, String printer, double gramRate, double estimateProductGram, double estimateVaPercent, double estimateVaNumber, double sgst, double cgst, boolean buyingItem) {
        this.material = material;
        this.printer = printer;
        this.gramRate = gramRate;
        this.estimateProductGram = estimateProductGram;
        this.estimateVaPercent = estimateVaPercent;
        this.estimateVaNumber = estimateVaNumber;
        this.sgst = sgst;
        this.cgst = cgst;
        this.buyingItem = buyingItem;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    public double getGramRate() {
        return gramRate;
    }

    public void setGramRate(double gramRate) {
        this.gramRate = gramRate;
    }

    public double getEstimateProductGram() {
        return estimateProductGram;
    }

    public void setEstimateProductGram(double estimateProductGram) {
        this.estimateProductGram = estimateProductGram;
    }

    public double getEstimateVaPercent() {
        return estimateVaPercent;
    }

    public void setEstimateVaPercent(double estimateVaPercent) {
        this.estimateVaPercent = estimateVaPercent;
    }

    public double getEstimateVaNumber() {
        return estimateVaNumber;
    }

    public void setEstimateVaNumber(double estimateVaNumber) {
        this.estimateVaNumber = estimateVaNumber;
    }

    public double getSgst() {
        return sgst;
    }

    public void setSgst(double sgst) {
        this.sgst = sgst;
    }

    public double getCgst() {
        return cgst;
    }

    public void setCgst(double cgst) {
        this.cgst = cgst;
    }

    public boolean isBuyingItem() {
        return buyingItem;
    }

    public void setBuyingItem(boolean buyingItem) {
        this.buyingItem = buyingItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.material);
        dest.writeString(this.printer);
        dest.writeDouble(this.gramRate);
        dest.writeDouble(this.estimateProductGram);
        dest.writeDouble(this.estimateVaPercent);
        dest.writeDouble(this.estimateVaNumber);
        dest.writeDouble(this.sgst);
        dest.writeDouble(this.cgst);
        dest.writeByte(this.buyingItem ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.estimateBuyingPrice);
        dest.writeDouble(this.estimateBuyingGrossWeight);
        dest.writeDouble(this.estimateBuyingNetWeight);
    }

    protected CreateEstimate(Parcel in) {
        this.material = in.readString();
        this.printer = in.readString();
        this.gramRate = in.readDouble();
        this.estimateProductGram = in.readDouble();
        this.estimateVaPercent = in.readDouble();
        this.estimateVaNumber = in.readDouble();
        this.sgst = in.readDouble();
        this.cgst = in.readDouble();
        this.buyingItem = in.readByte() != 0;
        this.estimateBuyingPrice = in.readDouble();
        this.estimateBuyingGrossWeight = in.readDouble();
        this.estimateBuyingNetWeight = in.readDouble();
    }

    public static final Parcelable.Creator<CreateEstimate> CREATOR = new Parcelable.Creator<CreateEstimate>() {
        @Override
        public CreateEstimate createFromParcel(Parcel source) {
            return new CreateEstimate(source);
        }

        @Override
        public CreateEstimate[] newArray(int size) {
            return new CreateEstimate[size];
        }
    };
}
