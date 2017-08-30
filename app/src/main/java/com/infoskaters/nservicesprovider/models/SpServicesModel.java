package com.infoskaters.nservicesprovider.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by madhu on 27/07/17.
 */

public class SpServicesModel {
    @SerializedName("service_id")
    @Expose
    private String serviceId;
    @SerializedName("service_name")
    @Expose
    private String serviceName;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}