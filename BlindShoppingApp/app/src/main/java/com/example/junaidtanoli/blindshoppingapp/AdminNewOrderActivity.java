package com.example.junaidtanoli.blindshoppingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.junaidtanoli.blindshoppingapp.Model.AdminsOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView ordelist;
    private DatabaseReference orderRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);
        orderRef=FirebaseDatabase.getInstance().getReference().child("Order Details");

        ordelist=(RecyclerView) findViewById(R.id.Order_list);
        ordelist.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminsOrders>options= new FirebaseRecyclerOptions.Builder<AdminsOrders>()
                .setQuery(orderRef,AdminsOrders.class).build();
        FirebaseRecyclerAdapter<AdminsOrders,AdminOrdersViewHolder>adapter=
                new FirebaseRecyclerAdapter<AdminsOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminsOrders model)
                    {

                        holder.username.setText("Name:"+model.getName());
                        holder.userphonenumber.setText("Phone:"+model.getPhone());
                        holder.userTotalprice.setText("Total Amount:"+model.getTotalAmount());
                        holder.UserDateTime.setText("Order at:"+model.getDate()+"  "+model.getTime());
                        holder.userShippingAddress.setText("shipping Address:"+model.getAddress()+" , "+model.getCity());

                        holder.showorderbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uID=getRef(position).getKey();

                                Intent intent=new Intent(AdminNewOrderActivity.this,AdminUserProductsActivity.class);
                                intent.putExtra("uid",uID);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrdersViewHolder(view);
                    }
                };
        ordelist.setAdapter(adapter);
        adapter.startListening();

    }
    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder

    {
        public TextView username,userphonenumber,userTotalprice,UserDateTime,userShippingAddress;
        public Button showorderbtn;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.user_name);
            userphonenumber=itemView.findViewById(R.id.order_phone_number);
            userTotalprice=itemView.findViewById(R.id.order_total_price);
            UserDateTime=itemView.findViewById(R.id.order_date_time);
            userShippingAddress=itemView.findViewById(R.id.order_address_city);
            showorderbtn=itemView.findViewById(R.id.show_all_products);
        }
    }
}
