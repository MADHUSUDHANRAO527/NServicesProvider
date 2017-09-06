package com.infoskaters.nservicesprovider.viewModels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.databinding.ActivityRegisterBinding;
import com.infoskaters.nservicesprovider.netwrokHelpers.VolleyHelper;
import com.infoskaters.nservicesprovider.ui.activity.RegisterActivity;
import com.infoskaters.nservicesprovider.utilities.NServicesSingleton;
import com.infoskaters.nservicesprovider.utilities.UImsgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madhu on 28/6/17.
 */

public class RegisterVM {
    private ActivityRegisterBinding registerBinding;
    private Context context;
    private VolleyHelper volleyHelper;
    String checkEmailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public RegisterVM(Context mContext, ActivityRegisterBinding registerBinding, VolleyHelper volley) {
        this.registerBinding = registerBinding;
        this.context = mContext;
        this.volleyHelper = volley;
        setInitialState();
    }

    public RegisterVM(Context mContext, ActivityRegisterBinding registerBinding) {
        this.registerBinding = registerBinding;
        this.context = mContext;
        volleyHelper = new VolleyHelper(context);
        setInitialState();
    }

    private void setInitialState() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.services_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        registerBinding.servicesSpinner.setAdapter(adapter);

    }

    public void signUpClick(View view) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }

    public void registerClick(View v) {
        if (registerBinding.firstName.getText().length() == 0) {
            UImsgs.showSnackBar(v, context.getString(R.string.enter_proper_name));
        } else if (registerBinding.mobileNum.getText().length() == 0) {
            UImsgs.showSnackBar(v, context.getString(R.string.enter_proper_mblenum));
        } else if (!registerBinding.email.getText().toString().isEmpty() && checkEmailPattern.matches(registerBinding.email.getText().toString())) {
            UImsgs.showSnackBar(v, context.getString(R.string.enter_proper_email));
        } else if (registerBinding.password.getText().length() < 6) {
            UImsgs.showSnackBar(v, context.getString(R.string.enter_proper_password));
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", registerBinding.firstName.getText());
                jsonObject.put("email", registerBinding.email.getText());
                jsonObject.put("mobile", registerBinding.mobileNum.getText());
                jsonObject.put("service", NServicesSingleton.getInstance().getRegisterSignUpServiceId());
                jsonObject.put("password", registerBinding.password.getText());
                volleyHelper.signUp(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void signInClick(View view) {
        ((Activity) context).finish();
    }

}
