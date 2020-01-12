package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminLogin extends AppCompatActivity {
    EditText editTexttphoneadmin,editTexttpasswordadmin;
    Button login;
ProgressDialog loadinBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        login=findViewById(R.id.Loginadmin);
        loadinBar = new ProgressDialog(this);

        editTexttphoneadmin=findViewById(R.id.editTextPhoneadmin);
        editTexttpasswordadmin=findViewById(R.id.editTextPasswardadmin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
loadinBar.show();
                if(editTexttphoneadmin.getText().toString().equals("03007382084")&&editTexttpasswordadmin.getText().toString().equals("admin88995")) {
                    Intent intent = new Intent(AdminLogin.this, AdminCategoryActivity.class);
                    loadinBar.hide();
                    startActivity(intent);
                }
                else
                    Toast.makeText(AdminLogin.this, "Please Check Your Information for Login", Toast.LENGTH_SHORT).show();
            }
        });
       }
    private void AllowAccesstoAccount(final String phone, final String pass) {
        String url = "https://www.studentfyp.com/blindshoppingapp/login.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(AdminLogin.this, "" + response, Toast.LENGTH_SHORT).show();
                JSONObject data = null;
                try {
                    data = new JSONObject(response);
                    String success = data.getString("success");
                    if (success.equals("1")) {
                        Intent intent = new Intent(AdminLogin.this, AdminAddNewproduct.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(AdminLogin.this,"Some Error Occured", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminLogin.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pass", phone);
                params.put("phone", pass);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AdminLogin.this);
        queue.add(request);

    }

}
