package com.zaptrapp.estimator2.Models;

/**
 * Created by Nishanth on 10-Nov-17.
 */

public class EstimateLog {
    String timeStamp;
    String estimate;

    public EstimateLog() {
    }

    public EstimateLog(String timeStamp, String estimate) {
        this.timeStamp = timeStamp;
        this.estimate = estimate;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getEstimate() {
        return estimate;
    }

    public void setEstimate(String estimate) {
        this.estimate = estimate;
    }
}
