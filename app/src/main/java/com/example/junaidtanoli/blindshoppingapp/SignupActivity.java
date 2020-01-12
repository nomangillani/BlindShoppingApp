package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import static android.text.TextUtils.isEmpty;

public class SignupActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,Runnable{
private Button Register,gotologin;
private EditText username,phone,passward,useraddress;
private ProgressDialog loadingBar;
    TextToSpeech tts;
    String result=Comands.NAME;
String phoneNo,name,pass,address;
    private boolean initialized;
    private final int REQ_CODE = 100;
    private String queuedText;
    private String TAG = "TTS";

    Bundle bundle;
    Thread t;
    EditText et_to,et_subject,et_body;
      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        bundle=new Bundle();

        username=(EditText)findViewById(R.id.editname);
        phone=(EditText)findViewById(R.id.edittextphone);
        passward=(EditText)findViewById(R.id.edittextpassward);
          useraddress=(EditText)findViewById(R.id.edittextaddress);
        Register=(Button) findViewById(R.id.Register);
        loadingBar=new ProgressDialog(this);
        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
              Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }
    private void CreateAccount() {

         name=username.getText().toString();
        address=useraddress.getText().toString();
        phoneNo=phone.getText().toString();
        pass=passward.getText().toString();
        if (isEmpty(name)) {
            username.setError("please enter  Name");
        }
       else if (isEmpty(phoneNo)){
            phone.setError("please enter phone Number");
        }
       else if (isEmpty(pass)){
            passward.setError("please enter passward");
        }
        else if (isEmpty(address)){
            useraddress.setError("please enter address");
        }
        else{
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            RegisterUser(name,phoneNo,pass,address);
        }
    }
    private void RegisterUser(final String name, final String phoneNo, final String pass,final String address) {
        String url="https://www.studentfyp.com/blindshoppingapp/register.php";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(SignupActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                JSONObject data= null;
                try {
                    data = new JSONObject(response);
                    String success=data.getString("success");
                    if(success.equals("1"))
                    {
                        loadingBar.hide();
                        Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                        startActivity(intent);

                    }
                    else
                    {
                        loadingBar.show();
                        Toast.makeText(SignupActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    loadingBar.hide();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.hide();
                Toast.makeText(SignupActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",name);
                params.put("pass",pass);
                params.put("phone",phoneNo);
                params.put("address",address);
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(SignupActivity.this);
        queue.add(request);
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
    public void onPointerCaptureChanged(boolean hasCapture) {

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
                    SignupActivity.this.runOnUiThread(new runnable()
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(this.result != Comands.CREATE)
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
                bundle.putString(Comands.NAME, result+"");
                username.setText(result);
                if(!fill)
                    this.result = Comands.PHONE;
                else
                    this.result = Comands.PHONE;
                ask_fill();
                break;
            case Comands.PHONE:
                bundle.putString(Comands.PHONE,result);
                phone.setText(result);
                if(!fill)
                    this.result = Comands.PASSWORD;
                else
                    this.result = Comands.PASSWORD;
                ask_fill();
                break;
            case Comands.PASSWORD:
                bundle.putString(Comands.PASSWORD,result);
                passward.setText(result);
                if(!fill)
                    this.result = Comands.ADDRESS;
                else
                    this.result = Comands.ADDRESS;
                ask_fill();
                break;
            case Comands.ADDRESS:
                bundle.putString(Comands.ADDRESS,result);
                useraddress.setText(result);
                if(!fill)
                    this.result = Comands.CREATE;
                else
                    this.result = Comands.CREATE;
                ask_fill();
                break;
            case Comands.CREATE:
                try{
                    CreateAccount();
                }
                catch (Exception e){
                    Toast.makeText(SignupActivity.this, "Some error occured while creating account", Toast.LENGTH_SHORT).show();
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
            case Comands.ADDRESS:
                speakOut(Comands.ADDRESS);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;
            case Comands.CREATE:
                try{
                 CreateAccount();
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
}
