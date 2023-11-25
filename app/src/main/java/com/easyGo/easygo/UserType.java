package com.easyGo.easygo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class UserType extends AppCompatActivity {
    private Button admin,user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        admin = findViewById(R.id.admin);
        user = findViewById(R.id.user);

        admin.setOnClickListener(v -> {
            Intent I = new Intent(UserType.this,Login.class);
            startActivity(I);

        });
        user.setOnClickListener(v -> {
            Toast.makeText(UserType.this, "This Feature is not available now..Stay Tuned....", Toast.LENGTH_SHORT).show();
            Intent I = new Intent(UserType.this,SearchLocation.class);
            startActivity(I);

        });
    }
}