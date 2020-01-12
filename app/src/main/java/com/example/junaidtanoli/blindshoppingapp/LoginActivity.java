package com.example.junaidtanoli.blindshoppingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import io.paperdb.Paper;
import static android.text.TextUtils.isEmpty;
import static java.util.Locale.US;

public class LoginActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private EditText editphone, editpassward;

    private Button loginbtn;
    private TextView admin, notadmin, createAccount, welcome_text;
    private ProgressDialog loadinBar;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences.Editor editor ;
    SharedPreferences pref;
    private String parentDbName = "Users";
    SessionManager sessionManager;
String phone,pass,username,address;
    String result=Comands.PHONE;    // speech
    private boolean initialized;
    private String queuedText;
    int success;

    private TextToSpeech tts;
    private final int REQ_CODE = 100;
    private String TAG = "TTS";
    Bundle bundle;
    Thread t;
    public void init() {
        /*welcome_text = (TextView) findViewById(R.id.textView);
        admin = (TextView) findViewById(R.id.admin);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbtn.setText("Login Admin");
                admin.setVisibility(View.INVISIBLE);
                notadmin.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });*/
        /*notadmin = (TextView) findViewById(R.id.notAdmin);
        notadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbtn.setText("Login");
                admin.setVisibility(View.VISIBLE);
                notadmin.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });*/
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
sessionManager=new SessionManager(LoginActivity.this);
        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
        speak("Please Enter Phone Or Password");
        bundle=new Bundle();
        editpassward = (EditText) findViewById(R.id.editTextPassward);
        editphone = (EditText) findViewById(R.id.editTextPhone);
        loadinBar = new ProgressDialog(this);
        loadinBar.setTitle("Login Account");
        loadinBar.setMessage("please wait while we are checking the credentials");
        loadinBar.setCanceledOnTouchOutside(false);
        loginbtn = (Button) findViewById(R.id.Login);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AllowAccesstoAccount(editphone.getText().toString(),editpassward.getText().toString());
            }
        });

    }
    private void loginUser() {

        phone = editphone.getText().toString();
        pass = editpassward.getText().toString();

        if (isEmpty(phone)) {
            editphone.setError("please enter phone");
        } else if (isEmpty(pass)) {
            editpassward.setError("please enter passward");
        } else {
            loadinBar.setTitle("Login Account");
            loadinBar.setMessage("please wait while we are checking the credentials");
            loadinBar.setCanceledOnTouchOutside(false);
            loadinBar.show();
AllowAccesstoAccount(phone,pass);
        }
    }
    private void AllowAccesstoAccount(final String phone, final String pass) {
        loadinBar.show();
        String url = "http://www.studentfyp.com/blindshoppingapp/Loginuser.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadinBar.hide();
                try {
                    Toast.makeText(LoginActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    JSONObject data = new JSONObject(response);
                    success = data.getInt("success");
                    String namee=data.getString("name");
                    String blockedd=data.getString("blocked");
                    String phonee=data.getString("phone");
                    String idd=data.getString("id");
                    if(success==1){
                        LoginSessionManager loginSessionManager=new LoginSessionManager(getApplicationContext());
                        loginSessionManager.createLoginSession(namee,phone,idd);
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                    //Toast.makeText(LoginActivity.this, ""+success, Toast.LENGTH_SHORT).show();
                   else  if(success==0){
                        startActivityForResult(getIntent(),200);
                        //ConvertTextToSpeech("Please Enter Valid Number");
                        //ask_fill();
                        //promptSpeechInput();
                        /*boolean fill = false;
                        fill(result);*/
                        speak("Please Enter Contact and password again");

                        //Toast.makeText(LoginActivity.this, "Conditino success"+success, Toast.LENGTH_SHORT).show();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    loadinBar.hide();
                }

                }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                loadinBar.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pass",pass);
                params.put("phone",phone);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);

}


    private void ConvertTextToSpeech(String text) {
        // TODO Auto-generated method stub

        if(text==null||"".equals(text))
        {
            text = "Well come to Blind Shoping App do you want to sign up or login";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, US);
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

                    if(this.result != Comands.NAME)
                        fill(result.get(0));
                    else
                    {
speak("please Enter again");
                    }
                    // confirm(result.get(0));
                }
                /*else if(resultCode == RESULT_CANCELED)
                {
                    finish();
                }
                break;*/
            }

        }
    }

    boolean fill = false;
    private void fill(String result) {
        switch (this.result){
            case Comands.PHONE:
                bundle.putString(Comands.PHONE,result);
                editphone.setText(result);
                if(!fill)
                    this.result = Comands.PASSWORD;
                else
                    this.result = Comands.PASSWORD;
                ask_fill();
                break;
            case Comands.PASSWORD:
                bundle.putString(Comands.PASSWORD,result);
                editpassward.setText(result);
                if(!fill)
                    this.result = Comands.CREATE;
                else
                    this.result = Comands.CREATE;
                ask_fill();
                break;
            case Comands.CREATE:
                try{
          loginUser();
                }
                catch (Exception e){
                    Toast.makeText(LoginActivity.this, "Some error occured while creating account", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    private void ask_fill()  {
        switch (result){
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
             loginUser();
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
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, US);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "");
        try {
            startActivityForResult(intent, REQ_CODE);
        } catch (ActivityNotFoundException a) {
        }
        //  speach recognition code stops
    }
    //  speach recognition code stops


}