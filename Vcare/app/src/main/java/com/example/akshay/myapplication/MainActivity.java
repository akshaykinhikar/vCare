package com.example.akshay.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button danger, save_contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        danger = (Button) findViewById(R.id.btn_danger);
        save_contact = (Button) findViewById(R.id.btn_addContact);


        danger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alertIntent = new Intent(getApplicationContext(), AlertActivity.class);
                startActivity(alertIntent);
            }
        });

        save_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alertIntent = new Intent(getApplicationContext(), SaveContactActivity.class);
                startActivity(alertIntent);
            }
        });
    }
}
