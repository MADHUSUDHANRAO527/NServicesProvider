package mobile.app.nservicesprovider.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import mobile.app.nservicesprovider.R;
import mobile.app.nservicesprovider.databinding.ActivityJobDetailBinding;
import mobile.app.nservicesprovider.events.JobStartEndEvent;
import mobile.app.nservicesprovider.models.QuestionAnswerModel;
import mobile.app.nservicesprovider.models.TodaysJobModel;
import mobile.app.nservicesprovider.netwrokHelpers.VolleyHelper;
import mobile.app.nservicesprovider.services.SendSPLocationService;
import mobile.app.nservicesprovider.ui.adapters.QuestionAnswerAdapter;
import mobile.app.nservicesprovider.utilities.NServicesSingleton;
import mobile.app.nservicesprovider.utilities.PreferenceManager;
import mobile.app.nservicesprovider.utilities.StringConstants;
import mobile.app.nservicesprovider.utilities.UImsgs;
import mobile.app.nservicesprovider.utilities.Utils;

/**
 * Created by Madhu on 31/07/17.
 */

public class JobDetailsActivity extends BaseActivity implements StringConstants {
    private ActivityJobDetailBinding activityJobDetailBinding;
    private Context mContext;
    private PreferenceManager mPreferenceManager;
    private TodaysJobModel jobModel;
    VolleyHelper volleyHelper;
    String bookingId, spLati, spLongi;
    private static final int REQUEST_PERMISSION_SERVICE_SETTING = 103;
    String jobStatus = "on_the_way";

