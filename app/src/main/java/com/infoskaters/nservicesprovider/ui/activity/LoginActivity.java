package com.infoskaters.nservicesprovider.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.databinding.ActivityLoginBinding;
import com.infoskaters.nservicesprovider.events.ForgetPasswordEvent;
import com.infoskaters.nservicesprovider.events.LoginEvent;
import com.infoskaters.nservicesprovider.utilities.PreferenceManager;
import com.infoskaters.nservicesprovider.utilities.UImsgs;
import com.infoskaters.nservicesprovider.viewModels.LoginVm;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding loginBinding;
    private Context mContext;
    private PreferenceManager mPreferenceManager;

    public LoginActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mContext = this;
        mPreferenceManager = new PreferenceManager(mContext);
        if (mPreferenceManager.getString("sp_id") != null) {
            startActivity(new Intent(this, MainActivity.class));
        }
        LoginVm loginVm = new LoginVm(mContext, loginBinding);
        loginBinding.setLogin(loginVm);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginEvent event) {
        if (event.success) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        UImsgs.showToast(this, event.msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ForgetPasswordEvent event) {
        if (event.success) {
            UImsgs.showToast(this, event.msg);
        } else {
            UImsgs.showToastErrorMessage(mContext, event.errorCode);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeApp();
    }
}
