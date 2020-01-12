package com.example.junaidtanoli.blindshoppingapp.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.junaidtanoli.blindshoppingapp.AdminViewAllOrderActivity;
import com.example.junaidtanoli.blindshoppingapp.DataClasses.showallordersclass;
import com.example.junaidtanoli.blindshoppingapp.DataClasses.showhomedata;
import com.example.junaidtanoli.blindshoppingapp.R;
import com.example.junaidtanoli.blindshoppingapp.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.junaidtanoli.blindshoppingapp.R.drawable;
import static com.example.junaidtanoli.blindshoppingapp.R.id;
import static com.example.junaidtanoli.blindshoppingapp.R.layout;

public class showalluseradapter extends ArrayAdapter<showhomedata> {
    ArrayList<showhomedata> thedata;
    ImageView imageView;
    ProgressDialog loadingBar;
    TextView txtnumber,txtusername;
Button btndeliver;
AdminViewAllOrderActivity adminViewAllOrderActivity;
    public Context context;
    public showalluseradapter(Context context, ArrayList<showhomedata> thedata)
    {
        super(context, R.layout.showuserlayout,thedata);
        this.context=context;
        this.thedata=thedata;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.showuserlayout,null);
adminViewAllOrderActivity=new AdminViewAllOrderActivity();
       loadingBar=new ProgressDialog(context);
        btndeliver=view.findViewById(id.block);
        txtusername =view.findViewById(id.name);
        txtnumber=view.findViewById(R.id.no);
        txtnumber.setText(position+1+"");
        txtusername.setText(thedata.get(position).getName());
        btndeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Orderdeliver(thedata.get(position).getId());
            }
        });
        return view;
    }
    public void Orderdeliver(final int userid) {
        String url="http://www.studentfyp.com/blindshoppingapp/blockuser.php";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();
                JSONObject data= null;
                try {
                    data = new JSONObject(response);
                    String success=data.getString("success");
                    if(success.equals("1"))
                    {
                        loadingBar.hide();
                    }
                    else
                    {
                        loadingBar.show();
                        Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    loadingBar.hide();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.hide();
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("userid",""+userid);
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(request);
    }
}
