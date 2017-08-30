package com.infoskaters.nservicesprovider.events;

import java.util.ArrayList;

import com.infoskaters.nservicesprovider.models.TodaysJobModel;

/**
 * Created by Madhu on 31/07/17.
 */

public class JobAcceptEvent {
    public final boolean success;
    public int errorCode;
    public  String msg;
    public ArrayList<TodaysJobModel> modelList;

    public JobAcceptEvent(boolean success, ArrayList<TodaysJobModel> todaysJobModel) {
        this.success = success;
        this.modelList = todaysJobModel;
    }
    public JobAcceptEvent(boolean success, String message) {
        this.success = success;
        this.msg = message;
    }
    public JobAcceptEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }
}