package com.infoskaters.nservicesprovider.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.databinding.ActivityUserSettingsBinding;
import com.infoskaters.nservicesprovider.netwrokHelpers.VolleyHelper;
import com.infoskaters.nservicesprovider.utilities.PreferenceManager;


public class UserProfileActivity extends BaseActivity {
    private ActivityUserSettingsBinding activityUserSettingsBinding;
    private PreferenceManager mPreferenceManager;
    private Context mContext;
    private VolleyHelper volleyHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserSettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_settings);
        mContext = this;
        volleyHelper = new VolleyHelper(mContext);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changeToolbarBackarrow();
        mPreferenceManager = new PreferenceManager(mContext);
        activityUserSettingsBinding.firstName.setText(mPreferenceManager.getString("sp_name"));
     //   activityUserSettingsBinding.lastName.setText(mPreferenceManager.getString("lastname"));
     //   activityUserSettingsBinding.mobileNum.setText(mPreferenceManager.getString("mobile"));
     //   activityUserSettingsBinding.email.setText(mPreferenceManager.getString("email"));

        activityUserSettingsBinding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreferenceManager.clear();
                startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
