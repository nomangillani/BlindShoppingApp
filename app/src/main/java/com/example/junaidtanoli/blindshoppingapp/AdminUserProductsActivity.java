package com.example.junaidtanoli.blindshoppingapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class AdminUserProductsActivity extends AppCompatActivity {
private RecyclerView productList;
RecyclerView.LayoutManager layoutManager;
ListView product_list;
private String UserId="";
ArrayList<showhomedata> thedata;
ArrayAdapter<showhomedata> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);
product_list=findViewById(R.id.product_list);
      //  UserId=getIntent().getStringExtra("uid");
thedata=new ArrayList<>();
        alldata();
   //     cartListref=FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(UserId).child("Products");
    }
    public void alldata(){
        String url="https://www.studentfyp.com/blindshoppingapp/showallitems.php";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(AdminUserProductsActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                JSONObject data = null;
                try {
                    data = new JSONObject(response);
                    JSONArray jsonArray=data.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject object= (JSONObject) jsonArray.get(i);
                       String desc,price,image;
                       int productid;
                        String name=object.getString("name");
                        desc=object.getString("description");
                        price=object.getString("price");
                        image=object.getString("image");
                        productid=object.getInt("id");
                        thedata.add(new showhomedata(name,desc,price,image,productid));
                    }
                    adapter=new ShowHomeDataAdapter(AdminUserProductsActivity.this,thedata);
                    product_list.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminUserProductsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(AdminUserProductsActivity.this);
        queue.add(request);
    }
}
