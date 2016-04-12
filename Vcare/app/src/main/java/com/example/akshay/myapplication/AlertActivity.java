package com.example.akshay.myapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.akshay.myapplication.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class AlertActivity extends AppCompatActivity {
    Spinner spinner_contactList;
    List<String> contactNameList = new ArrayList<String>();

    String mob_Number, message_location;
    String message = "I am in trouble, my current location is ";
    Button alert;

    LocationManager lManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);


            //+++++++++++++++++++++++++++++++++++++++++++++++++++
            //++++++++++++get current Location +++++++++++++++++++++++++
            //+++++++++++++++++++++++++++++++++++++++++++++++++++
        lManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        lManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("onLocationChanged", "lat "+ location +location.getLatitude() + location.getLongitude());
                Toast.makeText(AlertActivity.this, "lat "+location.getLatitude() + location.getLongitude(),Toast.LENGTH_LONG ).show();
                getAddressByLatLng(location);
            }


//        +++++++++++++++++++++++++++++++++++++++++++++++
//        ++++++++++++++     get address   +++++++++++++
//        ++++++++++++++++++++++++++++++++++++++++++++++



            private void getAddressByLatLng(Location location) {
                location = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(getApplicationContext(),
                        "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + "," + location.getLongitude(),
                        new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Log.d("AlertActivity", "Failed " + responseString);
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                Log.d("AlertActivity", "Success " + responseString);
                                try {
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    if (jsonObject.getString("status").equals("OK")) {
                                        message_location = jsonObject.getJSONArray("results").getJSONObject(0)
                                                .getString("formatted_address");
                                        Toast.makeText(getApplicationContext(), ""+ message_location, Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    Log.d("AlertActivity", String.valueOf(e));
                                }
                            }
                        });
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(AlertActivity.this, "onStatusChanged "+status,Toast.LENGTH_LONG ).show();

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(AlertActivity.this, "onProviderEnabled "+provider,Toast.LENGTH_LONG ).show();

            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(AlertActivity.this, "onProviderDisabled "+provider,Toast.LENGTH_LONG ).show();

            }
        },getMainLooper());


        spinner_contactList = (Spinner) findViewById(R.id.spinner_contact);
        alert = (Button) findViewById(R.id.button_alert);

//        +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//        +++++++++         send sms        +++++++++++++++++++++++
//        +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage();
            }

            protected void sendSMSMessage() {
                Log.i("Send SMS", "");


                try {


                    SmsManager sms = SmsManager.getDefault();
                    PendingIntent sentPI;
                    String SENT = "SMS_SENT";
                    sentPI = PendingIntent.getBroadcast(AlertActivity.this, 0,new Intent(SENT), 0);
                    sms.sendTextMessage(mob_Number, null, message + message_location, sentPI, null);
//                    SmsManager smsManager = SmsManager.getDefault();
//                    Toast.makeText(AlertActivity.this, "mob_Number is " +mob_Number, Toast.LENGTH_SHORT).show();
//                    smsManager.sendTextMessage(mob_Number, null, message_location, null, null);
//                    Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                }

                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });



//            +++++++++++++++++++++++++++++++++++++++++++++++
//            ++++++ REQ For Getting ++++++++++++Data +++++++
//            +++++++++++++++++++++++++++++++++++++++++++++++
        JSONObject getTransacDetailObj = new JSONObject();
        //for cookies
        AsyncHttpClient login = new AsyncHttpClient();
        PersistentCookieStore tranCookies = new PersistentCookieStore(AlertActivity.this);
        login.setCookieStore(tranCookies);

        if (Utility.isNetConnected(AlertActivity.this)) {
            try {
                login.post(AlertActivity.this, "http://192.168.137.180:8001/send/contact/",
                        new StringEntity(getTransacDetailObj.toString()),
                        "application/json", new JsonHttpResponseHandler() {


                            @Override
                            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                try {
                                    Log.d("contactList", "onSuccess" + response.toString(4));
                                    int arrayLength = response.getJSONArray("contactList").length();
                                    for(int i = 0; i < arrayLength; i++){
                                        contactNameList.add(response.getJSONArray("contactList").getJSONObject(i).getString("name"));
                                    }
                                    // Creating adapter for spinner
                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AlertActivity.this, android.R.layout.simple_spinner_item, contactNameList);

                                    // Drop down layout style - list view with radio button
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                    spinner_contactList.setAdapter(dataAdapter);
//============
                                    spinner_contactList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        public void onItemSelected(AdapterView<?> parent, View view,
                                                                   int position, long id) {
                                            try {
                                                Toast.makeText(AlertActivity.this, "" + response.getJSONArray("contactList").getJSONObject(position).getString("contactNo").toString(), Toast.LENGTH_SHORT).show();
//                                              obj_message.put("name", response.getJSONArray("contactList").getJSONObject(position).getString("contactNo").toString());
                                                mob_Number = response.getJSONArray("contactList").getJSONObject(position).getString("contactNo").toString();
                                                Log.d("contact",""+ mob_Number);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
//==============


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                                Log.d("msg", "onFailure" + responseString + statusCode);
                            }
                        });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(AlertActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }
}
