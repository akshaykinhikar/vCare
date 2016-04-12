package com.example.akshay.myapplication;

import android.content.Intent;
import android.preference.PreferenceActivity;
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

public class LoginActivity extends AppCompatActivity {
    PersistentCookieStore myCookieStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText userName = (EditText) findViewById(R.id.et_username);
        final EditText passwd = (EditText) findViewById(R.id.et_password);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        Button btn_register = (Button) findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(myIntent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("msg", "login Button Clicked");
                String username = userName.getText().toString();
                String password = passwd.getText().toString();

                JSONObject jsonobj = new JSONObject();
                try {
                    jsonobj.put("username", username);
                    jsonobj.put("password", password);

                    //for cookies
                    AsyncHttpClient login = new AsyncHttpClient();
                    myCookieStore = new PersistentCookieStore(LoginActivity.this);
                    login.setCookieStore(myCookieStore);

                    try {
                        if (Utility.isNetConnected(getApplicationContext())) {
                            login.post(LoginActivity.this, "http://192.168.137.180:8001/auth/", new StringEntity(jsonobj.toString()),
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
                                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
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