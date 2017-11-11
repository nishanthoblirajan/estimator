package com.zaptrapp.estimator2.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nishanth on 02/11/17.
 */

public class Product implements Parcelable {
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    String productName;
    double lessThanOne;
    double one;
    double two;
    double three;
    double four;
    double five;
    double six;
    double greaterThanSix;

    public Product() {
    }

    public Product(String productName) {
        this.productName = productName;
    }

    public Product(String productName, double lessThanOne, double one, double two, double three, double four, double five, double six, double greaterThanSix) {
        this.productName = productName;
        this.lessThanOne = lessThanOne;
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
        this.six = six;
        this.greaterThanSix = greaterThanSix;
    }

    protected Product(Parcel in) {
        this.productName = in.readString();
        this.lessThanOne = in.readDouble();
        this.one = in.readDouble();
        this.two = in.readDouble();
        this.three = in.readDouble();
        this.four = in.readDouble();
        this.five = in.readDouble();
        this.six = in.readDouble();
        this.greaterThanSix = in.readDouble();
    }

    @Override
    public String toString() {
        return "Product{" +
                "productName='" + productName + '\'' +
                ", lessThanOne=" + lessThanOne +
                ", one=" + one +
                ", two=" + two +
                ", three=" + three +
                ", four=" + four +
                ", five=" + five +
                ", six=" + six +
                ", greaterThanSix=" + greaterThanSix +
                '}';
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getLessThanOne() {
        return lessThanOne;
    }

    public void setLessThanOne(double lessThanOne) {
        this.lessThanOne = lessThanOne;
    }

    public double getOne() {
        return one;
    }

    public void setOne(double one) {
        this.one = one;
    }

    public double getTwo() {
        return two;
    }

    public void setTwo(double two) {
        this.two = two;
    }

    public double getThree() {
        return three;
    }

    public void setThree(double three) {
        this.three = three;
    }

    public double getFour() {
        return four;
    }

    public void setFour(double four) {
        this.four = four;
    }

    public double getFive() {
        return five;
    }

    public void setFive(double five) {
        this.five = five;
    }

    public double getSix() {
        return six;
    }

    public void setSix(double six) {
        this.six = six;
    }

    public double getGreaterThanSix() {
        return greaterThanSix;
    }

    public void setGreaterThanSix(double greaterThanSix) {
        this.greaterThanSix = greaterThanSix;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productName);
        dest.writeDouble(this.lessThanOne);
        dest.writeDouble(this.one);
        dest.writeDouble(this.two);
        dest.writeDouble(this.three);
        dest.writeDouble(this.four);
        dest.writeDouble(this.five);
        dest.writeDouble(this.six);
        dest.writeDouble(this.greaterThanSix);
    }
}
