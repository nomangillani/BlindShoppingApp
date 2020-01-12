package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junaidtanoli.blindshoppingapp.Model.Cart;
import com.example.junaidtanoli.blindshoppingapp.Model.MyProducts;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class CartFinalized extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private EditText editphone, editpassward;
    private Button loginbtn;
    String phone,password;
    String result=Comands.PHONE;
    private String queuedText;
    private String TAG = "TTS";
    //  end speech
    ArrayList<MyProducts> myProducts;
    String id;
    int itemposition;
    private TextView textTotalAmaount,txtmsg;
    private int overTotalPrice=0;
    Button Nextprocessbtn;
    private final int REQ_CODE = 200;
    float TotalPrice;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private TextView admin,notadmin,createAccount,welcome_text;
    private ProgressDialog loadinBar;
    private String parentDbName="Users";
    Bundle bundle;
    int no;
    int yes;
    String moving="";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    // speech
    private TextToSpeech tts;
    private boolean initialized;
    //  end speech

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testcart);
        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
        speak(" Please say admin, not admin or Create account");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        moving=preferences.getString("inside","");
        Toast.makeText(this, "here is moving"+moving, Toast.LENGTH_SHORT).show();
        no=0;
        yes=0;
        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
        myProducts=new ArrayList<>();
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
                Intent intent=new Intent(CartFinalized.this,ConfirmFinalOrder.class);
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
        final FirebaseRecyclerOptions<Cart> options=new FirebaseRecyclerOptions.Builder<Cart>().setQuery(CartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone()).child("Products"),Cart.class).build();
        final FirebaseRecyclerAdapter<Cart,CartViewHolder> adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                itemposition=position;
                holder.textproductname.setText(model.getPname());
                holder.textproductprice.setText("Price="+model.getPrice());
                holder.textproductquantity.setText("Quantity="+model.getQuantity());
                myProducts.add(new MyProducts(model.getPid(), model.getPname(),model.getPrice(), model.getPrice()));
                editor.putString("inside","inside");
                editor.commit();
                int ontyProductPrice=((Integer.valueOf(model.getPrice())))* Integer.valueOf(model.getQuantity());
                overTotalPrice=overTotalPrice+ontyProductPrice;
//               if(position==0)
//               {
//                   myProducts.add(new MyProducts(model.getPid(), model.getPname(),model.getPrice(), model.getPrice()));
//nextProduct();
//               }

//               nextProduct();
                int speechStatus = tts.speak(myProducts.get(0).getTitle()+"  with  Rupees"+model.getPrice()+"Doy want to remove or Confirm Order?", TextToSpeech.QUEUE_FLUSH, null);
                if (speechStatus == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
                promptSpeechInput();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[]=new CharSequence[]{
                                "Edit",
                                "Delete"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartFinalized.this);
                        builder.setTitle("Cart Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i==0);
                                {
                                    Intent intent=new Intent(CartFinalized.this,ProductDetailsActivity.class);
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
                                                        Toast.makeText(CartFinalized.this,"item remove successfully",Toast.LENGTH_SHORT).show();
                                                        Intent intent=new Intent(CartFinalized.this,HomeActivity.class);
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
        //nextProduct();
    }
    public  void nextProduct()
    {
        MyProducts obj = myProducts.get(0);
        int speechStatus = tts.speak(obj.title+"  with  Rupees"+obj.price+"Doy want to remove?", TextToSpeech.QUEUE_FLUSH, null);
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
        promptSpeechInput();
//        int index=0;
//        Log.e("nextpr"," "+index+" "+myProducts.size()+"");
//
//        if(index<myProducts.size()) {
//            MyProducts obj = myProducts.get(0);
//            Log.e("seeit"," "+obj.getTitle()+"");
////            Toast.makeText(this, "here is title"+obj.title, Toast.LENGTH_SHORT).show();
////            Toast.makeText(this, "here is your choice"+myProducts.get(0).title+myProducts.get(0).price, Toast.LENGTH_SHORT).show();
////           Toast.makeText(this, "price and title"+obj.price+obj.title, Toast.LENGTH_SHORT).show();
//            speak(obj.title.toString()+" with "+obj.price+" rupees  Remove or Close");
//        }
    }
    //voice
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
                        Toast.makeText(CartFinalized.this,"you can purchase more products, once you recieved your final order",Toast.LENGTH_SHORT);
                    }
                    else if (shippingstate.equals("not shipped"))
                    {
                        textTotalAmaount.setText("Shipping State=Not Shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtmsg.setVisibility(View.VISIBLE);
                        Nextprocessbtn.setVisibility(View.GONE);
                        Toast.makeText(CartFinalized.this,"you can purchase more products, once you recieved your final order",Toast.LENGTH_SHORT);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public void remove(final String id1)
    {
        Log.e("value",id1+" ");
        final DatabaseReference CartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
        CartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(id1).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(CartFinalized.this,"item remove successfully",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(CartFinalized.this,CartFinalized.class);
                            startActivity(intent);
                        }
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

            new Thread()
            {
                public void run()
                {
                    CartFinalized.this.runOnUiThread(new runnable()
                    {
                        public void run()
                        {
                            afterSpeaks();
                        }
                    });
                }
            }.start();

        }
    };
    void afterSpeaks(){
        Toast.makeText(getBaseContext(), "Speaks completed", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.ENGLISH);
        try{
            startActivityForResult(intent,200);
        }catch (Exception e){
            Toast.makeText(this, "Intent Problem", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, ""+requestCode, Toast.LENGTH_SHORT).show();
        if(requestCode==REQ_CODE){
            Toast.makeText(this, "here is adta ", Toast.LENGTH_SHORT).show();
            if(resultCode==RESULT_OK && data!=null){
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String input=result.get(0);
                if("admin".equalsIgnoreCase(input)){
                    Intent intent=new Intent(CartFinalized.this,Testlogin.class);
                    intent.putExtra("parentDbName","Admin");
                    startActivity(intent);
         }
                else if("not admin".equalsIgnoreCase(input)){
                    Intent intent=new Intent(CartFinalized.this,Testlogin.class);
                    intent.putExtra("parentDbName","Users");
                    startActivity(intent);
                }else
                {
                    speak("Which You want to go admin or not admin");
                }

            }
        }
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
}