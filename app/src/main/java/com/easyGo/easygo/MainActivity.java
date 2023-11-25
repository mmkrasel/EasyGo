package com.easyGo.easygo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_CUSTOM_BROADCAST = "rijve.shovon.easygo.CUSTOM_BROADCAST";
    private Button letsGo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        letsGo = findViewById(R.id.letsGo);

        letsGo.setOnClickListener(v -> {
            Intent I = new Intent(MainActivity.this,UserType.class);
            startActivity(I);
            finish();
        });

    }
}