package com.infoskaters.nservicesprovider.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.events.JobRejectEvent;
import com.infoskaters.nservicesprovider.events.NewJobRequestEvent;
import com.infoskaters.nservicesprovider.models.TodaysJobModel;
import com.infoskaters.nservicesprovider.netwrokHelpers.VolleyHelper;
import com.infoskaters.nservicesprovider.services.CurrentLocationService;
import com.infoskaters.nservicesprovider.services.NewJobRequestService;
import com.infoskaters.nservicesprovider.ui.fragments.FragmentDrawer;
import com.infoskaters.nservicesprovider.ui.fragments.HomeFragment;
import com.infoskaters.nservicesprovider.utilities.NServicesSingleton;
import com.infoskaters.nservicesprovider.utilities.PreferenceManager;
import com.infoskaters.nservicesprovider.utilities.UImsgs;
import com.infoskaters.nservicesprovider.utilities.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {
    private FragmentDrawer drawerFragment;
    public Toolbar toolbar;
    private static Context mContext;
    private PreferenceManager mPreferenceManager;
    private TextView spNameTxt;
    private RelativeLayout navHeaderLay;
    Dialog mPopup;
    static NewJobRequestService newJobRequestService;
    VolleyHelper volleyHelper;
    private String bookinigId = "", spId;
    CurrentLocationService appLocationService;
    private boolean locationPermission;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    private static final int ACCESS_FINE_LOCATION_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        spNameTxt = (TextView) findViewById(R.id.login_signup_txt);
        navHeaderLay = (RelativeLayout) findViewById(R.id.nav_header_container);
        toolbar.setTitle("My Jobs");
        mContext = this;
        newJobRequestService = new NewJobRequestService();
        mPreferenceManager = new PreferenceManager(mContext);
        volleyHelper = new VolleyHelper(mContext);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        toolbar.post(new Runnable() {
            @Override
            public void run() {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.hamburger_icon, null);
                toolbar.setNavigationIcon(d);
            }
        });
        if (mPreferenceManager.getString("sp_name") != null) {
            spNameTxt.setText(mPreferenceManager.getString("sp_name"));
            spId = mPreferenceManager.getString("sp_id");
        }
        navHeaderLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreferenceManager.getString("sp_name") != null) {
                    startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

        displayView(0);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NewJobRequestService.class);
        startService(intent);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (Utils.isGPSEnabled(mContext)) {
                getLocation();
            } else {
                buildAlertMessageNoGps(mContext);
            }
        } else {
            checkForPermissions();
        }
    }

    private void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //if user deny's //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need location Permission");
                builder.setMessage("This app needs location permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationPermission = true;
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need location Permission");
                builder.setMessage("This app needs locaiton permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        //   Uri uri = Uri.fromParts("package", getPackageName(), null);
                        //  intent.setData(uri);

                        startActivity(intent);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant locaiton!", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.ACCESS_FINE_LOCATION, true);
            editor.apply();
        } else {
            //You already have the permission, just go ahead.
            if (mPreferenceManager.getString("user_location") != null && !mPreferenceManager.getString("user_location").equals("")) {
                //  placeTxt.setText(mPreferenceManager.getString("user_location"));
                //  dismisPbar();
            } else {
                getLocation();
            }
        }

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                replaceFragment(new HomeFragment());
                title = "Home";
                break;
            case 1:

                break;
        }
    }

    public static void showLocalNewJobRequestNotification(Context context, ArrayList<TodaysJobModel> newJobEventModels) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("new request", "1");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.app_icon)
                .setTicker("Hearty365")
                .setContentTitle("New User requested!")
                .setContentText(newJobEventModels.get(0).getService())
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewJobRequestEvent event) {
        if (event.success) {
            if (event.newJobEventModels.size() > 0) {
                bookinigId = event.newJobEventModels.get(0).getId();
                if (mPopup == null) {
                    showLocalNewJobRequestNotification(mContext, event.newJobEventModels);
                    showFeedbackPopup(event.newJobEventModels);
                } else if (!mPopup.isShowing()) {
                    showLocalNewJobRequestNotification(mContext, event.newJobEventModels);
                    showFeedbackPopup(event.newJobEventModels);
                }
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

    private void showFeedbackPopup(ArrayList<TodaysJobModel> newJobEventModels) {

        mPopup = new Dialog(mContext);
        mPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPopup.setContentView(R.layout.new_job_request_popup);
        mPopup.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

        final TextView requestTxt = (TextView) mPopup.findViewById(R.id.service_name_txt);
        final TextView addressTxt = (TextView) mPopup.findViewById(R.id.address_name_txt);

        Button rejectBtn = (Button) mPopup.findViewById(R.id.reject_btn);
        Button acceptBtn = (Button) mPopup.findViewById(R.id.accept_btn);
        requestTxt.setText("Service : " + newJobEventModels.get(0).getService() + "\n\n" + "   Time : " + Utils.convertToDateFromUTC(newJobEventModels.get(0).getServiceReqDate()));
        addressTxt.setText(newJobEventModels.get(0).getLocation());
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("booking_id", bookinigId);
                    jsonObject.put("sp_id", spId);
                    jsonObject.put("accept", "true");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                volleyHelper.acceptRejectNewJobEvent(jsonObject, true);
                mPopup.dismiss();
            }
        });
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("booking_id", bookinigId);
                    jsonObject.put("sp_id", spId);
                    jsonObject.put("accept", "false");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                volleyHelper.acceptRejectNewJobEvent(jsonObject, false);
                mPopup.dismiss();
            }
        });
        mPopup.show();
    }


    //after accept refreshing today jobs list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JobRejectEvent event) {
        if (event.success) {
            UImsgs.showToast(this, event.msg);
            volleyHelper.getTodaysJobsList(spId);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                getLocation();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Need location Permission");
                    builder.setMessage("This app needs location permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void getLocation() {
        appLocationService = new CurrentLocationService(
                MainActivity.this);

        Location location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            NServicesSingleton.getInstance().setMycurrentLatitude(latitude);
            NServicesSingleton.getInstance().setMycurrentLongitude(longitude);
            mPreferenceManager.putString("user_lat", String.valueOf(latitude));
            mPreferenceManager.putString("user_long", String.valueOf(longitude));

        }
    }

    private void buildAlertMessageNoGps(final Context mContext) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //  mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeApp();
    }
}
