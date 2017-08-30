package com.infoskaters.nservicesprovider.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.databinding.ActivityUserSettingsBinding;
import com.infoskaters.nservicesprovider.utilities.PreferenceManager;

public class UserSettingsActivity extends BaseActivity {
    private ActivityUserSettingsBinding activityUserSettingsBinding;
    private PreferenceManager preferenceManager;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserSettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_settings);
        mContext = this;
      //  changeToolbarBackarrow();
        preferenceManager = new PreferenceManager(mContext);
        activityUserSettingsBinding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.clear();
                startActivity(new Intent(UserSettingsActivity.this, LoginActivity.class));
            }
        });
    }
}
