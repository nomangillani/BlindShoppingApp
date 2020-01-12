package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class CategoryActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private Button search;
    private EditText inputText;
    private ListView searchlist;
    private String SearchInput;
private ProgressDialog loadingbar;
UserCompleteDataArray thedataa;
public static showhomedata data2;
    public static final String MYPREFERNCES_Example="mysharedpref";
    SharedPreferences.Editor editor ;
    SharedPreferences pref;
    ArrayList<showhomedata> thedata;
 public static    ArrayList<showhomedata> thedata1;
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
        setContentView(R.layout.activity_search);
        thedataa=new UserCompleteDataArray();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        index=getIntent().getIntExtra("index",0);
        Toast.makeText(this, "here is index in search"+index, Toast.LENGTH_SHORT).show();
        username = getIntent().getStringExtra("name");
        address= getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");
        loadingbar=new ProgressDialog(CategoryActivity.this);
        loadingbar.setTitle("Please Wait");
        loadingbar.setMessage("Loading Data");
        loadingbar.setCanceledOnTouchOutside(false);
        thedata=new ArrayList<>();
        searchlist=findViewById(R.id.search_list);
adapter=new ShowHomeDataAdapter(CategoryActivity.this,thedata);
searchlist.setAdapter(adapter);
searchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(CategoryActivity.this,ProductDetailsActivity.class);
        startActivity(intent);
    }
});
        search=(Button)findViewById(R.id.searchbtn);
        inputText=(EditText)findViewById(R.id.search_product_name);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchInput=inputText.getText().toString();
                onStart();
            }
        });

        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
        //thedata.add(new showhomedata("Apple",description,price,image,id));


       /* thedataa.Storedata.add(new showhomedata("Stationary","1200","1200","dfdfa",1));
        thedataa.Storedata.add(new showhomedata("Electronic","1200","1200","dfdfa",1));
        thedataa.Storedata.add(new showhomedata("Garmetns","1200","1200","dfdfa",1));
        thedataa.Storedata.add(new showhomedata("Feet wear","1200","1200","dfdfa",1));
        thedataa.Storedata.add(new showhomedata("Hardware","1200","1200","dfdfa",1));

*/
        /*speak(thedataa.Storedata.get(0).getName()+",,"+thedataa.Storedata.get(1).getName()+",,"+thedataa.Storedata.get(2).getName()+",,"+thedataa.Storedata.get(3).getName()+",,"+thedataa.Storedata.get(4).getName());
   */
    speak("Following are the Categories like t_shirt,bags,laptops etc");}
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
                    CategoryActivity.this.runOnUiThread(new CategoryActivity.runnable()
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
                if(input.contains("caps") || input.contains("Caps")) {
                    inputText.setText("Caps");
                    SearchInput = "Caps";
                    ShowProductFromDatabase(SearchInput);
                    onStart();
                }
                  else if (input.contains("t-shirts")||input.contains("T-shirts"))
                {   inputText.setText("T shirts");
                    SearchInput="T shirts";
                    ShowProductFromDatabase(SearchInput);
                    onStart();
                }else if (input.contains("laptop")||input.contains("Laptops"))
                {   inputText.setText("Laptops");
                    SearchInput="Laptops";
                    ShowProductFromDatabase(SearchInput);
                    onStart();
                }
                  else if(input.contains("glasses") || input.contains("sun Glasses")){
                    inputText.setText("Glasses");
                    SearchInput="Glasses";
                    ShowProductFromDatabase(SearchInput);
                    onStart();
                  }
                else if(input.contains("Bags") || input.contains("bags")){
                    inputText.setText("Bags");
                    SearchInput="Glasses";
                    ShowProductFromDatabase(SearchInput);
                    onStart();
                }
                else if ("show details".equalsIgnoreCase(input)||"show detail".equalsIgnoreCase(input)){
                    Intent intent = new Intent(CategoryActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("name",thedata.get(index).getName());
                    intent.putExtra("desc",thedata.get(index).getDesc());
                    intent.putExtra("price",thedata.get(index).getPrice());
                    intent.putExtra("image",thedata.get(index).getImage());
                    intent.putExtra("id",thedata.get(index).getId());
                    editor.putInt("nextproductindex",index);
                    editor.commit();
                    editor.apply();
                    startActivity(intent);
                }
                else if ("Skip".equalsIgnoreCase(input)){
                    index++;
                    nextProduct();

                  //  speak("How many Poducts Do you want to buy");
                }
//                  else if(input.equals("1")||input.equals("2")||input.equals("3")||input.equals("4")||input.equals("5")||input.equals("6")||input.equals("7")||input.equals("8"))
//                {
//                    Submitorder(thedata.get(index).getName(),thedata.get(index).getDesc(),thedata.get(index).getPrice(),thedata.get(index).getImage()
//                            ,username,phone,address,input);
//
//                }
                else{
                    speak("please say only show detail or Skip.");
                }
            }
        }
    }
    public  void nextProduct()
    {
        if(thedata.size()>index) {
            speak(thedata.get(index).getDesc()+" with "+thedata.get(index).getPrice()+" rupees  show detail or Skip");
        }
        else
        {
            Toast.makeText(this, "not valide", Toast.LENGTH_SHORT).show();
        }
    }
    private void ShowProductFromDatabase(final String itemname) {
        loadingbar.show();
        String url="https://www.studentfyp.com/blindshoppingapp/searchitems.php";
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                    adapter=new ShowHomeDataAdapter(CategoryActivity.this,thedata);
                    searchlist.setAdapter(adapter);
                    loadingbar.hide();

                    speak(thedata.get(0).getDesc()+"with"+thedata.get(0).getPrice()+"Say Show details or Skip");
                    Toast.makeText(CategoryActivity.this, ""+thedata.get(0).getDesc()+"with"+thedata.get(0).getPrice(), Toast.LENGTH_SHORT).show();

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
                params.put("name",itemname);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(CategoryActivity.this);
        queue.add(jsonArrayRequest);
    }
    private void Submitorder(final String itemname,final String desc,final String price,final String image ,final String username,final String phoneNo, final String address,final String quantity) {
        String url="https://www.studentfyp.com/blindshoppingapp/ordersnow.php";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(CategoryActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                JSONObject data= null;
                try {
                    data = new JSONObject(response);
                    String success=data.getString("success");
                    if(success.equals("1"))
                    {/*
                        loadingbar.hide();
                        Intent intent=new Intent(CategoryActivity.this,LoginActivity.class);
                        startActivity(intent);*/

                    }
                    else
                    {
                        loadingbar.show();
                        Toast.makeText(CategoryActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CategoryActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

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
        RequestQueue queue= Volley.newRequestQueue(CategoryActivity.this);
        queue.add(request);
    }





}
