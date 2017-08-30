package com.infoskaters.nservicesprovider.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.databinding.ActivityRegisterBinding;
import com.infoskaters.nservicesprovider.events.RegisterEvent;
import com.infoskaters.nservicesprovider.utilities.UImsgs;
import com.infoskaters.nservicesprovider.viewModels.RegisterVM;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding registerBinding;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mContext = this;
        RegisterVM registerVm = new RegisterVM(mContext, registerBinding);
        registerBinding.setRegister(registerVm);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RegisterEvent event) {
        if (event.success) {
            UImsgs.showToast(this, event.msg);
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            UImsgs.showToastErrorMessage(this, 0);
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
}
