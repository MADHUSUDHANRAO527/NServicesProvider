package com.infoskaters.nservicesprovider.events;

import org.json.JSONObject;

/**
 * Created by madhu on 25/6/17.
 */

public class ForgetPasswordEvent {
    public final boolean success;
    public int errorCode;
    public final String msg;
    public JSONObject response;

    public ForgetPasswordEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }

    public ForgetPasswordEvent(boolean success, String message, JSONObject jsonObject) {
        this.success = success;
        this.msg = message;
        this.response = jsonObject;
    }
}
