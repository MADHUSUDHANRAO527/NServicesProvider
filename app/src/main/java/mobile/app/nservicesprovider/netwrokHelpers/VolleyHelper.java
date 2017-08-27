package mobile.app.nservicesprovider.netwrokHelpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.app.nservicesprovider.events.FutureJobEvent;
import mobile.app.nservicesprovider.events.JobAcceptEvent;
import mobile.app.nservicesprovider.events.JobRejectEvent;
import mobile.app.nservicesprovider.events.JobStartEndEvent;
import mobile.app.nservicesprovider.events.LoginEvent;
import mobile.app.nservicesprovider.events.MapDistanceEvent;
import mobile.app.nservicesprovider.events.NewJobRequestEvent;
import mobile.app.nservicesprovider.events.OnOffJobEvent;
import mobile.app.nservicesprovider.events.TodaysJobEvent;
import mobile.app.nservicesprovider.models.TodaysJobModel;
import mobile.app.nservicesprovider.utilities.Constants;
import mobile.app.nservicesprovider.utilities.CustomerServicesApp;
import mobile.app.nservicesprovider.utilities.NServicesSingleton;
import mobile.app.nservicesprovider.utilities.PreferenceManager;


/**
 * Created by Madhu on 17/06/17.
 */
public class VolleyHelper {
    private static final String TAG = "Volley";
    private Context mContext;
    private PreferenceManager mPreferenceManager;

    public VolleyHelper(Context context) {
        this.mContext = context;
        mPreferenceManager = new PreferenceManager(context);
    }

