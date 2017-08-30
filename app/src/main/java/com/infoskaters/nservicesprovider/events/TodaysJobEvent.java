package com.infoskaters.nservicesprovider.events;

import java.util.ArrayList;

import com.infoskaters.nservicesprovider.models.TodaysJobModel;

/**
 * Created by Madhu on 27/07/17.
 */

public class TodaysJobEvent {
    public final boolean success;
    public int errorCode;
    public  String msg;
    public ArrayList<TodaysJobModel> modelList;

    public TodaysJobEvent(boolean success, ArrayList<TodaysJobModel> todaysJobModel) {
        this.success = success;
        this.modelList = todaysJobModel;
    }

    public TodaysJobEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }
}