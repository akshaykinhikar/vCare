package com.example.akshay.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.akshay.myapplication.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.entity.StringEntity;

public class SaveContactActivity extends AppCompatActivity {
    PersistentCookieStore myCookieStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_contact);

        final EditText userName = (EditText) findViewById(R.id.contactName);
        final EditText con_number = (EditText) findViewById(R.id.contactNo);
        Button btn_login = (Button) findViewById(R.id.btn_save_contact);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("msg", "login Button Clicked");
                String name = userName.getText().toString();
                String numb = con_number.getText().toString();

                JSONObject jsonobj = new JSONObject();
                try {
                    jsonobj.put("name", name);
                    jsonobj.put("contactNo", numb);

                    //for cookies
                    AsyncHttpClient login = new AsyncHttpClient();
                    myCookieStore = new PersistentCookieStore(SaveContactActivity.this);
                    login.setCookieStore(myCookieStore);

                    try {
                        if (Utility.isNetConnected(getApplicationContext())) {
                            login.post(SaveContactActivity.this, "http://192.168.137.180:8001/save/contact/", new StringEntity(jsonobj.toString()),
                                    "application/json", new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            super.onSuccess(statusCode, headers, response);
                                            List<Cookie> cookies = myCookieStore.getCookies();
                                            Log.d("cookies", "" + cookies);
                                            for (Cookie c : cookies) {
                                                Log.d("Ex1", "" + c);
                                                Log.d("Ex1", "" + c.getValue());  //sessionID
                                            }
                                            try {
                                                if (response.getBoolean("status")) {
                                                    Intent i = new Intent(SaveContactActivity.this, MainActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    Log.d("msg", "In Invalid Credential");
                                                    Toast.makeText(getApplication(), "Invalid Credentials, Please Try Again", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(getApplication(), "Please Try Again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplication(), "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



    }

}