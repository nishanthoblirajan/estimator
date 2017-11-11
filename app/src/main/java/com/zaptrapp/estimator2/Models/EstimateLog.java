package com.zaptrapp.estimator2.Models;

/**
 * Created by nishanth on 10/11/17.
 */

public class EstimateLog {
    @Override
    public String toString() {
        return "EstimateLog{" +
                "timeStamp='" + timeStamp + '\'' +
                ", estimate='" + estimate + '\'' +
                '}';
    }

    String timeStamp;

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

    String estimate;

    public EstimateLog() {
    }

    public String getEstimate() {
        return estimate;
    }

    public void setEstimate(String estimate) {
        this.estimate = estimate;
    }
}
