package mobile.app.nservicesprovider.events;

import java.util.ArrayList;

import mobile.app.nservicesprovider.models.TodaysJobModel;

/**
 * Created by madhu on 25/6/17.
 */

public class NewJobRequestEvent {
    public final boolean success;
    public int errorCode;
    public final String msg;
    public ArrayList<TodaysJobModel> newJobEventModels;

    public NewJobRequestEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }

    public NewJobRequestEvent(boolean success, String message, ArrayList<TodaysJobModel> newJobEventModel) {
        this.success = success;
        this.msg = message;
        this.newJobEventModels = newJobEventModel;
    }
}
