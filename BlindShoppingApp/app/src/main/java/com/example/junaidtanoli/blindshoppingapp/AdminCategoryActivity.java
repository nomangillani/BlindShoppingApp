package com.example.junaidtanoli.blindshoppingapp;

import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView shoes,Tshirts,laptops,glasses;
    private ImageView caps,begs,phones,grocery;
    private Button logoutbtn,checkorderbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        logoutbtn=(Button)findViewById(R.id.admin_logout_btn);
        checkorderbtn=(Button)findViewById(R.id.check_order_button);

        shoes=(ImageView)findViewById(R.id.shoes);
        Tshirts=(ImageView)findViewById(R.id.t_shirts);
        laptops=(ImageView)findViewById(R.id.laptop);
        glasses=(ImageView)findViewById(R.id.glasses);

        caps=(ImageView)findViewById(R.id.caps);
        begs=(ImageView)findViewById(R.id.begs);
        phones=(ImageView)findViewById(R.id.phones);
        grocery=(ImageView)findViewById(R.id.grocery);



        Tshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewproduct.class);
                intent.putExtra("Category","t-shirts");
                startActivity(intent);
            }
        });
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewproduct.class);
                intent.putExtra("Category","shoes");
                startActivity(intent);
            }
        });
        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewproduct.class);
                intent.putExtra("Category","Laptops");
                startActivity(intent);
            }
        });
        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewproduct.class);
                intent.putExtra("Category","Glasses");
                startActivity(intent);
            }
        });
        caps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewproduct.class);
                intent.putExtra("Category","Caps");
                startActivity(intent);
            }
        });
        phones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewproduct.class);
                intent.putExtra("Category","Mobile Phone");
                startActivity(intent);
            }
        });
        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewproduct.class);
                intent.putExtra("Category","Grocery");
                startActivity(intent);
            }
        });
        begs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewproduct.class);
                intent.putExtra("Category","Begs");
                startActivity(intent);
            }
        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        checkorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminNewOrderActivity.class);
                startActivity(intent);

            }
        });

    }
}
