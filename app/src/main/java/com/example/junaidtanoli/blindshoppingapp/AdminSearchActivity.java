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

public class AdminSearchActivity extends AppCompatActivity {
    private Button search;
    private EditText inputText;
    private ListView searchlist;
    private String SearchInput;
private ProgressDialog loadingbar;
    public static final String MYPREFERNCES_Example="mysharedpref";
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
        setContentView(R.layout.activity_search);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        index=getIntent().getIntExtra("index",0);
        Toast.makeText(this, "here is index in search"+index, Toast.LENGTH_SHORT).show();
        username = getIntent().getStringExtra("name");
        address= getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");
        loadingbar=new ProgressDialog(AdminSearchActivity.this);
        loadingbar.setTitle("Please Wait");
        loadingbar.setMessage("Loading Data");
        loadingbar.setCanceledOnTouchOutside(false);
        thedata=new ArrayList<>();
        searchlist=findViewById(R.id.search_list);
adapter=new ShowHomeDataAdapter(AdminSearchActivity.this,thedata);
searchlist.setAdapter(adapter);
        search=(Button)findViewById(R.id.searchbtn);
        inputText=(EditText)findViewById(R.id.search_product_name);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchInput=inputText.getText().toString();
                ShowProductFromDatabase(SearchInput);
            }
        });
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
                    adapter=new ShowHomeDataAdapter(AdminSearchActivity.this,thedata);
                    searchlist.setAdapter(adapter);
                    loadingbar.hide();
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
        RequestQueue queue = Volley.newRequestQueue(AdminSearchActivity.this);
        queue.add(jsonArrayRequest);
    }
}
