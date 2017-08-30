package com.infoskaters.nservicesprovider.utilities;

import com.infoskaters.nservicesprovider.models.TodaysJobModel;

/**
 * Created by madhu on 28/6/17.
 */

public class NServicesSingleton {
    private static final NServicesSingleton instance = new NServicesSingleton();
    private String spId;
    private String token;
    private TodaysJobModel selectedJobModel;
    private Double mycurrentLatitude;
    private Double mycurrentLongitude;

    public void setMycurrentLatitude(Double mycurrentLatitude) {
        this.mycurrentLatitude = mycurrentLatitude;
    }

    public void setMycurrentLongitude(Double mycurrentLongitude) {
        this.mycurrentLongitude = mycurrentLongitude;
    }

    public Double getConsumerLatitude() {
        return consumerLatitude;
    }

    public void setConsumerLatitude(Double consumerLatitude) {
        this.consumerLatitude = consumerLatitude;
    }

    public Double getConsumerLongitude() {
        return consumerLongitude;
    }

    public void setConsumerLongitude(Double consumerLongitude) {
        this.consumerLongitude = consumerLongitude;
    }

    private Double consumerLatitude;
    private Double consumerLongitude;

    public Double getMycurrentLatitude() {
        return mycurrentLatitude;
    }

    public Double getMycurrentLongitude() {
        return mycurrentLongitude;
    }

    public TodaysJobModel getSelectedJobModel() {
        return selectedJobModel;
    }

    public void setSelectedJobModel(TodaysJobModel selectedJobModel) {
        this.selectedJobModel = selectedJobModel;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static NServicesSingleton getInstance() {
        return instance;
    }

  

    public void setMycurrentLongitude(double mycurrentLongitude) {
        this.mycurrentLongitude = mycurrentLongitude;
    }

    public void setMycurrentLatitude(double mycurrentLatitude) {
        this.mycurrentLatitude = mycurrentLatitude;
    }
}
