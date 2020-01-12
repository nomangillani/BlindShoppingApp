package com.example.junaidtanoli.blindshoppingapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;

import com.example.junaidtanoli.blindshoppingapp.Interface.ItemClickListner;
import com.example.junaidtanoli.blindshoppingapp.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textproductname,textproductprice,textproductquantity;
    private ItemClickListner itemClickListner;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        textproductname=itemView.findViewById(R.id.cart_product_name);
        textproductprice=itemView.findViewById(R.id.cart_product_price);
        textproductquantity=itemView.findViewById(R.id.cart_product_quantity);


    }

    @Override
    public void onClick(View view) {


        itemClickListner.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}
