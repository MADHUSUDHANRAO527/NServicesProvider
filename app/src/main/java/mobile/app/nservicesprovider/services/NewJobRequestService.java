package mobile.app.nservicesprovider.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import mobile.app.nservicesprovider.netwrokHelpers.VolleyHelper;
import mobile.app.nservicesprovider.utilities.PreferenceManager;

/**
 * Created by Madhu on 28/07/17.
 */

public class NewJobRequestService extends Service {
    Handler mHandler = new Handler();
    Thread downloadThread;
    boolean isRunning = true;
    private VolleyHelper volleyHelper;
    private PreferenceManager mPreferenceManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        //  Toast.makeText(this, " MyService Created ", Toast.LENGTH_LONG).show();
        volleyHelper = new VolleyHelper(this);
        mPreferenceManager = new PreferenceManager(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                //  Log.d("run: ", "service is running!");

                volleyHelper.getNewJobRequest(mPreferenceManager.getString("sp_id"));
                // t.cancel();
                // Log.d("run: ", "service is stopped!");

            }
        }, 0, 5000);
        return START_STICKY;
    }

    public void stopTimer() {
        try {
            downloadThread.join();
            downloadThread.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
