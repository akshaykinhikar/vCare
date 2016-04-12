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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        Register
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("msg", "register Button Clicked");
                JSONObject userRegisterDetail = new JSONObject();
                try {
                    if(((EditText) findViewById(R.id.et_password)).getText().toString().equals(((EditText) findViewById(R.id.et_password1)).getText().toString())){
                        Log.d("pwdCheck","Matched");
                        userRegisterDetail.put("username", ((EditText) findViewById(R.id.et_username)).getText().toString());
                        userRegisterDetail.put("password", ((EditText) findViewById(R.id.et_password)).getText().toString());
                        userRegisterDetail.put("password1",((EditText) findViewById(R.id.et_password1)).getText().toString());
                        userRegisterDetail.put("email",((EditText) findViewById(R.id.et_email)).getText().toString());
                        userRegisterDetail.put("contactNo",((EditText) findViewById(R.id.et_mobileNo)).getText());

                        AsyncHttpClient registerUser = new AsyncHttpClient();
                        try {
                            JSONObject userInfo = new JSONObject();
                            userInfo.put("userInfo", userRegisterDetail);
                            if(Utility.isNetConnected(getApplicationContext())){
                                registerUser.post(RegisterActivity.this, "http://192.168.137.180:8001/register/user/",
                                        new StringEntity(userInfo.toString()),"application/json",
                                        new JsonHttpResponseHandler(){
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                super.onSuccess(statusCode, headers, response);
                                                Log.d("msg", "response" + response);
                                                Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(myIntent);
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                super.onFailure(statusCode, headers, responseString, throwable);
                                                Log.d("msg", "error Response" +responseString);
                                            }
                                        });
                            }


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Password not matched", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}