    public JobDetailsActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityJobDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_detail);
        mContext = this;
        volleyHelper = new VolleyHelper(mContext);
        getSupportActionBar().setTitle("Job Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changeToolbarBackarrow();
        mPreferenceManager = new PreferenceManager(mContext);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        activityJobDetailBinding.questionsAnswerRecyclerview.setLayoutManager(layoutManager);
        activityJobDetailBinding.questionsAnswerRecyclerview.setNestedScrollingEnabled(false);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            bookingId = bundle.getString("booking_id");
            getSupportActionBar().setTitle("BN : " + bookingId);

        }
        if (mPreferenceManager.getString("user_lat") != null) {
            spLati = mPreferenceManager.getString("user_lat");
            spLongi = mPreferenceManager.getString("user_long");
        } else {
            activityJobDetailBinding.trackUserBtn.setVisibility(View.GONE);
            activityJobDetailBinding.routeMap.setVisibility(View.GONE);
            activityJobDetailBinding.startCustomerLocation.setVisibility(View.GONE);
        }

        jobModel = NServicesSingleton.getInstance().getSelectedJobModel();
        try {
            setData(jobModel);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jobModel.getJobStatus().equalsIgnoreCase("accepted")) {
            showNavLayout();
        } else if (jobModel.getDisplayStatus().equalsIgnoreCase("on the way")) {
            showNavLayout();
            activityJobDetailBinding.startCustomerLocation.setText("Reached Customer Location");
            Intent intent = new Intent(Intent.ACTION_SYNC, null, JobDetailsActivity.this, SendSPLocationService.class);
            startService(intent);
            jobStatus = "reached";

        } else {
            hideNavLayout();
        }
    }

    private void setData(final TodaysJobModel jobModel) throws JSONException {
        activityJobDetailBinding.jobId.setText(jobModel.getId());
        activityJobDetailBinding.jobTimeTxt.setText(Utils.convertToDateFromUTC(jobModel.getServiceReqDate()));
        activityJobDetailBinding.billedAmountTxt.setText(jobModel.getAmount());
        activityJobDetailBinding.serviceNameTxt.setText(jobModel.getService());
        activityJobDetailBinding.statusTxt.setText(jobModel.getDisplayStatus());
        activityJobDetailBinding.addressNameTxt.setText(jobModel.getLocation());

        JSONArray jsonReqDatails = new JSONArray(jobModel.getReqDetails());
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<QuestionAnswerModel>>() {
        }.getType();
        ArrayList<QuestionAnswerModel> questionAnswerModelList = gson.fromJson(jsonReqDatails.toString(), type);
        if (questionAnswerModelList.size() > 0) {
            QuestionAnswerAdapter questionAnswerAdapter = new QuestionAnswerAdapter(mContext, questionAnswerModelList);
            activityJobDetailBinding.questionsAnswerRecyclerview.setAdapter(questionAnswerAdapter);
            activityJobDetailBinding.serviceTxt.setVisibility(View.VISIBLE);
        }


       /* activityJobDetailBinding.consumerDetailsTxt.setText(jobModel.getDisplayStatus() + " " + jobModel.getLastname() + System.getProperty("line.separator") + jobModel.getLocation() + System.getProperty("line.separator") +
                jobModel.getMobile() + System.getProperty("line.separator") + jobModel.getEmail() + System.getProperty("line.separator") +
                "Service details" + System.getProperty("line.separator") + jobModel.getService() + System.getProperty("line.separator") +
                jobModel.getReqDetails() + System.getProperty("line.separator") +
                jobModel.getPrice() + ":" + jobModel.getPriceType());*/

       /* if (jobModel.getJobStatus().equalsIgnoreCase("On the way") || jobModel.getJobStatus().equalsIgnoreCase("assigned")) {
            activityJobDetailBinding.startEndJobLay.setVisibility(View.VISIBLE);
        } else {
            activityJobDetailBinding.startEndJobLay.setVisibility(View.GONE);
        }*/
        NServicesSingleton.getInstance().setConsumerLatitude(Double.parseDouble(jobModel.getConsumerLat()));
        NServicesSingleton.getInstance().setConsumerLongitude(Double.parseDouble(jobModel.getConsumerLng()));

        activityJobDetailBinding.trackUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spLati != null && spLongi != null) {

                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + Utils.getLocation(mContext) + "&daddr=" + jobModel.getConsumerLat() + "," + jobModel.getConsumerLng()));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                } else {
                    UImsgs.showToast(mContext, R.string.enable_perission_manually);
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                    intent.setData(uri);
                    //  mContext.startActivityForResult(intent);
                    ((Activity) mContext).startActivityForResult(intent, REQUEST_PERMISSION_SERVICE_SETTING);


                }
            }
        });
        activityJobDetailBinding.startCustomerLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (jobStatus.equalsIgnoreCase("at_the_location")) {
                        activityJobDetailBinding.startCustomerLocation.setText("START JOB");
                    }
                    volleyHelper.updateJobStatus(bookingId, jobStatus);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        activityJobDetailBinding.routeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobDetailsActivity.this, RouteMapActivity.class);
                startActivity(i);
            }
        });
        /*activityJobDetailBinding.endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    volleyHelper.updateJobStatus(bookingId, TRACK_SP_STATUS_JOB_DONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    public void showNavLayout() {
        activityJobDetailBinding.trackUserBtn.setVisibility(View.VISIBLE);
        activityJobDetailBinding.routeMap.setVisibility(View.VISIBLE);
        activityJobDetailBinding.startCustomerLocation.setVisibility(View.VISIBLE);
    }

    public void hideNavLayout() {
        activityJobDetailBinding.trackUserBtn.setVisibility(View.GONE);
        activityJobDetailBinding.routeMap.setVisibility(View.GONE);
        activityJobDetailBinding.startCustomerLocation.setVisibility(View.GONE);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JobStartEndEvent event) {
        if (event.success) {
            UImsgs.showToast(mContext, event.msg);
            if (event.status.equalsIgnoreCase("on_the_Way")) {
                activityJobDetailBinding.startCustomerLocation.setText("Reached Customer Location");
                jobStatus = "at_the_location";

                Intent intent = new Intent(Intent.ACTION_SYNC, null, JobDetailsActivity.this, SendSPLocationService.class);
                startService(intent);
                //   jobStatus = "reached";
            } else if (jobStatus.equalsIgnoreCase("at_the_location")) {
                jobStatus = "inprogress";
            } else if (event.status.equalsIgnoreCase("inprogress")) {
                activityJobDetailBinding.startCustomerLocation.setText("END JOB");
                jobStatus = "job_done";
            } else if (event.status.equalsIgnoreCase("job_done")) {
                startActivity(new Intent(this, MainActivity.class));
            }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
