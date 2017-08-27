package mobile.app.nservicesprovider.events;

/**
 * Created by madhu on 25/6/17.
 */

public class MapDistanceEvent {
    public final boolean success;
    public int errorCode;
    public final String msg;
    public String distance;

    public MapDistanceEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }

    public MapDistanceEvent(boolean success, String message, String distance) {
        this.success = success;
        this.msg = message;
        this.distance = distance;
    }
}
