package mobile.app.nservicesprovider.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import mobile.app.nservicesprovider.R;
import mobile.app.nservicesprovider.models.TodaysJobModel;
import mobile.app.nservicesprovider.utilities.NServicesSingleton;

public class TrackMapActivity extends AppCompatActivity {
    private TodaysJobModel jobModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_map);
        jobModel = NServicesSingleton.getInstance().getSelectedJobModel();

    }
}
