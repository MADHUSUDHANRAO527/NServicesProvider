package com.infoskaters.nservicesprovider.events;

import java.util.ArrayList;

import com.infoskaters.nservicesprovider.models.TodaysJobModel;

/**
 * Created by Madhu on 03/08/17.
 */

public class JobStartEndEvent {
    public final boolean success;
    public int errorCode;
    public  String msg,status;
    public ArrayList<TodaysJobModel> modelList;

    public JobStartEndEvent(boolean success,String message,String status) {
        this.success = success;
        this.msg = message;
        this.status = status;
    }
    public JobStartEndEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }
}
