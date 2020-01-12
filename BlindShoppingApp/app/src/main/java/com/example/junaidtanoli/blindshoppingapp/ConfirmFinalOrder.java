package com.example.junaidtanoli.blindshoppingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.junaidtanoli.blindshoppingapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrder extends AppCompatActivity {

    private EditText nameEdittext,phoneEdittext,addressEdittext,cityEdittext;
    private Button confirmbtn,priceCheck;

    private String TotalAmount="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        TotalAmount=getIntent().getStringExtra("Total Price");
        Toast.makeText(this,"Total price="+TotalAmount,Toast.LENGTH_SHORT)
        .show();
        confirmbtn=(Button)findViewById(R.id.confirmfinalOrder);
        nameEdittext=(EditText)findViewById(R.id.shipmentName);
        phoneEdittext=(EditText)findViewById(R.id.ShipmentPhone);
        addressEdittext=(EditText)findViewById(R.id.yourHomeAddress);
        cityEdittext=(EditText)findViewById(R.id.yourCity);

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }

    private void check() {
        if (TextUtils.isEmpty(nameEdittext.getText().toString())){
            Toast.makeText(this,"please provide your full name.",Toast.LENGTH_SHORT).show();

        }
        else  if (TextUtils.isEmpty(addressEdittext.getText().toString())){
            Toast.makeText(this,"please provide your full Address.",Toast.LENGTH_SHORT).show();

        }
        else  if (TextUtils.isEmpty(phoneEdittext.getText().toString())){
            Toast.makeText(this,"please provide your phone.",Toast.LENGTH_SHORT).show();

        }
        else  if (TextUtils.isEmpty(cityEdittext.getText().toString())){
            Toast.makeText(this,"please provide your city name.",Toast.LENGTH_SHORT).show();

        }
        else {
            confirmOrder();

        }
    }

    private void confirmOrder() {
       final String saveCurrentDate,saveCurrentTime;

        Calendar callforDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate=currentDate.format(callforDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(callforDate.getTime());

        final DatabaseReference orderRef=FirebaseDatabase.getInstance().getReference().child("Order Details").child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String,Object>orderMap=new HashMap<>();
        orderMap.put("totalAmount",TotalAmount);
        orderMap.put("name",nameEdittext.getText().toString());
        orderMap.put("phone",phoneEdittext.getText().toString());
        orderMap.put("address",addressEdittext.getText().toString());
        orderMap.put("time",saveCurrentTime);
        orderMap.put("city",cityEdittext.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("state","not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().child("List View").child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrder.this,"yout final order is placed successfully",Toast.LENGTH_LONG).show();

                                        Intent intent=new Intent(ConfirmFinalOrder.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });


    }
}
