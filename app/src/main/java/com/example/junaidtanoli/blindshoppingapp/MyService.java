package com.example.junaidtanoli.blindshoppingapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class MyService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        // register receiver that handles screen on and screen off logic
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new Receiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {
            // your code
        } else {
            // your code
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}