package com.example.junaidtanoli.blindshoppingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junaidtanoli.blindshoppingapp.DataClasses.showhomedata;
import com.example.junaidtanoli.blindshoppingapp.R;
import com.example.junaidtanoli.blindshoppingapp.Utility;

import java.util.ArrayList;

import static com.example.junaidtanoli.blindshoppingapp.R.*;

public class ShowHomeDataAdapter extends ArrayAdapter<showhomedata> {
    ArrayList<showhomedata> thedata;
    ImageView imageView;
    TextView txtProductName,txtProductDescription,txtProductPrice;
    public Context context;
    public ShowHomeDataAdapter(Context context, ArrayList<showhomedata> thedata)
    {
        super(context, R.layout.product_item_layout,thedata);
        this.context=context;
        this.thedata=thedata;
    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.product_item_layout,null);

        imageView = view.findViewById(id.product_image);
        txtProductName =view.findViewById(id.product_name);
        txtProductDescription = view.findViewById(id.product_discription);
        txtProductPrice = view.findViewById(id.product_price);
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
        return view;
    }
}
