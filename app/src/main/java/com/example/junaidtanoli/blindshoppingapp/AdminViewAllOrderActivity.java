package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.junaidtanoli.blindshoppingapp.Adapters.ShowHomeDataAdapter;
import com.example.junaidtanoli.blindshoppingapp.Adapters.showallordersadapter;
import com.example.junaidtanoli.blindshoppingapp.DataClasses.showallordersclass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminViewAllOrderActivity extends AppCompatActivity {

    private ListView ordelist;
    ArrayList<showallordersclass> thedata;
    ArrayAdapter<showallordersclass> adapter;
    ListView searchlist;
    ProgressDialog loadingBar;

    ProgressDialog progressDialog;
    String name,username,description,quantity,contact,address,price,image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);
        loadingBar=new ProgressDialog(AdminViewAllOrderActivity.this);
        loadingBar.setMessage("Delivering Order. . .");
        loadingBar.setCanceledOnTouchOutside(false);
   searchlist=findViewById(R.id.search_list);
        progressDialog=new ProgressDialog(AdminViewAllOrderActivity.this);
        thedata=new ArrayList<>();
        ordelist=(ListView) findViewById(R.id.Order_list);
        ShowProductFromDatabase();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    private void ShowProductFromDatabase() {
        loadingBar.show();
        String url="http://www.studentfyp.com/blindshoppingapp/viewallorders.php";
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
                        thedata.add(new showallordersclass(oid,name,description,price,image));
                    }
                    adapter=new showallordersadapter(AdminViewAllOrderActivity.this,thedata);
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
                Toast.makeText(AdminViewAllOrderActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LOG", error.toString());
                loadingBar.hide();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(AdminViewAllOrderActivity.this);
        queue.add(jsonArrayRequest);
    }

}
