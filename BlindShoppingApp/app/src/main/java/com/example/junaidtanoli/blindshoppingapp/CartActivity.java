package com.example.junaidtanoli.blindshoppingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junaidtanoli.blindshoppingapp.Model.Cart;
import com.example.junaidtanoli.blindshoppingapp.Prevalent.Prevalent;
import com.example.junaidtanoli.blindshoppingapp.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button Nextprocessbtn;
    private TextView textTotalAmaount,txtmsg;
    private int overTotalPrice=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView=(RecyclerView)findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Nextprocessbtn=(Button)findViewById(R.id.next_btn);
        textTotalAmaount=(TextView)findViewById(R.id.total_price);
        txtmsg=(TextView)findViewById(R.id.msg1);

        Nextprocessbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textTotalAmaount.setText("Total Price=$"+String .valueOf(overTotalPrice));

                Intent intent=new Intent(CartActivity.this,ConfirmFinalOrder.class);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        checkorderstate();

        final DatabaseReference CartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart>options=new FirebaseRecyclerOptions.Builder<Cart>().setQuery(CartListRef.child("User View")
        .child(Prevalent.currentOnlineUser.getPhone()).child("Products"),Cart.class).build();

       FirebaseRecyclerAdapter<Cart,CartViewHolder>adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

               holder.textproductname.setText(model.getPname());
               holder.textproductprice.setText("Price="+model.getPrice());
               holder.textproductquantity.setText("Quantity="+model.getQuantity());

               int ontyProductPrice=((Integer.valueOf(model.getPrice())))* Integer.valueOf(model.getQuantity());
               overTotalPrice=overTotalPrice+ontyProductPrice;

               holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       CharSequence options[]=new CharSequence[]{

                               "Edit",
                               "Delete"


                       };
                       AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                       builder.setTitle("Cart Options");
                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int i) {

                               if (i==0);
                               {
                                   Intent intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                   intent.putExtra("pid",model.getPid());
                                   startActivity(intent);
                               }

                               if (i==1){

                                   CartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                           .child("Products").child(model.getPid()).removeValue()
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful()){

                                                       Toast.makeText(CartActivity.this,"item remove successfully",Toast.LENGTH_SHORT).show();

                                                       Intent intent=new Intent(CartActivity.this,HomeActivity.class);

                                                       startActivity(intent);
                                                   }
                                               }
                                           });
                               }
                           }
                       });
                       builder.show();
                   }
               });
           }

           @NonNull
           @Override
           public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
           {

               View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);

               CartViewHolder holder=new CartViewHolder(view);
               return holder;
           }
       };
       recyclerView.setAdapter(adapter);
       adapter.startListening();
    }
    public void checkorderstate()
    {
        DatabaseReference orderref;
        orderref=FirebaseDatabase.getInstance().getReference().child("Order Details").child(Prevalent.currentOnlineUser.getPhone());

        orderref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String shippingstate=dataSnapshot.child("state").getValue().toString();
                    String name=dataSnapshot.child("name").getValue().toString();
                    if (shippingstate.equals("shipped"))
                    {
                        textTotalAmaount.setText("Dear"+name+"\n order is shipped successfully");
                        recyclerView.setVisibility(View.GONE);

                        txtmsg.setVisibility(View.VISIBLE);
                        Nextprocessbtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this,"you can purchase more products, once you recieved your final order",Toast.LENGTH_SHORT);

                    }
                    else if (shippingstate.equals("not shipped"))
                    {
                        textTotalAmaount.setText("Shipping State=Not Shipped");
                        recyclerView.setVisibility(View.GONE);

                        txtmsg.setVisibility(View.VISIBLE);
                        Nextprocessbtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this,"you can purchase more products, once you recieved your final order",Toast.LENGTH_SHORT);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
