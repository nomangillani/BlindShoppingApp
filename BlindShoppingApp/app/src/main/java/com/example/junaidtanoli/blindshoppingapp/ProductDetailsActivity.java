package com.example.junaidtanoli.blindshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.junaidtanoli.blindshoppingapp.Model.MyProducts;
import com.example.junaidtanoli.blindshoppingapp.Model.Products;
import com.example.junaidtanoli.blindshoppingapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Button addtoCart;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productprice, productname, productdiscription;
    private String productID = "", state = "";
    public static int index;

    ArrayList<MyProducts> myProducts=new ArrayList<>();


    // speech
    private TextToSpeech tts;
    private boolean initialized;
    private String queuedText;
    private String TAG = "TTS";

    //  end speech
    public void init() {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        addtoCart = (Button) findViewById(R.id.add_to_cart_btn);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button1);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        productname = (TextView) findViewById(R.id.product_name_details);
        productdiscription = (TextView) findViewById(R.id.product_discription_details);
        productprice = (TextView) findViewById(R.id.product_price_details);


        getProductDetails(productID);

        addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               addToMyChart();

            }
        });


        init();

        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);


    }

    private void addToMyChart() {
        if (state.equals("Order Placed") || state.equals("Order Shipped")) {
            Toast.makeText(ProductDetailsActivity.this, "you can purchase more products, once your order is shipped or confirmed", Toast.LENGTH_LONG).show();

        } else {
            addingToCart();
            Toast.makeText(this, "thank you for shooping", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkorderstate();
    }

    private void addingToCart() {
        String saveCurrentTime, saveCurrentDate;

        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(callforDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(callforDate.getTime());

        final DatabaseReference cartList = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productname.getText().toString());
        cartMap.put("price", productprice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");

        cartList.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID)
                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()) {
                    cartList.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID)
                            .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(ProductDetailsActivity.this, "added to cart List", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                intent.putExtra("index",index);
                                startActivity(intent);
                            }
                        }
                    });
                }

            }
        });


    }

    private void getProductDetails(String productID) {
        DatabaseReference productref = FirebaseDatabase.getInstance().getReference().child("Products");

        productref.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Products products = dataSnapshot.getValue(Products.class);

                    productname.setText(products.getPname());
                    productprice.setText(products.getPrice());
                    productdiscription.setText(products.getDiscription());
                    Picasso.get().load(products.getImage()).into(productImage);
                    speak(  productname.getText().toString()+" "+productdiscription.getText().toString()+" with Price "+productprice.getText().toString()
                            +" Rupees DO you want to Add to Cart or go back");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkorderstate() {
        DatabaseReference orderref;
        orderref = FirebaseDatabase.getInstance().getReference().child("Order Details").child(Prevalent.currentOnlineUser.getPhone());

        orderref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String shippingstate = dataSnapshot.child("state").getValue().toString();

                    if (shippingstate.equals("shipped")) {
                        state = "Order Shipped";
                    } else if (shippingstate.equals("not shipped")) {

                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //voice
    public void speak(String text) {

        if (!initialized) {
            queuedText = text;
            return;
        }
        queuedText = null;

        setTtsListener(); // no longer creates a new UtteranceProgressListener each time
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_ADD, map);
    }

    private void setTtsListener() {

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            initialized = true;
            tts.setLanguage(Locale.ENGLISH);

            if (queuedText != null) {
                speak(queuedText);
            }
        }

    }

    private abstract class runnable implements Runnable {
    }

    private UtteranceProgressListener mProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
        } // Do nothing

        @Override
        public void onError(String utteranceId) {
        } // Do nothing.

        @Override
        public void onDone(String utteranceId) {

            new Thread() {
                public void run() {
                    ProductDetailsActivity.this.runOnUiThread(new ProductDetailsActivity.runnable() {
                        public void run() {
                            afterSpeaks();
                        }
                    });
                }
            }.start();

        }
    };

    void afterSpeaks() {
        Toast.makeText(getBaseContext(), "Speaks completed", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        try {
            startActivityForResult(intent, 200);
        } catch (Exception e) {
            Toast.makeText(this, "Intent Problem", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String input = result.get(0);
                if ("Add to Cart".equalsIgnoreCase(input) || input.contains("Add to Cart")) {
                    addToMyChart();

                } else if ("go back".equalsIgnoreCase(input) || "back".equalsIgnoreCase(input)) {



                    Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                    intent.putExtra("index",index);
                    startActivity(intent);


                } else {
                    speak("speak only back or Add to Cart");
                }
            }
        }
    }
    public  void nextProduct()
    {
        if(index<myProducts.size()) {
            MyProducts obj = myProducts.get(index++);
            speak(obj.title+" with "+obj.price+" rupees  View or Skip");
        }
    }

}