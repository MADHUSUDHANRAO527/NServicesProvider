package com.infoskaters.nservicesprovider.events;

import com.infoskaters.nservicesprovider.models.RegisterServicesModel;

import java.util.ArrayList;

/**
 * Created by Madhu on 27/07/17.
 */

public class RegisteredServicesEvent {
    public final boolean success;
    public int errorCode;
    public String msg;
    public ArrayList<RegisterServicesModel> modelList;

    public RegisteredServicesEvent(boolean success, ArrayList<RegisterServicesModel> registerServicesModels) {
        this.success = success;
        this.modelList = registerServicesModels;
    }

    public RegisteredServicesEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }
}