package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.OkHttpClient;

import static android.text.TextUtils.isEmpty;

public class SettingsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,Runnable {
private Button Register,gotologin;
private EditText username,phone,passward;
private ProgressDialog loadingBar;
    Bundle bundle;
    Thread t;
EditText fullNameEditText,userPhoneEditText,addressEditText;
    private OkHttpClient mClient = new OkHttpClient();
    private Context mContext;
    TextToSpeech tts;
    EditText et_to,et_subject,et_body;
    String result=Comands.NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name1);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone_number1);
        addressEditText = (EditText) findViewById(R.id.settings_address1);

        loadingBar=new ProgressDialog(this);
        tts = new TextToSpeech(this,this);
        t = new Thread(this);
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
                result = Util.mail(result);
                bundle.putString(Comands.NAME, result +
                        "");
         fullNameEditText.setText(result);
                if(!fill)
                    this.result = Comands.PHONE;
                else
                    this.result = Comands.PHONE;
                ask_fill();
                break;
            case Comands.PHONE:
                bundle.putString(Comands.PHONE,result);
                userPhoneEditText.setText(result);
                if(!fill)
                    this.result = Comands.PASSWORD;
                else
                    this.result = Comands.PASSWORD;
                ask_fill();
                break;
            case Comands.PASSWORD:
                bundle.putString(Comands.PASSWORD,result);
                addressEditText.setText(result);
                if(!fill)
                    this.result = Comands.CREATE;
                else
                    this.result = Comands.CREATE;
                ask_fill();
                break;

            case Comands.CREATE:

                try{

                    Intent intent=new Intent(SettingsActivity.this,Updateuserprofile.class);
                    intent.putExtra("name",fullNameEditText.getText().toString());
                    intent.putExtra("phone",userPhoneEditText.getText().toString());
                    intent.putExtra("address",addressEditText.getText().toString());
                    startActivity(intent);
//                    SmsManager smgr = SmsManager.getDefault();
//                    smgr.sendTextMessage(et_to.getText().toString(),null,et_body.getText().toString(),null,null);
//                    Toast.makeText(SignupActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(SettingsActivity.this, "Some error occured while creating account", Toast.LENGTH_SHORT).show();
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
            case Comands.PHONE:
                speakOut(Comands.PHONE);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;
            case Comands.PASSWORD:
                speakOut(Comands.PASSWORD);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;

            case Comands.CREATE:
                try{
Intent intent=new Intent(SettingsActivity.this,Updateuserprofile.class);
                    intent.putExtra("name",fullNameEditText.getText().toString());
                    intent.putExtra("phone",userPhoneEditText.getText().toString());
                    intent.putExtra("address",addressEditText.getText().toString());
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
    @Override
    public void run() {
        // fill();
    }
}
