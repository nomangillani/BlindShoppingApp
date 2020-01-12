package com.example.junaidtanoli.blindshoppingapp;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.junaidtanoli.blindshoppingapp.Adapters.showallordersadapter;
import com.example.junaidtanoli.blindshoppingapp.DataClasses.showallordersclass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

public class ViewHistory extends AppCompatActivity {
    private ListView ordelist;
    ArrayList<showallordersclass> thedata;
    ArrayAdapter<showallordersclass> adapter;
    ListView searchlist;
    ProgressDialog loadingBar;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences.Editor editor ;
    SharedPreferences pref;
    ProgressDialog progressDialog;
    int userid;
    String name,username,description,quantity,contact,address,price,image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        userid=pref.getInt("useid",0);
        Toast.makeText(this, ""+userid, Toast.LENGTH_SHORT).show();
        loadingBar=new ProgressDialog(ViewHistory.this);
        loadingBar.setMessage("Delivering Order. . .");
        loadingBar.setCanceledOnTouchOutside(false);
   searchlist=findViewById(R.id.search_list);
        progressDialog=new ProgressDialog(ViewHistory.this);
        thedata=new ArrayList<>();
        ordelist=(ListView) findViewById(R.id.Order_list);
        ShowProductFromDatabase(userid);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    private void ShowProductFromDatabase(final int userid) {
        loadingBar.show();
        String url="http://www.studentfyp.com/blindshoppingapp/viewhistory.php";
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);
                    JSONArray allproducts=data.getJSONArray("result");
                    for (int j = 0; j < allproducts.length(); j++) {
                        JSONObject type = allproducts.getJSONObject(j);
                        int oid=type.getInt("oid");
                        name = type.getString("name");
                        description = type.getString("description");
                        price = type.getString("price");
                        image = type.getString("image");
                        thedata.add(new showallordersclass(oid,name,description,price,image,0));
                    }
                    adapter=new showallordersadapter(ViewHistory.this,thedata);
                    ordelist.setAdapter(adapter);
                    loadingBar.hide();
                } catch (Exception e) {
                    e.printStackTrace();
                    loadingBar.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewHistory.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LOG", error.toString());
                loadingBar.hide();
            }
        })
            {
                @Override
                protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("userid",new LoginSessionManager(getApplicationContext()).getUserKey());
                return params;
            }
        }
                ;
        RequestQueue queue = Volley.newRequestQueue(ViewHistory.this);
        queue.add(jsonArrayRequest);
    }
}
