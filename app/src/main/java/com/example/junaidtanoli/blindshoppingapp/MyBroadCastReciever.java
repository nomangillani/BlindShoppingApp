package com.example.junaidtanoli.blindshoppingapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadCastReciever extends BroadcastReceiver {
    static int countPowerOff = 0;
    private Activity activity = null;

    public MyBroadCastReciever(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("onReceive", "Power button is pressed.");

        Toast.makeText(context, "power button clicked", Toast.LENGTH_LONG)
                .show();
        Intent i=new Intent(context,MainActivity.class);
        context.startActivity(i);

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            countPowerOff++;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Intent ii = new Intent(activity, MainActivity.class);
            activity.startActivity(ii);
            if (countPowerOff == 2) {

            }
        }
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent ii = new Intent(activity, MainActivity.class);
            activity.startActivity(ii);
            countPowerOff++;
        }

    }
}