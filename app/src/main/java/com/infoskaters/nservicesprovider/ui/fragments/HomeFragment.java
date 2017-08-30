package com.infoskaters.nservicesprovider.ui.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.databinding.FragmentHomeBinding;
import com.infoskaters.nservicesprovider.events.OnOffJobEvent;
import com.infoskaters.nservicesprovider.models.LoginBaseModel;
import com.infoskaters.nservicesprovider.netwrokHelpers.VolleyHelper;
import com.infoskaters.nservicesprovider.ui.adapters.JobsViewPagerAdapter;
import com.infoskaters.nservicesprovider.ui.adapters.SpServicesAdapter;
import com.infoskaters.nservicesprovider.utilities.InternetUtility;
import com.infoskaters.nservicesprovider.utilities.PreferenceManager;
import com.infoskaters.nservicesprovider.utilities.UImsgs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding fragmentHomeBinding;
    private Context mContext;
    private VolleyHelper volleyHelper;
    private int timerInterval = 2000, i = 0;
    Handler mHandler = new Handler();
    Thread downloadThread;
    boolean isRunning = true;
    private PreferenceManager mPreferenceManager;
    String spId;
    LoginBaseModel loginModel = new LoginBaseModel();

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = fragmentHomeBinding.getRoot();
        mContext = getActivity();
        mPreferenceManager = new PreferenceManager(mContext);

        volleyHelper = new VolleyHelper(mContext);
        setupViewPager(fragmentHomeBinding.viewpager);
        if (mPreferenceManager.getString("sp_id") != null) {
            spId = mPreferenceManager.getString("sp_id");
        }
        if (mPreferenceManager.getString("login_response") != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<LoginBaseModel>() {
            }.getType();
            JSONObject json = null;
            try {
                json = new JSONObject(mPreferenceManager.getString("login_response"));

                loginModel = gson.fromJson(json.toString(), type);

                fragmentHomeBinding.spNameTxt.setText(loginModel.getName());
                fragmentHomeBinding.jobsCompletedTxt.setText("Jobs Completed : " + loginModel.getJobsCompleted());
                int rating = (int) loginModel.getRating();
                fragmentHomeBinding.ratingBar.setNumStars(rating);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (InternetUtility.isConnected(mContext)) {
            recyclerViewSetup();

        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), mContext.getString(R.string.internet_connection), Snackbar.LENGTH_SHORT).show();
        }
        fragmentHomeBinding.onlineSwitch.setChecked(true);
        fragmentHomeBinding.onlineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sp_id", mPreferenceManager.getString("sp_id"));
                    if (b) {
                        fragmentHomeBinding.onlineSwitch.setText(R.string.online);
                        jsonObject.put("status", "on");
                        volleyHelper.onOffJOb(jsonObject);
                    } else {
                        fragmentHomeBinding.onlineSwitch.setText(R.string.offline);
                        jsonObject.put("status", "off");
                        volleyHelper.onOffJOb(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private void recyclerViewSetup() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        fragmentHomeBinding.servicesRecyclerView.setLayoutManager(gridLayoutManager);
        fragmentHomeBinding.servicesRecyclerView.setNestedScrollingEnabled(false);
        if (loginModel.getServices().size() > 0) {
            SpServicesAdapter allServicesAdapter = new SpServicesAdapter(mContext, loginModel.getServices());
            fragmentHomeBinding.servicesRecyclerView.setAdapter(allServicesAdapter);
        } else {
            fragmentHomeBinding.servicesRecyclerView.setVisibility(View.GONE);

        }
    }

    private void setupViewPager(final ViewPager viewPager) {
        JobsViewPagerAdapter adapter = new JobsViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TodayJobsFragment(), "Today's Job");
        adapter.addFragment(new FutureJobsFragment(), "Future Jobs");
        viewPager.setAdapter(adapter);
        fragmentHomeBinding.tabs.setupWithViewPager(fragmentHomeBinding.viewpager);
        fragmentHomeBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int position) {
                if (position == 0)
                    volleyHelper.getTodaysJobsList(spId);
                else
                    volleyHelper.getFutureJobsList(spId);

            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.
            }
        });
    }

    //after accept refreshing today jobs list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnOffJobEvent event) {
        if (event.success) {
            UImsgs.showToast(mContext, event.msg);
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
