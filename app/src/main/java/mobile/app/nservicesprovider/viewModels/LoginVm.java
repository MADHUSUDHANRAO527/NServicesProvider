package mobile.app.nservicesprovider.viewModels;

import android.content.Context;
import android.view.View;

import org.json.JSONObject;

import mobile.app.nservicesprovider.R;
import mobile.app.nservicesprovider.databinding.ActivityLoginBinding;
import mobile.app.nservicesprovider.netwrokHelpers.VolleyHelper;
import mobile.app.nservicesprovider.utilities.UImsgs;


/**
 * Created by madhu on 28/6/17.
 */

public class LoginVm {
    private ActivityLoginBinding loginBinding;
    private Context context;
    private String emailOrMobileNum;
    private VolleyHelper volleyHelper;

    public LoginVm(Context mContext, ActivityLoginBinding loginBinding) {
        this.loginBinding = loginBinding;
        this.context = mContext;
        volleyHelper = new VolleyHelper(context);
        setInitialState();
    }

    private void setInitialState() {
     /*   loginBinding.emailMobileEdit.setText("madhu@infoskaters.com");
        loginBinding.passwordEdit.setText("madhu123");
*/    }

    public void loginClick(View v) {
       if (loginBinding.emailMobileEdit.getText().length() == 0) {
            UImsgs.showSnackBar(v, context.getString(R.string.enter_proper_email_ph));
        } else if (loginBinding.passwordEdit.getText().length() < 6) {
            UImsgs.showSnackBar(v, context.getString(R.string.enter_proper_password));
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                emailOrMobileNum = loginBinding.emailMobileEdit.getText().toString();
                if (emailOrMobileNum.matches(".*\\d+.*")) {
                    jsonObject.put("username", emailOrMobileNum);

                } else {
                    jsonObject.put("username", emailOrMobileNum);

                }
                jsonObject.put("password", loginBinding.passwordEdit.getText());
                volleyHelper.signIn(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
