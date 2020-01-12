package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class Updateadress extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private EditText editaddress, editpassward;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private Button loginbtn;
    int userid;
    private TextView admin, notadmin, createAccount, welcome_text;
    private ProgressDialog loadinBar;
    public static final String MYPREFERNCES_Example = "mysharedpref";
    SharedPreferences.Editor editor ;
    SharedPreferences pref;
    private String parentDbName = "Users";
    SessionManager sessionManager;
String phone,pass,username,address;
    String result=Comands.ADDRESS;    // speech
  ///////////////////  Data Declared for speach input  ////////////
  private boolean initialized;
    private String queuedText;
    private TextToSpeech tts;
    private final int REQ_CODE = 100;
    private String TAG = "TTS";
    Bundle bundle;
    Thread t;
   /////////////////////////////
    public void init() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateaddress);

        init();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        userid=pref.getInt("userid",0);
sessionManager=new SessionManager(Updateadress.this);
        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
        bundle=new Bundle();
        editaddress = (EditText) findViewById(R.id.editaddress);
        loadinBar = new ProgressDialog(this);
        loadinBar.setTitle("Updating Address");
        loadinBar.setMessage("please wait while we are checking the credentials");
        loadinBar.setCanceledOnTouchOutside(false);
        loginbtn = (Button) findViewById(R.id.update);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Updateadress.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
    private void updateaddress() {
        phone = editaddress.getText().toString();
        if (isEmpty(phone)) {
            editaddress.setError("please enter address");
        } else {
            loadinBar.setTitle("Making your cart");
            loadinBar.setMessage("please wait while we are updating your address");
            loadinBar.setCanceledOnTouchOutside(false);
            loadinBar.show();
AllowAccesstoAccount(phone,userid);
        }
    }
    private void AllowAccesstoAccount(final String address, final int userid) {
        Toast.makeText(this, ""+pass+phone, Toast.LENGTH_SHORT).show();
        loadinBar.show();
        String url = "http://www.studentfyp.com/blindshoppingapp/updateaddress.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    String success = data.getString("success");
                    if (success.equals("1")) {
                        loadinBar.hide();
                        Intent intent = new Intent(Updateadress.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Updateadress.this, HomeActivity.class);
                        startActivity(intent);
                        loadinBar.hide();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadinBar.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Updateadress.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                loadinBar.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("address",address);
                params.put("userid",userid+"");
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(Updateadress.this);
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
                    Updateadress.this.runOnUiThread(new runnable()
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
            case Comands.ADDRESS:
                bundle.putString(Comands.ADDRESS,result);
                editaddress.setText(result);
                if(!fill)
                    this.result = Comands.CREATE;
                else
                    this.result = Comands.CREATE;
                ask_fill();
                break;
            case Comands.CREATE:
                try{
                    updateaddress();
                }
                catch (Exception e){
                    Toast.makeText(Updateadress.this, "Some error occured updating address", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    private void ask_fill()  {
        switch (result){
            case Comands.ADDRESS:
                speakOut(Comands.ADDRESS);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;
            case Comands.CREATE:
                try{
                    updateaddress();
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