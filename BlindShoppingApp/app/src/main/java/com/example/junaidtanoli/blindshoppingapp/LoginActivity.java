package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junaidtanoli.blindshoppingapp.Model.Users;
import com.example.junaidtanoli.blindshoppingapp.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private EditText editphone, editpassward;
    private Button loginbtn;
    private TextView admin,notadmin,createAccount,welcome_text;
private ProgressDialog loadinBar;
private String parentDbName="Users";

    // speech
    private TextToSpeech tts;
    private boolean initialized;
    private String queuedText;
    private String TAG = "TTS";
    //  end speech
    public void init(){
        welcome_text=(TextView)findViewById(R.id.textView);
        admin=(TextView)findViewById(R.id.admin);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbtn.setText("Login Admin");
                admin.setVisibility(View.INVISIBLE);
                notadmin.setVisibility(View.VISIBLE);
                parentDbName="Admins";
            }
        });


        notadmin=(TextView)findViewById(R.id.notAdmin);
        notadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbtn.setText("Login");
                admin.setVisibility(View.VISIBLE);
                notadmin.setVisibility(View.INVISIBLE);
                parentDbName="Users";
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editpassward=(EditText) findViewById(R.id.editTextPassward);
        editphone=(EditText) findViewById(R.id.editTextPhone);
        loginbtn=(Button) findViewById(R.id.Login);


        loadinBar=new ProgressDialog(this);



        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();
            }
        });


        init();
        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
        speak(welcome_text.getText().toString()+" Please say admin, not admin or Create account");
}

    private void loginUser() {

        String phone=editphone.getText().toString();
        String pass=editpassward.getText().toString();

        if (isEmpty(phone)){
           editphone.setError("please enter phone");
        }


        else if (isEmpty(pass)){

            editpassward.setError("please enter passward");
        }
        else{
            loadinBar.setTitle("Login Account");
            loadinBar.setMessage("please wait while we are checking the credentials");
            loadinBar.setCanceledOnTouchOutside(false);
            loadinBar.show();

            AllowAccesstoAccount(phone,pass);
        }
    }
    private void AllowAccesstoAccount(final String phone, final String pass) {
        final DatabaseReference Rootref;
        Rootref=FirebaseDatabase.getInstance().getReference();
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users userdata=dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (userdata.getPhone().equals(phone)){
                        if (userdata.getPassward().equals(pass)){
                            if (parentDbName.equals("Admins")){
                                Toast.makeText(LoginActivity.this,"successful.",Toast.LENGTH_LONG).show();
                            loadinBar.dismiss();
                            Intent intent=new Intent(LoginActivity.this,AdminCategoryActivity.class);
                            startActivity(intent);
                            }else if(parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this,"successful.",Toast.LENGTH_LONG).show();
                            loadinBar.dismiss();
                            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                intent.putExtra("EXTRA_SESSION_ID", phone);
                                Prevalent.currentOnlineUser=userdata;
                            startActivity(intent);
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this,"This"+phone+"incorrect.",Toast.LENGTH_LONG).show();
                    loadinBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                    LoginActivity.this.runOnUiThread(new runnable()
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
                if("admin".equalsIgnoreCase(input)){

                    speak("please Enter Phone Number and Password ");

                    loginbtn.setText("Login Admin");
                    admin.setVisibility(View.INVISIBLE);
                    notadmin.setVisibility(View.VISIBLE);
                    parentDbName="Admins";


                    Toast.makeText(this, "welcome to Admin" , Toast.LENGTH_SHORT).show();
                }
                else if("not admin".equalsIgnoreCase(input)){


                    loginbtn.setText("Login");
                    admin.setVisibility(View.VISIBLE);
                    notadmin.setVisibility(View.INVISIBLE);
                    parentDbName="Users";
                    Toast.makeText(this, "welcome to user", Toast.LENGTH_SHORT).show();



                }else
                {
                    speak("Which You want to go admin or not admin");
                }
            }
        }
    }



    //  speach recognition code stops
}