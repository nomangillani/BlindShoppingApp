package com.example.junaidtanoli.blindshoppingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

public class MediaButtonReciever extends BroadcastReceiver {

    private static long prevTime;
    private static boolean isSingleCall = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                Toast.makeText(context, "your click eon media button ina dnroi daja" +
                        "", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
