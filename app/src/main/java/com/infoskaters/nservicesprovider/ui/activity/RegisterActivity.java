package com.infoskaters.nservicesprovider.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.databinding.ActivityRegisterBinding;
import com.infoskaters.nservicesprovider.events.RegisterEvent;
import com.infoskaters.nservicesprovider.events.RegisteredServicesEvent;
import com.infoskaters.nservicesprovider.models.RegisterServicesModel;
import com.infoskaters.nservicesprovider.netwrokHelpers.VolleyHelper;
import com.infoskaters.nservicesprovider.utilities.NServicesSingleton;
import com.infoskaters.nservicesprovider.utilities.UImsgs;
import com.infoskaters.nservicesprovider.viewModels.RegisterVM;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding registerBinding;
    private Context mContext;
    String serviceName;
    private VolleyHelper volleyHelper;
    private String serviceId;
    public ArrayList<RegisterServicesModel> servicesModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mContext = this;
        volleyHelper = new VolleyHelper(mContext);

        RegisterVM registerVm = new RegisterVM(mContext, registerBinding);
        registerBinding.setRegister(registerVm);

       /* ArrayAdapter<CharSequence> avenueAdapter = ArrayAdapter
                .createFromResource(this, R.array.services_array,
                        R.layout.services_spinr_txt);
        avenueAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        registerBinding.servicesSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                avenueAdapter, R.layout.services_spinner_nothing_selected,
                RegisterActivity.this));
*/
        registerBinding.servicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long arg3) {
                // TODO Auto-generated method stub
                serviceName = String.valueOf(registerBinding.servicesSpinner.getSelectedItem());

                ((TextView) registerBinding.servicesSpinner.getSelectedView())
                        .setTextColor(getResources().getColor(R.color.black));

                if (serviceName == null || serviceName.isEmpty()) {
                    System.out.println("1st TIME NULLLLLLLLLLLLLLLLLL");
                } else {
                    if (servicesModelList.size() > 0)
                        for (int i = 0; i < servicesModelList.size(); i++) {
                            if (servicesModelList.get(i).getName().equalsIgnoreCase(serviceName)) {
                                Log.d("onItemSelected ID: ", serviceName + "-" + servicesModelList.get(pos).getId());
                                NServicesSingleton.getInstance().setRegisterSignUpServiceId(servicesModelList.get(pos).getId());
                            }
                        }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        volleyHelper.getRegsiteredServicesList();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RegisterEvent event) {
        if (event.success) {
            UImsgs.showToast(this, event.msg);
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            UImsgs.showToastErrorMessage(this, 0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RegisteredServicesEvent event) {
        if (event.success) {
            servicesModelList = event.modelList;
            List<String> servicesList = new ArrayList<>();
            for (int i = 0; i < event.modelList.size(); i++) {
                servicesList.add(event.modelList.get(i).getName());
            }
            ArrayAdapter<String> servicesAdapter = new ArrayAdapter<String>(
                    RegisterActivity.this, R.layout.services_spinr_txt,
                    servicesList);

         /*   servicesAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_item);
            registerBinding.servicesSpinner
                    .setAdapter(new NothingSelectedSpinnerAdapter(
                            servicesAdapter,
                            R.layout.services_spinner_nothing_selected,
                            RegisterActivity.this));
*/

            // Create an ArrayAdapter using the string array and a default spinner layout

// Specify the layout to use when the list of choices appears
            servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            registerBinding.servicesSpinner.setAdapter(servicesAdapter);


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
