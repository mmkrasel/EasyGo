package com.easyGo.easygo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {
    private EditText emailAddress;
    private Button enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailAddress  = findViewById(R.id.edt_email);
        enter = findViewById(R.id.enter);

        enter.setOnClickListener(v -> {
            String email = emailAddress.getText().toString();
            //verify the email address and proceed..
            Intent I = new Intent(Login.this,DataCollection.class);
            startActivity(I);
            finish();
        });
    }
}