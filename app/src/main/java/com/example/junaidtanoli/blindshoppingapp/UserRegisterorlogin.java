package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.paperdb.Paper;

public class UserRegisterorlogin extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private Button joinNowbutton, loginbutton;
    private ProgressDialog loadingBar;
    private TextView welcome_text;

    // speech
    private TextToSpeech tts;
    private boolean initialized;
    private String queuedText;
    private String TAG = "TTS";
    private ProgressDialog loadinBar;
    //  end speech
    public void init(){

        welcome_text=(TextView)findViewById(R.id.textView);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(UserRegisterorlogin.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        joinNowbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(UserRegisterorlogin.this,SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowbutton=findViewById(R.id.main_join_now_btn);
        loginbutton=findViewById(R.id.main_login_btn);
        loadingBar=new ProgressDialog(this);
        Paper.init(this);

    init();
        tts = new TextToSpeech(this, this, "com.google.android.tts");
       // tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
        speak("Welcome to blind shopping app Please Say Join Or Login");
    }
    //voice
    public void speak(String text) {

        if (!initialized) {
            queuedText = text;
            return;
        }
        queuedText = null;

        setTtsListener(); // no longer creates a new UtteranceProgressListener each time
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_ADD, map);
    }
    private void setTtsListener() {

    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            initialized = true;
            tts.setLanguage(Locale.US);

            if (queuedText != null) {
                speak(queuedText);
            }
        }

    }
    private abstract class runnable implements Runnable {
    }
    private UtteranceProgressListener mProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
        } // Do nothing

        @Override
        public void onError(String utteranceId) {
        } // Do nothing.

        @Override
        public void onDone(String utteranceId) {

            new Thread()
            {
                public void run()
                {
                    UserRegisterorlogin.this.runOnUiThread(new runnable()
                    {
                        public void run()
                        {
                            afterSpeaks();
                        }
                    });
                }
            }.start();

        }
    };
    void afterSpeaks(){
        Toast.makeText(getBaseContext(), "Speaks completed", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.US);
        try{
            startActivityForResult(intent,200);
        }catch (Exception e){
            Toast.makeText(this, "Intent Problem", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200){
            if(resultCode==RESULT_OK && data!=null){
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String input=result.get(0);
                if("Join".equalsIgnoreCase(input)){
                    Intent createAccount=new Intent(UserRegisterorlogin.this,SignupActivity.class);
                    startActivity(createAccount);
                    Toast.makeText(this, "Signup Activity open", Toast.LENGTH_SHORT).show();
                }
                else if ("Login".equalsIgnoreCase(input)){
                    Intent createAccount=new Intent(UserRegisterorlogin.this,LoginActivity.class);
                    startActivity(createAccount);
                    Toast.makeText(this, "Login Activity open", Toast.LENGTH_SHORT).show();
                }
                else{
                    speak("Which You want to go Join or Login");
                }
            }
        }
    }
    //  speach recognition code stops
}
