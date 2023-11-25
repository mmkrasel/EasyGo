package com.easyGo.easygo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SensorService.ACTION_ACCELEROMETER_DATA)) {
            // Extract the data from the intent
            float[] floatArray = intent.getFloatArrayExtra(SensorService.EXTRA_ACCELEROMETER_DATA);
            System.out.println(floatArray[0]+" MAIn");
            // Update UI or perform necessary actions based on the received data
        }
    }
}

