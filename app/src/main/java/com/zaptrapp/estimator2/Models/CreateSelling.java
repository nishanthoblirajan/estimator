package com.zaptrapp.estimator2.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nishanth on 10/12/17.
 */

public class CreateSelling implements Parcelable {
    public static final Creator<CreateSelling> CREATOR = new Creator<CreateSelling>() {
        @Override
        public CreateSelling createFromParcel(Parcel source) {
            return new CreateSelling(source);
        }

        @Override
        public CreateSelling[] newArray(int size) {
            return new CreateSelling[size];
        }
    };
    public String material;
    public String printer;
    public String modelName;
    public String hallmarkOrKDM;
    public double gramRate;
    public double estimateProductGram;
    public double estimateVaPercent;
    public double estimateVaNumber;
    public double extraInput;
    public double sgst;
    public double cgst;

    public CreateSelling() {
    }

    public CreateSelling(String material, String printer, String modelName, String hallmarkOrKDM, double gramRate, double estimateProductGram, double estimateVaPercent, double estimateVaNumber, double extraInput, double sgst, double cgst) {
        this.material = material;
        this.printer = printer;
        this.modelName = modelName;
        this.hallmarkOrKDM = hallmarkOrKDM;
        this.gramRate = gramRate;
        this.estimateProductGram = estimateProductGram;
        this.estimateVaPercent = estimateVaPercent;
        this.estimateVaNumber = estimateVaNumber;
        this.extraInput = extraInput;
        this.sgst = sgst;
        this.cgst = cgst;
    }

    protected CreateSelling(Parcel in) {
        this.material = in.readString();
        this.printer = in.readString();
        this.modelName = in.readString();
        this.hallmarkOrKDM = in.readString();
        this.gramRate = in.readDouble();
        this.estimateProductGram = in.readDouble();
        this.estimateVaPercent = in.readDouble();
        this.estimateVaNumber = in.readDouble();
        this.extraInput = in.readDouble();
        this.sgst = in.readDouble();
        this.cgst = in.readDouble();
    }

    @Override
    public String toString() {
        return "CreateSelling{" +
                "material='" + material + '\'' +
                ", printer='" + printer + '\'' +
                ", modelName='" + modelName + '\'' +
                ", hallmarkOrKDM='" + hallmarkOrKDM + '\'' +
                ", gramRate=" + gramRate +
                ", estimateProductGram=" + estimateProductGram +
                ", estimateVaPercent=" + estimateVaPercent +
                ", estimateVaNumber=" + estimateVaNumber +
                ", extraInput=" + extraInput +
                ", sgst=" + sgst +
                ", cgst=" + cgst +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.material);
        dest.writeString(this.printer);
        dest.writeString(this.modelName);
        dest.writeString(this.hallmarkOrKDM);
        dest.writeDouble(this.gramRate);
        dest.writeDouble(this.estimateProductGram);
        dest.writeDouble(this.estimateVaPercent);
        dest.writeDouble(this.estimateVaNumber);
        dest.writeDouble(this.extraInput);
        dest.writeDouble(this.sgst);
        dest.writeDouble(this.cgst);
    }
}
