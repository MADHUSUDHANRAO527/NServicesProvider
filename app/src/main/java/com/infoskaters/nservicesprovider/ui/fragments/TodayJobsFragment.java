package com.infoskaters.nservicesprovider.ui.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.databinding.FragmentTodayjobsBinding;
import com.infoskaters.nservicesprovider.events.JobAcceptEvent;
import com.infoskaters.nservicesprovider.events.TodaysJobEvent;
import com.infoskaters.nservicesprovider.netwrokHelpers.VolleyHelper;
import com.infoskaters.nservicesprovider.ui.adapters.TodaysJobAdapter;
import com.infoskaters.nservicesprovider.utilities.PreferenceManager;
import com.infoskaters.nservicesprovider.utilities.UImsgs;

/**
 * Created by Madhu on 27/07/17.
 */

public class TodayJobsFragment extends Fragment {
    FragmentTodayjobsBinding fragmentTodayjobsBinding;
    private Context mContext;
    private VolleyHelper volleyHelper;
    private PreferenceManager mPreferenceManager;
    String spId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentTodayjobsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_todayjobs, container, false);
        View view = fragmentTodayjobsBinding.getRoot();
        mContext = getActivity();
        mPreferenceManager = new PreferenceManager(mContext);
        volleyHelper = new VolleyHelper(mContext);
        if (mPreferenceManager.getString("sp_id") != null) {
            spId = mPreferenceManager.getString("sp_id");
            volleyHelper.getTodaysJobsList(spId);
        }
        recyclerViewSetup();
        fragmentTodayjobsBinding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mPreferenceManager.getString("sp_id") != null) {
                    volleyHelper.getTodaysJobsList(spId);
                }
            }
        });
        return view;
    }

    private void recyclerViewSetup() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fragmentTodayjobsBinding.todayJobsRecyclerview.setLayoutManager(layoutManager);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TodaysJobEvent event) {
        if (event.success) {
            if (event.modelList != null && event.modelList.size() > 0) {
                TodaysJobAdapter allServicesAdapter = new TodaysJobAdapter(mContext, event.modelList);
                fragmentTodayjobsBinding.noJobsTxt.setVisibility(View.GONE);
                fragmentTodayjobsBinding.todayJobsRecyclerview.setAdapter(allServicesAdapter);

            } else {
                fragmentTodayjobsBinding.noJobsTxt.setVisibility(View.VISIBLE);
                fragmentTodayjobsBinding.noJobsTxt.setText("No todays job");
                fragmentTodayjobsBinding.todayJobsRecyclerview.setAdapter(null);

            }
        } else {
            UImsgs.showToast(mContext, event.msg);
        }
        dismissPbar();
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

    //after accept refreshing today jobs list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JobAcceptEvent event) {
        if (event.success) {
            if (mPreferenceManager.getString("sp_id") != null) {
                volleyHelper.getTodaysJobsList(spId);
            }
            UImsgs.showToast(mContext, event.msg);
        } else {
            UImsgs.showToastErrorMessage(mContext, event.errorCode);
        }
    }

    public void dismissPbar() {
        fragmentTodayjobsBinding.progressBar.setVisibility(View.GONE);
        fragmentTodayjobsBinding.swiperefresh.setRefreshing(false);
    }
}
