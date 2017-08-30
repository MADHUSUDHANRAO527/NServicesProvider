package com.infoskaters.nservicesprovider.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.models.TodaysJobModel;
import com.infoskaters.nservicesprovider.utilities.NServicesSingleton;

public class TrackMapActivity extends AppCompatActivity {
    private TodaysJobModel jobModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_map);
        jobModel = NServicesSingleton.getInstance().getSelectedJobModel();

    }
}
