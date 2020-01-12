package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junaidtanoli.blindshoppingapp.Prevalent.Prevalent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Updateuserprofile extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private EditText editphone, editpassward;
    private Button loginbtn;
    String phone,password;
    String result=Comands.PHONE;

    private final int REQ_CODE = 100;

    private TextView admin,notadmin,createAccount,welcome_text;
private ProgressDialog loadinBar;
private String parentDbName="Users";
Bundle bundle;
    // speech
    private TextToSpeech tts;
    private boolean initialized;
String name,address;
    private String queuedText;
    private String TAG = "TTS";
    //  end speech
    public void init(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_updateprofile);
bundle=new Bundle();
        name=getIntent().getStringExtra("name");
        phone=getIntent().getStringExtra("phone");
        address=getIntent().getStringExtra("address");


        loadinBar=new ProgressDialog(this);

        init();
        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
        speak("Do you want to Update Or Close");
        promptSpeechInput();
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
            tts.setLanguage(Locale.ENGLISH);

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
                    Updateuserprofile.this.runOnUiThread(new runnable()
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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.ENGLISH);
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
                if("Update".equalsIgnoreCase(input)){
                    updateOnlyUserInfo(name,phone,address);
                    Intent intent=new Intent(Updateuserprofile.this,MainActivity.class);
                    speak("Profile updated successfully");
                    startActivity(intent);
         }
                else if("close".equalsIgnoreCase(input)){
                    Intent intent=new Intent(Updateuserprofile.this,MainActivity.class);
                    speak("Closed");
                    startActivity(intent);
                }else
                {
                    speak("Say Update or Close");
                }

            }
        }
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
        }
        //  speach recognition code stops
    }
    private void updateOnlyUserInfo(final String name1,final String phone1,final String address1)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", name1);
        userMap. put("address", phone1);
        userMap. put("phoneOrder",address1);
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
        startActivity(new Intent(Updateuserprofile.this,HomeActivity.class));
        Toast.makeText(Updateuserprofile.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }
}