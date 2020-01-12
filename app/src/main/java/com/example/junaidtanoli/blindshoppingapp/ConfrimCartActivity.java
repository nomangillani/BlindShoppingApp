package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.SubMenu;
import android.view.SurfaceHolder;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.junaidtanoli.blindshoppingapp.DataClasses.cartlist;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfrimCartActivity extends AppCompatActivity {
    ListView cartactivitylistview;
    public ProgressDialog loadinBar;
    ArrayList<theorder> theorderslist;
    DBHelper dbHelper;
    int userid,pid;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    cartlist cartlistobj;
    Cursor res;
    String formattedDate;
    SimpleDateFormat df;
    Date c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confrim_cart);
        c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

         df = new SimpleDateFormat("dd-MMM-yyyy");
         formattedDate = df.format(c);
        Context context;
        loadinBar=new ProgressDialog(ConfrimCartActivity.this);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        userid=pref.getInt("userid",0);
        pid=getIntent().getIntExtra("pid",0);
        loadinBar.setTitle("Checking Cart");
        theorderslist=new ArrayList<>();
dbHelper=new DBHelper(ConfrimCartActivity.this);
        loadinBar.setMessage("Please wait your cart is being processed");
        loadinBar.setCanceledOnTouchOutside(false);
        cartactivitylistview=findViewById(R.id.cartactivitylistview);
       dbHelper=new DBHelper(ConfrimCartActivity.this);
        theorder theorder=new theorder();
    //    theorderlist=dbHelper.getallorders();
        Submitorder();
    }
    private void Submitorder() {
        loadinBar.show();
        String url="https://www.studentfyp.com/blindshoppingapp/finalcart.php";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ConfrimCartActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                JSONObject data= null;
                try {
                    data = new JSONObject(response);
                    String success=data.getString("success");
                    if(success.equals("1"))
                    {
                        loadinBar.hide();
                        Toast.makeText(ConfrimCartActivity.this, "Your Order Has Been Submited", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(ConfrimCartActivity.this,LoginActivity.class);
                        startActivity(intent);

                    }
                    else
                    {
                        loadinBar.show();
                        Toast.makeText(ConfrimCartActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    loadinBar.hide();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadinBar.hide();
                Toast.makeText(ConfrimCartActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("itemid",pid+"");
                params.put("userid",userid+"");
                params.put("quantity",1+"");
                params.put("date",formattedDate);


                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(ConfrimCartActivity.this);
        queue.add(request);
    }
}