    public void signIn(JSONObject loginJson) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Constants.BASE_URL + Constants.SIGNIN_URL, loginJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject loginResponse) {
                        //{"status":true,"data":{"id":"26","name":"madhu","mobile":"9036849397","email":"madhu@infoskaters.com","icon":"","status":"","job_completed":"0","services":[],
                        // "rating":0},"token":"e5d50e9fe8333be196ce","message":"Service Provider is Successfully Loggedin!!!"}
                        Log.d(TAG, loginResponse.toString());
                        //{"status":false,"data":[],"message":"Invalid UserName or Password"}
                        try {
                            if (loginResponse.getString("status").equals("false")) {
                                EventBus.getDefault().post(new LoginEvent(false, loginResponse.getString("message"), loginResponse));
                            } else {
                                JSONObject jsonObject = loginResponse.getJSONObject("data");
                                mPreferenceManager.putString("login_response", jsonObject.toString());
                                mPreferenceManager.putString("sp_id", jsonObject.getString("id"));
                                mPreferenceManager.putString("token", loginResponse.getString("token"));
                                mPreferenceManager.putString("sp_name", jsonObject.getString("name"));

                                NServicesSingleton.getInstance().setToken(loginResponse.getString("token"));
                                EventBus.getDefault().post(new LoginEvent(true, loginResponse.getString("message"), loginResponse));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.d(TAG, error.toString());
                error.printStackTrace();
                JSONObject errorJson = null;
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        errorJson = new JSONObject(res);
                        Log.d(TAG, "onErrorResponse: " + errorJson);
                        // Toast.makeText(mContext, errorJson.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
                if (error.networkResponse != null) {
                    try {
                        EventBus.getDefault().post(new LoginEvent(false, error.networkResponse.statusCode, errorJson.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    EventBus.getDefault().post(new LoginEvent(false, 0, error.toString()));
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                // params.put("Authorization", getAuthHeader());
                return params;
            }
        };
        jsonRequest.setShouldCache(false);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonRequest);
    }

    public void getTodaysJobsList(String spId) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Constants.BASE_URL + Constants.TODAY_JOBS_LIST_URL + spId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject spResponse) {
                        Log.d(TAG, spResponse.toString());
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<TodaysJobModel>>() {
                        }.getType();
                        ArrayList<TodaysJobModel> todaysJobModels = null;
                        try {
                            todaysJobModels = gson.fromJson(spResponse.getJSONArray("data").toString(), type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new TodaysJobEvent(true, todaysJobModels));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.d(TAG, error.toString());
                error.printStackTrace();
                JSONObject errorJson = null;
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        errorJson = new JSONObject(res);
                        Log.d(TAG, "onErrorResponse: " + errorJson);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
                if (error.networkResponse != null) {
                    try {
                        EventBus.getDefault().post(new TodaysJobEvent(false, error.networkResponse.statusCode, errorJson.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    EventBus.getDefault().post(new TodaysJobEvent(false, 0, error.toString()));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                Log.d("token: ", mPreferenceManager.getString("token"));
                params.put("Authorization", mPreferenceManager.getString("token"));
                return params;
            }
        };
        jsonRequest.setShouldCache(false);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonRequest);
    }

    public void getFutureJobsList(String spId) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Constants.BASE_URL + Constants.FUTURE_JOBS_LIST_URL + spId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject spResponse) {
                        Log.d(TAG, spResponse.toString());
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<TodaysJobModel>>() {
                        }.getType();
                        ArrayList<TodaysJobModel> todaysJobModels = null;
                        try {
                            todaysJobModels = gson.fromJson(spResponse.getJSONArray("data").toString(), type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new FutureJobEvent(true, todaysJobModels));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.d(TAG, error.toString());
                error.printStackTrace();
                JSONObject errorJson = null;
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        errorJson = new JSONObject(res);
                        Log.d(TAG, "onErrorResponse: " + errorJson);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
                if (error.networkResponse != null) {
                    try {
                        EventBus.getDefault().post(new FutureJobEvent(false, error.networkResponse.statusCode, errorJson.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    EventBus.getDefault().post(new FutureJobEvent(false, 0, error.toString()));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                Log.d("token: ", mPreferenceManager.getString("token"));
                params.put("Authorization", mPreferenceManager.getString("token"));
                return params;
            }
        };
        jsonRequest.setShouldCache(false);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonRequest);
    }

    public void getNewJobRequest(String spId) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Constants.BASE_URL + Constants.GET_NEW_JOB_REQUEST + spId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject spResponse) {
                        Log.d(TAG, spResponse.toString());

                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<TodaysJobModel>>() {
                        }.getType();
                        ArrayList<TodaysJobModel> todaysJobModels = null;
                        try {
                            todaysJobModels = gson.fromJson(spResponse.getJSONArray("data").toString(), type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new NewJobRequestEvent(true, "", todaysJobModels));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.d(TAG, error.toString());
                error.printStackTrace();
                JSONObject errorJson = null;
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        errorJson = new JSONObject(res);
                        Log.d(TAG, "onErrorResponse: " + errorJson);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
                if (error.networkResponse != null) {
                    try {
                        EventBus.getDefault().post(new FutureJobEvent(false, error.networkResponse.statusCode, errorJson.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    EventBus.getDefault().post(new FutureJobEvent(false, 0, error.toString()));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                Log.d("token: ", mPreferenceManager.getString("token"));
                params.put("Authorization", mPreferenceManager.getString("token"));
                return params;
            }
        };
        jsonRequest.setShouldCache(false);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonRequest);
    }

    public void acceptRejectNewJobEvent(JSONObject jsonObject, final boolean status) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Constants.BASE_URL + Constants.JOB_REQUEST_ACCEPT_REJECT, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        Log.d(TAG, jsonResponse.toString());
                        try {
                            if (status)
                                EventBus.getDefault().post(new JobAcceptEvent(true, jsonResponse.getString("message")));
                            else
                                EventBus.getDefault().post(new JobRejectEvent(true, jsonResponse.getString("message")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //{"status":true,"data":[],"message":"Job Request Rejected Successfully"}

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.d(TAG, error.toString());
                error.printStackTrace();
                JSONObject errorJson = null;
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        errorJson = new JSONObject(res);
                        Log.d(TAG, "onErrorResponse: " + errorJson);
                        // Toast.makeText(mContext, errorJson.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
                if (error.networkResponse != null) {
                    try {
                        EventBus.getDefault().post(new JobAcceptEvent(false, error.networkResponse.statusCode, errorJson.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    EventBus.getDefault().post(new JobAcceptEvent(false, 0, error.toString()));
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                Log.d("token: ", mPreferenceManager.getString("token"));
                params.put("Authorization", mPreferenceManager.getString("token"));
                return params;
            }
        };
        jsonRequest.setShouldCache(false);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonRequest);
    }

    public void updateJobStatus(String bookingId, final String status) throws JSONException {
        JSONObject json = new JSONObject();

        if (mPreferenceManager.getString("sp_id") != null) {
            json.put("sp_id", mPreferenceManager.getString("sp_id"));
            json.put("booking_id", bookingId);
            json.put("status", status);
        }

        JSONObject locationJson = new JSONObject();
        if (mPreferenceManager.getString("user_lat") != null) {
            locationJson.put("latitude", mPreferenceManager.getString("user_lat"));
            locationJson.put("longitude", mPreferenceManager.getString("user_long"));
        }

        json.put("location", locationJson);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Constants.BASE_URL + Constants.UPDATE_JOB_STATUS, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject spResponse) {
                        //{"status":true,"data":[],"message":"Service provider status updates successfully"}
                        Log.d("Accept Reject API:", spResponse.toString());

                        try {
                            EventBus.getDefault().post(new JobStartEndEvent(true, spResponse.getString("message"), status));
                            ;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.d(TAG, error.toString());
                error.printStackTrace();
                JSONObject errorJson = null;
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        errorJson = new JSONObject(res);
                        Log.d(TAG, "onErrorResponse: " + errorJson);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
                if (error.networkResponse != null) {
                    try {
                        EventBus.getDefault().post(new FutureJobEvent(false, error.networkResponse.statusCode, errorJson.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    EventBus.getDefault().post(new FutureJobEvent(false, 0, error.toString()));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                Log.d("token: ", mPreferenceManager.getString("token"));
                params.put("Authorization", mPreferenceManager.getString("token"));
                return params;
            }
        };
        jsonRequest.setShouldCache(false);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonRequest);
    }

    public void sendSPLocation() throws JSONException {
        JSONObject json = new JSONObject();

        if (mPreferenceManager.getString("sp_id") != null) {
            json.put("sp_id", mPreferenceManager.getString("sp_id"));
        }

        JSONObject locationJson = new JSONObject();
        if (mPreferenceManager.getString("user_lat") != null) {
            locationJson.put("latitude", mPreferenceManager.getString("user_lat"));
            locationJson.put("longitude", mPreferenceManager.getString("user_long"));
        }

        json.put("location", locationJson);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Constants.BASE_URL + Constants.SEND_SP_LOCATION, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject spResponse) {
                        Log.d(TAG + "sendSPLocation:", spResponse.toString());

                      /*  Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<TodaysJobModel>>() {
                        }.getType();
                        ArrayList<TodaysJobModel> todaysJobModels = null;
                        try {
                            todaysJobModels = gson.fromJson(spResponse.getJSONArray("data").toString(), type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new NewJobRequestEvent(true, "", todaysJobModels));*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.d(TAG, error.toString());
                error.printStackTrace();
                JSONObject errorJson = null;
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        errorJson = new JSONObject(res);
                        Log.d(TAG, "onErrorResponse: " + errorJson);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
                if (error.networkResponse != null) {
                    try {
                        EventBus.getDefault().post(new FutureJobEvent(false, error.networkResponse.statusCode, errorJson.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    EventBus.getDefault().post(new FutureJobEvent(false, 0, error.toString()));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                Log.d("token: ", mPreferenceManager.getString("token"));
                params.put("Authorization", mPreferenceManager.getString("token"));
                return params;
            }
        };
        jsonRequest.setShouldCache(false);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonRequest);
    }

    public void onOffJOb(JSONObject json) throws JSONException {

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Constants.BASE_URL + Constants.UPDATE_JOB_STATUS, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject spResponse) {
                        //{"status":true,"data":[],"message":"Service provider status updates successfully"}
                        Log.d("Accept Reject API:", spResponse.toString());

                        try {
                            EventBus.getDefault().post(new OnOffJobEvent(true, spResponse.getString("message")));
                            ;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.d(TAG, error.toString());
                error.printStackTrace();
                JSONObject errorJson = null;
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        errorJson = new JSONObject(res);
                        Log.d(TAG, "onErrorResponse: " + errorJson);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
                if (error.networkResponse != null) {
                    try {
                        EventBus.getDefault().post(new OnOffJobEvent(false, error.networkResponse.statusCode, errorJson.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    EventBus.getDefault().post(new OnOffJobEvent(false, 0, error.toString()));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                Log.d("token: ", mPreferenceManager.getString("token"));
                params.put("Authorization", mPreferenceManager.getString("token"));
                return params;
            }
        };
        jsonRequest.setShouldCache(false);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonRequest);
    }

    public void calculateDistance(String url) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d(TAG, jsonObject.toString());
                        JSONArray array = null;
                        try {
                            array = jsonObject.getJSONArray("routes");
                            JSONObject routes = array.getJSONObject(0);
                            JSONArray legs = routes.getJSONArray("legs");
                            JSONObject steps = legs.getJSONObject(0);
                            JSONObject distance = steps.getJSONObject("distance");
                            String parsedDistance = distance.getString("text");

                            EventBus.getDefault().post(new MapDistanceEvent(true, "", parsedDistance));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.d(TAG, error.toString());
                error.printStackTrace();
                JSONObject errorJson = null;
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        errorJson = new JSONObject(res);
                        Log.d(TAG, "onErrorResponse: " + errorJson);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
                if (error.networkResponse != null) {
                    try {
                        EventBus.getDefault().post(new MapDistanceEvent(false, error.networkResponse.statusCode, errorJson.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    EventBus.getDefault().post(new TodaysJobEvent(false, 0, error.toString()));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                Log.d("token: ", mPreferenceManager.getString("token"));
                params.put("Authorization", mPreferenceManager.getString("token"));
                return params;
            }
        };
        jsonRequest.setShouldCache(false);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonRequest);
    }
}

