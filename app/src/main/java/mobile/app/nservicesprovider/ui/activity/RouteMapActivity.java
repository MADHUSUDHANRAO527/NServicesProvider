package mobile.app.nservicesprovider.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobile.app.nservicesprovider.R;
import mobile.app.nservicesprovider.netwrokHelpers.VolleyHelper;
import mobile.app.nservicesprovider.utilities.MapAnimator;
import mobile.app.nservicesprovider.utilities.MapDirection.DirectionDataParser;
import mobile.app.nservicesprovider.utilities.NServicesSingleton;
import mobile.app.nservicesprovider.utilities.PreferenceManager;
import mobile.app.nservicesprovider.utilities.UImsgs;
import mobile.app.nservicesprovider.utilities.Utils;

import static java.lang.Double.parseDouble;
import static mobile.app.nservicesprovider.R.id.map;


public class RouteMapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "TRACKSP.class";
    private GoogleMap googleMapInstance;
    private TextView addressTxt;
    String locationAddress;
    private PreferenceManager preferenceManager;
    private Context mContext;
    ArrayList<String> latLongi = new ArrayList<>();
    private VolleyHelper volleyHelper;
    private String userLati, userLongi, serviceId, spLat = "", spLongi = "", bookingId;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_map_activty);
        mContext = this;
        getSupportActionBar().setTitle("Route Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MarkerPoints = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        volleyHelper = new VolleyHelper(mContext);
        //   addressTxt = (TextView) findViewById(R.id.address_txt);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        mContext = this;
        preferenceManager = new PreferenceManager(mContext);
        changeToolbarBackarrow();

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            bookingId = bundle.getString("booking_id");
            //  Intent intent = new Intent(Intent.ACTION_SYNC, null, RouteMapActivity.this, Service.class);
            //  intent.putExtra("booking_id", bookingId);
            //   startService(intent);
        }

//        UImsgs.showProgressDialog("loading data", mContext);


       /* googleMapInstance.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                spLat = String.valueOf(latLng.latitude);
                spLongi = String.valueOf(latLng.longitude);
                Log.e(TAG, "SP-LATI:LONGI " + spLat + "-" + spLongi);
            }
        });*/



      /*  Log.e(TAG, "Lat:LONGI " + userLati + "-" + userLongi);
        if (preferenceManager.getString("user_lat") != null) {
            if (InternetUtility.isConnected(mContext)) {
                UImsgs.showProgressDialog("loading data", mContext);
                volleyHelper.getServiceProvidersMapList(preferenceManager.getString("user_lat"), preferenceManager.getString("user_long") + "/" + serviceId);
            } else {
                UImsgs.showSnackBar(getWindow().getDecorView().findViewById(android.R.id.content)
                        , mContext.getString(R.string.internet_connection));
            }
        }
        if (preferenceManager.getString("user_location") != null) {
            addressTxt.setText(preferenceManager.getString("user_location"));
        }*/

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapInstance = googleMap;

        List<LatLng> list = new ArrayList<>();
        LatLng spLatLng = new LatLng(NServicesSingleton.getInstance().getConsumerLatitude(), NServicesSingleton.getInstance().getConsumerLongitude());

        if (preferenceManager.getString("user_lat") != null) {
            LatLng userLatLng = new LatLng(NServicesSingleton.getInstance().getMycurrentLatitude(), NServicesSingleton.getInstance().getMycurrentLongitude());

            list.add(0, spLatLng);
            list.add(1, userLatLng);
            drawPolyLineOnMap(list);

            MarkerPoints.add(userLatLng);
            MarkerPoints.add(spLatLng);

        }

        LatLng origin = MarkerPoints.get(0);
        LatLng dest = MarkerPoints.get(1);

        drawMarker(origin, dest);

        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origin);
        builder.include(dest);

        LatLngBounds bounds = builder.build();

        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMapInstance.moveCamera(CameraUpdateFactory.newLatLng(origin));
        googleMapInstance.animateCamera(CameraUpdateFactory.zoomTo(11));

      /*  googleMapInstance.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                spLat = String.valueOf(marker.getPosition().latitude);
                spLongi = String.valueOf(marker.getPosition().longitude);
                Log.e(TAG, "SP-LATI:LONGI " + spLat + "-" + spLongi);
                return false;
            }
        });*/
    }

    protected Marker createMarker(LatLng latLng) {
        return googleMapInstance.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .anchor(0.5f, 0.5f)
                .title(""));

    }


   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TrackSPEvent event) {
        if (event.success) {
            spLat = event.trackSPModel.getLatitude();
            spLongi = event.trackSPModel.getLongitude();
            List<LatLng> list = new ArrayList<>();
            LatLng spLatLng = new LatLng(Double.parseDouble(spLat), Double.parseDouble(spLongi));

            if (preferenceManager.getString("user_lat") != null) {
                LatLng userLatLng = new LatLng(Double.parseDouble(preferenceManager.getString("user_lat")), Double.parseDouble(preferenceManager.getString("user_long")));

                list.add(0, spLatLng);
                list.add(1, userLatLng);
                drawPolyLineOnMap(list);

            }


         *//*   LatLng userLocation = new LatLng(Double.parseDouble(spLat), Double.parseDouble(spLongi));
            googleMapInstance.addMarker(new MarkerOptions().position(userLocation));
   *//*
        } else {
            UImsgs.showToastErrorMessage(this, event.errorCode);
        }
        UImsgs.dismissProgressDialog();

    }*/

    public void drawPolyLineOnMap(List<LatLng> list) {
      /*  PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.RED);
        polyOptions.width(5);
        polyOptions.addAll(list);

        googleMapInstance.clear();
        googleMapInstance.addPolyline(polyOptions);
*/
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
           /* builder.include(createMarker(latLng.latitude,
                    latLng.longitude, "", "", Utils.setMarkerSize(mContext))
                    .getPosition());
        */
            drawMarker(list.get(0), list.get(1));

        }

        final LatLngBounds bounds = builder.build();

        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        googleMapInstance.moveCamera(cu);
        googleMapInstance.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);

        startAnim(list);

    }

    private void startAnim(List<LatLng> list) {
        if (googleMapInstance != null) {
            MapAnimator.getInstance().animateRoute(googleMapInstance, list);
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    public void drawMarker(LatLng source_point, LatLng destination_point) {

        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions1 = new MarkerOptions();
       /* markerOptions1.title("Marker");
        markerOptions1.snippet("Marker Yo Yo");
     */
        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerOptions1.position(source_point);

        MarkerOptions markerOptions2 = new MarkerOptions();
      /*  markerOptions2.title("Marker2");
        markerOptions2.snippet("Marker Xo Xo");
      */
        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptions2.position(destination_point);

        // Adding marker on the Google Map

        googleMapInstance.addMarker(markerOptions1);
        googleMapInstance.addMarker(markerOptions2);
    }

    private Marker createMarker(double latitude, double longitude, String s, String s1, Bitmap bitmap) {


        return googleMapInstance.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(s)
                .icon(BitmapDescriptorFactory.fromBitmap(Utils.setMarkerMapSize(mContext))));

    }


    private void locateUserAddress() {
        LatLng latLong = new LatLng(parseDouble(userLati), parseDouble(userLongi));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLong, 15);
        googleMapInstance.animateCamera(yourLocation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                UImsgs.dismissProgressDialog();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DirectionDataParser parser = new DirectionDataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                googleMapInstance.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = googleMapInstance.addMarker(markerOptions);

        //move map camera
        googleMapInstance.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMapInstance.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
