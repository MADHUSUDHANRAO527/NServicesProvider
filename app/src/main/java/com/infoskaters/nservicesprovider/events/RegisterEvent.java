package com.infoskaters.nservicesprovider.events;

/**
 * Created by madhu on 25/6/17.
 */

public class RegisterEvent {
    public final boolean success;
    private int errorCode;
    public final String msg;

    public RegisterEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }

    public RegisterEvent(boolean success, String message) {
        this.success = success;
        this.msg = message;
    }
}
