package com.infoskaters.nservicesprovider.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.infoskaters.nservicesprovider.R;
import com.infoskaters.nservicesprovider.services.CurrentLocationService;


/**
 * Created by madhu on 10/7/17.
 */

public class Utils {

    public static String convertToDateFromUTC(String createdAt) {
        if (createdAt.length() > 10) {
            String datee = createdAt.substring(0, 10);
            String fromYear = datee.substring(0, 4);
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date dat = df.parse(datee);
                System.out.println(createdAt);
                createdAt = dat.toString();
                System.out.println(createdAt.length());
                createdAt = createdAt.substring(3, 10);
                System.out.println(createdAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return createdAt + "," + fromYear;
        } else {
            return createdAt;
        }
    }

    public static Bitmap setMarkerSize(Context mContext) {
        int height = 95;
        int width = 95;
        BitmapDrawable bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.user_photo);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    //july 14th 2017
    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    //2009-12-31
    public static String getCurrentDateFormat() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    //CompareDates
    public static boolean comapareDates(String fromDate, String toDate) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //edit here
        Date todayDate = sdf.parse(fromDate);
        Date toDatee = sdf.parse(toDate);
        if (todayDate.before(toDatee) || todayDate.equals(toDatee)) {
            return true;
        }
        return false;
    }

    public static String getLocation(Context mCotext) {
        double latitude = 0, longitude = 0;
        CurrentLocationService appLocationService = new CurrentLocationService(
                mCotext);
        Location location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            NServicesSingleton.getInstance().setMycurrentLatitude(latitude);
            NServicesSingleton.getInstance().setMycurrentLongitude(longitude);
        }
        return latitude + "," + longitude;

    }

    public static Bitmap setMarkerMapSize(Context mContext) {
        int height = 95;
        int width = 95;
        BitmapDrawable bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    public static boolean isGPSEnabled(Context mContext) {
        final LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //buildAlertMessageNoGps(mContext);
            return false;
        } else {
            return true;
        }
    }

    public static String getDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        String distance = kmInDec + " km";
        return distance;
    }

}
