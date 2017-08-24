package mobile.app.nservicesprovider.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import mobile.app.nservicesprovider.R;
import mobile.app.nservicesprovider.databinding.ActivityLoginBinding;
import mobile.app.nservicesprovider.events.LoginEvent;
import mobile.app.nservicesprovider.utilities.PreferenceManager;
import mobile.app.nservicesprovider.utilities.UImsgs;
import mobile.app.nservicesprovider.viewModels.LoginVm;


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
            startActivity(new Intent(this,MainActivity.class));
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
