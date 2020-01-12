package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junaidtanoli.blindshoppingapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.OkHttpClient;

public class ConfirmFinalOrders extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private Button Register,gotologin;
    private EditText username,phone,passward;
    private ProgressDialog loadingBar;
    Bundle bundle;
    Thread t;

    private EditText nameEdittext,phoneEdittext,addressEdittext,cityEdittext;
    private Button confirmbtn,priceCheck;

    private String TotalAmount="";
    private OkHttpClient mClient = new OkHttpClient();
    private Context mContext;
    TextToSpeech tts;
    EditText et_to,et_subject,et_body;
    String result=Comands.NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        TotalAmount=getIntent().getStringExtra("Total Price");
        Toast.makeText(this,"Total price="+TotalAmount,Toast.LENGTH_SHORT)
                .show();
        confirmbtn=(Button)findViewById(R.id.confirmfinalOrder);
        nameEdittext=(EditText)findViewById(R.id.shipmentName);
        phoneEdittext=(EditText)findViewById(R.id.ShipmentPhone);
        addressEdittext=(EditText)findViewById(R.id.yourHomeAddress);
        cityEdittext=(EditText)findViewById(R.id.yourCity);
        loadingBar=new ProgressDialog(this);
        tts = new TextToSpeech(this,this);
        bundle = new Bundle();
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
    private final int REQ_CODE = 100;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if(this.result != Comands.SMS_CONFIRM)
                        fill(result.get(0));
                    else
                    {

                    }
                    // confirm(result.get(0));
                }
                else if(resultCode == RESULT_CANCELED)
                {
                    finish();
                }
                break;
            }

        }
    }

    boolean fill = false;
    private void fill(String result) {
        switch (this.result){
            case Comands.NAME:
                bundle.putString(Comands.NAME, result +
                        "");
                nameEdittext.setText(result);
                if(!fill)
                    this.result = Comands.ADDRESS;
                else
                    this.result = Comands.ADDRESS;
                ask_fill();
                break;
            case Comands.ADDRESS:
                bundle.putString(Comands.ADDRESS,result);
                addressEdittext.setText(result);
                if(!fill)
                    this.result = Comands.PHONE;
                else
                    this.result = Comands.PHONE;
                ask_fill();
                break;
            case Comands.PHONE:
                bundle.putString(Comands.PHONE,result);
                phoneEdittext.setText(result);
                if(!fill)
                    this.result = Comands.CITY;
                else
                    this.result = Comands.CITY;
                ask_fill();
                break;

            case Comands.CITY:
                bundle.putString(Comands.CITY,result);
                cityEdittext.setText(result);
                if(!fill)
                    this.result = Comands.CREATE;
                else
                    this.result = Comands.CREATE;
                ask_fill();
                break;

            case Comands.CREATE:

                try{

                    Intent intent=new Intent(ConfirmFinalOrders.this,HomeActivity.class);
                   check();
                    startActivity(intent);
//                    SmsManager smgr = SmsManager.getDefault();
//                    smgr.sendTextMessage(et_to.getText().toString(),null,et_body.getText().toString(),null,null);
//                    Toast.makeText(SignupActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(ConfirmFinalOrders.this, "Some error occured while creating account", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void speakOut(String text) {


        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void ask_fill()  {
        switch (result){
            case Comands.NAME:
                speakOut(Comands.NAME);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;
            case Comands.ADDRESS:
                speakOut(Comands.ADDRESS);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;

            case Comands.PHONE:
                speakOut(Comands.PHONE);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;
            case Comands.CITY:
                speakOut(Comands.CITY);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;
            case Comands.CREATE:
                try{
                    Intent intent=new Intent(ConfirmFinalOrders.this,HomeActivity.class);
                 check();
                    startActivity(intent);
                }
                catch (Exception e){
                }



                break;
        }
    }

    boolean a =false;
    @Override
    protected void onPostResume() {
        super.onPostResume();
        a=true;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //bl.setEnabled(true);
                speakOut("");
                while (!a);
                ask_fill();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
    @Override
    public void onDestroy() {
        // Shuts Down TTS
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();

    }

    private void check() {
        if (TextUtils.isEmpty(nameEdittext.getText().toString())){
            Toast.makeText(this,"please provide your full name.",Toast.LENGTH_SHORT).show();

        }
        else  if (TextUtils.isEmpty(addressEdittext.getText().toString())){
            Toast.makeText(this,"please provide your full Address.",Toast.LENGTH_SHORT).show();

        }
        else  if (TextUtils.isEmpty(phoneEdittext.getText().toString())){
            Toast.makeText(this,"please provide your phone.",Toast.LENGTH_SHORT).show();

        }
        else  if (TextUtils.isEmpty(cityEdittext.getText().toString())){
            Toast.makeText(this,"please provide your city name.",Toast.LENGTH_SHORT).show();

        }
        else {
            confirmOrder();

        }
    }

    private void confirmOrder() {
        final String saveCurrentDate,saveCurrentTime;

        Calendar callforDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate=currentDate.format(callforDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(callforDate.getTime());

        final DatabaseReference orderRef=FirebaseDatabase.getInstance().getReference().child("Order Details").child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String,Object>orderMap=new HashMap<>();
        orderMap.put("totalAmount",TotalAmount);
        orderMap.put("name",nameEdittext.getText().toString());
        orderMap.put("phone",phoneEdittext.getText().toString());
        orderMap.put("address",addressEdittext.getText().toString());
        orderMap.put("time",saveCurrentTime);
        orderMap.put("city",cityEdittext.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("state","not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().child("List View").child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrders.this,"yout final order is placed successfully",Toast.LENGTH_LONG).show();

                                        Intent intent=new Intent(ConfirmFinalOrders.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });


    }

}
