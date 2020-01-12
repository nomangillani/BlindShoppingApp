package com.example.junaidtanoli.blindshoppingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Recever extends BroadcastReceiver {

    int Count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Count++;
            if (Count == 2) {
                Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(context,MainActivity.class);
                context.startActivity(i);
                //Send SMS code..
            }


        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(context,MainActivity.class);
            context.startActivity(i);
            //This is for screen ON option.

        }
    }
}