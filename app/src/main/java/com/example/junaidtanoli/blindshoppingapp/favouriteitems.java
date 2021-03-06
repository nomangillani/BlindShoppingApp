package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.junaidtanoli.blindshoppingapp.Adapters.ShowHomeDataAdapter;
import com.example.junaidtanoli.blindshoppingapp.DataClasses.showhomedata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class favouriteitems extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Button search;
    private EditText inputText;
    private ListView searchlist;
    private String SearchInput;
private ProgressDialog loadingbar;
    public static final String MYPREFERNCES_Example = "mysharedpref";
    SharedPreferences.Editor editor ;
    SharedPreferences pref;
    ArrayList<showhomedata> thedata;
ArrayAdapter<showhomedata> adapter;
    String description,price,image,name;
    // speech
    private TextToSpeech tts;
    private boolean initialized;
    private String queuedText;
    private String TAG = "TTS";
int id;
    int sideBar=0;
    int index=0;
String username,phone,address;
    //  end speech
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favouratitem);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        index=getIntent().getIntExtra("index",0);
        Toast.makeText(this, "here is index in search"+index, Toast.LENGTH_SHORT).show();
        username = getIntent().getStringExtra("name");
        address= getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");
        loadingbar=new ProgressDialog(favouriteitems.this);
        loadingbar.setTitle("Please Wait");
        loadingbar.setMessage("Loading Data");
        loadingbar.setCanceledOnTouchOutside(false);
        thedata=new ArrayList<>();
        searchlist=findViewById(R.id.search_list);
adapter=new ShowHomeDataAdapter(favouriteitems.this,thedata);
searchlist.setAdapter(adapter);
searchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(favouriteitems.this,ProductDetailsActivity.class);
        startActivity(intent);
    }
});


        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
    //    speak("Which products do you want to search like Caps,T shirts or Glasses");
    ShowProductFromDatabase(3);
    }
    @Override
    protected void onStart() {
        super.onStart();
        //nextProduct();
    }
    //   speak starts
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
                    favouriteitems.this.runOnUiThread(new favouriteitems.runnable()
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
                 if ("Show details".equalsIgnoreCase(input)||("show detail").equalsIgnoreCase(input)){
                    Intent intent = new Intent(favouriteitems.this, ProductDetailsActivity.class);
                    intent.putExtra("name",thedata.get(index).getName());
                    intent.putExtra("desc",thedata.get(index).getDesc());
                    intent.putExtra("price",thedata.get(index).getPrice());
                    intent.putExtra("image",thedata.get(index).getImage());
                    intent.putExtra("id",thedata.get(index).getId());
                    editor.putInt("nextproductindex",index);
                    editor.commit();
                    editor.apply();
                    // Submitorder(thedata.get(index).getName(),thedata.get(index).getDesc(),thedata.get(index).getPrice(),thedata.get(index).getImage(),phone,address,input);
                    startActivity(intent);
                }
                else if ("back".equalsIgnoreCase(input)){
                    Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                   /* index++;
                    nextProduct();*/

                  //  speak("How many Poducts Do you want to buy");
                }
//                  else if(input.equals("1")||input.equals("2")||input.equals("3")||input.equals("4")||input.equals("5")||input.equals("6")||input.equals("7")||input.equals("8"))
//                {
//                    Submitorder(thedata.get(index).getName(),thedata.get(index).getDesc(),thedata.get(index).getPrice(),thedata.get(index).getImage()
//                            ,username,phone,address,input);
//
//                }
                else{
                    speak("please say only View or Skip.");
                }
            }
        }
    }
    public  void nextProduct()
    {
        if(thedata.size()>index) {
            speak(thedata.get(index++).getDesc()+" with "+thedata.get(index++).getPrice()+" rupees  add to cart,  or go back");
        }
        else
        {
            Toast.makeText(this, "not valide", Toast.LENGTH_SHORT).show();
        }
    }
    private void ShowProductFromDatabase(final int userid) {
        loadingbar.show();
        String url="https://www.studentfyp.com/blindshoppingapp/searchfavouriteitem.php";
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(favouriteitems.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject data=new JSONObject(response);
                    JSONArray allproducts=data.getJSONArray("result");
                    for (int j = 0; j < allproducts.length(); j++) {
                        JSONObject type = allproducts.getJSONObject(j);
                         name = type.getString("name");
                         description = type.getString("description");
                         price = type.getString("price");
                        image = type.getString("image");
                        id = type.getInt("id");
                        thedata.add(new showhomedata(name,description,price,image,id));
                    }
                    adapter=new ShowHomeDataAdapter(favouriteitems.this,thedata);
                    searchlist.setAdapter(adapter);
                    loadingbar.hide();

                    speak(thedata.get(0).getDesc()+"with"+thedata.get(0).getPrice()+"Say Show details or Skip");
                    Toast.makeText(favouriteitems.this, ""+thedata.get(0).getDesc()+"with"+thedata.get(0).getPrice(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    loadingbar.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG", error.toString());
                loadingbar.hide();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid",""+new LoginSessionManager(getApplicationContext()).getUserKey());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(favouriteitems.this);
        queue.add(jsonArrayRequest);
    }
    private void Submitorder(final String itemname,final String desc,final String price,final String image ,final String username,final String phoneNo, final String address,final String quantity) {
        String url="https://www.studentfyp.com/blindshoppingapp/ordersnow.php";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(favouriteitems.this, ""+response, Toast.LENGTH_SHORT).show();
                JSONObject data= null;
                try {
                    data = new JSONObject(response);
                    String success=data.getString("success");
                    if(success.equals("1"))
                    {
                        loadingbar.hide();
                        Intent intent=new Intent(favouriteitems.this,HomeActivity.class);
                        startActivity(intent);

                    }
                    else
                    {
                        Intent intent=new Intent(favouriteitems.this,HomeActivity.class);
                        startActivity(intent);
                        loadingbar.show();
                        Toast.makeText(favouriteitems.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    loadingbar.hide();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingbar.hide();
                Toast.makeText(favouriteitems.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",itemname);
                params.put("ccontact",phoneNo);
                params.put("cname",username);
                params.put("description",desc);
                params.put("price",price);
                params.put("quantity",quantity);
                params.put("image",image);
                params.put("address",address);
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(favouriteitems.this);
        queue.add(request);
    }

}
