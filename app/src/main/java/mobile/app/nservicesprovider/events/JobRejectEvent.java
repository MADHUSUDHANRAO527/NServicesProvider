package mobile.app.nservicesprovider.events;

import java.util.ArrayList;

import mobile.app.nservicesprovider.models.TodaysJobModel;

/**
 * Created by Madhu on 31/07/17.
 */

public class JobRejectEvent {
    public final boolean success;
    public int errorCode;
    public  String msg;
    public ArrayList<TodaysJobModel> modelList;

    public JobRejectEvent(boolean success, ArrayList<TodaysJobModel> todaysJobModel) {
        this.success = success;
        this.modelList = todaysJobModel;
    }
    public JobRejectEvent(boolean success,String message) {
        this.success = success;
        this.msg = message;
    }
    public JobRejectEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }
}