package com.infoskaters.nservicesprovider.events;

/**
 * Created by Madhu on 03/08/17.
 */

public class OnOffJobEvent {
    public final boolean success;
    public int errorCode;
    public  String msg;

    public OnOffJobEvent(boolean success, String message) {
        this.success = success;
        this.msg = message;
    }
    public OnOffJobEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }
}
