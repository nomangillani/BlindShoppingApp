package com.example.junaidtanoli.blindshoppingapp.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.junaidtanoli.blindshoppingapp.LoginActivity;
import com.example.junaidtanoli.blindshoppingapp.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.junaidtanoli.blindshoppingapp.R.drawable;
import static com.example.junaidtanoli.blindshoppingapp.R.id;
import static com.example.junaidtanoli.blindshoppingapp.R.layout;

public class showallordersadapter extends ArrayAdapter<showallordersclass> {
    ArrayList<showallordersclass> thedata;
    ImageView imageView;
    TextView txtProductName,txtProductDescription,txtProductPrice;
    ProgressDialog loadingBar;
    TextView txtaddress,txtusername,txtcontact,txtquantity;
Button btndeliver;
AdminViewAllOrderActivity adminViewAllOrderActivity;
    public Context context;
    public showallordersadapter(Context context, ArrayList<showallordersclass> thedata)
    {
        super(context, layout.product_item_layout,thedata);
        this.context=context;
        this.thedata=thedata;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view =layoutInflater.inflate(layout.showallorderslayout,null);
adminViewAllOrderActivity=new AdminViewAllOrderActivity();
       loadingBar=new ProgressDialog(context);
        btndeliver=view.findViewById(id.btndeliver);
        imageView = view.findViewById(id.product_image1);
        txtProductName =view.findViewById(id.product_name1);
        txtProductDescription = view.findViewById(id.product_discription1);
        txtProductPrice = view.findViewById(id.product_price1);
        txtusername =view.findViewById(id.txtusername);
        txtcontact = view.findViewById(id.product_contact);
        txtquantity= view.findViewById(id.product_quantity);
        txtaddress= view.findViewById(id.product_address);
        try
     {
         imageView.setImageBitmap(Utility.stringToBitmap(String.valueOf(thedata.get(position).getImage())));
     }
     catch (Exception er)
     {
         imageView.setImageResource(drawable.applogo);
     }
        txtProductDescription.setText(thedata.get(position).getDesc());
        txtProductName.setText(thedata.get(position).getName());
        txtProductPrice.setText(thedata.get(position).getPrice());
        txtusername.setText(thedata.get(position).getUsername());
        txtcontact.setText(thedata.get(position).getUsercontact());
        txtquantity.setText(thedata.get(position).getQuantity());
        txtaddress.setText(thedata.get(position).getUseraddress());
        if(thedata.get(position).getAid()==0)
        {
            btndeliver.setVisibility(View.GONE);
        }
        btndeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Orderdeliver(thedata.get(position).getOid());
            }
        });
        return view;
    }
    public void Orderdeliver(final int oid) {
        String url="http://www.studentfyp.com/blindshoppingapp/diliverorder.php";
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
                params.put("oid",""+oid);
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(request);
    }
}
