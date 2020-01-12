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
import com.example.junaidtanoli.blindshoppingapp.Adapters.showalluseradapter;
import com.example.junaidtanoli.blindshoppingapp.DataClasses.showhomedata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ViewUsers extends AppCompatActivity {
    //  end speech
    ListView listView;
    ArrayList<showhomedata> thedata;
    ArrayAdapter<showhomedata> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewallusers);
        thedata=new ArrayList<>();
        listView=findViewById(R.id.viewallusers);
        ShowProductFromDatabase("ahmed");
    }
    private void ShowProductFromDatabase(final String itemname) {
        String url="https://www.studentfyp.com/blindshoppingapp/viewallusers.php";
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ViewUsers.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject data=new JSONObject(response);
                    JSONArray allproducts=data.getJSONArray("result");
                    for (int j = 0; j < allproducts.length(); j++) {
                        JSONObject type = allproducts.getJSONObject(j);
                        String name = type.getString("name");
                       int id = type.getInt("id");
                        thedata.add(new showhomedata(name,id));
                    }
                    adapter=new showalluseradapter(ViewUsers.this,thedata);
                    listView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG", error.toString());
            }
        });
        RequestQueue queue = Volley.newRequestQueue(ViewUsers.this);
        queue.add(jsonArrayRequest);
    }
}
