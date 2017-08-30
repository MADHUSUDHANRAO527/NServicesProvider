package com.infoskaters.nservicesprovider.viewModels;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.databinding.ActivityLoginBinding;
import com.infoskaters.nservicesprovider.netwrokHelpers.VolleyHelper;
import com.infoskaters.nservicesprovider.ui.activity.RegisterActivity;
import com.infoskaters.nservicesprovider.utilities.UImsgs;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by madhu on 28/6/17.
 */

public class LoginVm {
    private ActivityLoginBinding loginBinding;
    private Context context;
    private String emailOrMobileNum,email;
    private VolleyHelper volleyHelper;
    EditText emailEdit;

    public LoginVm(Context mContext, ActivityLoginBinding loginBinding) {
        this.loginBinding = loginBinding;
        this.context = mContext;
        volleyHelper = new VolleyHelper(context);
        setInitialState();
    }

    private void setInitialState() {
     /*   loginBinding.emailMobileEdit.setText("madhu@infoskaters.com");
        loginBinding.passwordEdit.setText("madhu123");
*/
    }

    public void forgetPassword(View view) {
        forgetPasswordPopup();
    }

    public void signUpClick(View view) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }

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

    private void forgetPasswordPopup() {
        final Dialog fogetPasswordDialog = new Dialog(context);
        fogetPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fogetPasswordDialog.setContentView(R.layout.forget_password_popup);
        fogetPasswordDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

        emailEdit = (EditText) fogetPasswordDialog.findViewById(R.id.email_mobile_edit);
        Button submitBtn = (Button) fogetPasswordDialog.findViewById(R.id.submit_password);
        Button cancelBtn = (Button) fogetPasswordDialog.findViewById(R.id.cancel_btn);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fogetPasswordDialog.dismiss();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEdit.getText().toString();
                if (email.length() == 0) {
                    UImsgs.showSnackBar(v, context.getString(R.string.enter_proper_email_ph));
                } else {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("email_id", email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    volleyHelper.forgetPassword(json);
                    fogetPasswordDialog.dismiss();
                }
            }
        });
        fogetPasswordDialog.show();
    }
}
