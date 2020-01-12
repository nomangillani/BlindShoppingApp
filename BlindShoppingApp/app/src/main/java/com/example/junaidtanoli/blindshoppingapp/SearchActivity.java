package com.example.junaidtanoli.blindshoppingapp;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.junaidtanoli.blindshoppingapp.Model.MyProducts;
import com.example.junaidtanoli.blindshoppingapp.Model.Products;
import com.example.junaidtanoli.blindshoppingapp.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Button search;
    private EditText inputText;
    private RecyclerView searchlist;
    private String SearchInput;

    // speech
    private TextToSpeech tts;
    private boolean initialized;
    private String queuedText;
    private String TAG = "TTS";

    ArrayList<MyProducts> myProducts=new ArrayList<>();
    int sideBar=0;
    int index=0;

    //  end speech
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search=(Button)findViewById(R.id.searchbtn);
        inputText=(EditText)findViewById(R.id.search_product_name);
        searchlist=(RecyclerView)findViewById(R.id.search_list);
        searchlist.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchInput=inputText.getText().toString();

                onStart();
            }
        });

        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
        speak("Which products do you want to search like Caps,T shirts or Glasses");

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products>options=new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("pname").startAt(SearchInput),Products.class)
                .build();
        FirebaseRecyclerAdapter<Products,ProductViewHolder>adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                myProducts.clear();
                myProducts.add(new MyProducts(model.getPid(),model.getPname(),model.getDiscription(),model.getPrice()));
                Log.d("jabir",model.getPname());
                holder.txtProductName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDiscription());
                holder.txtProductPrice.setText("Price=" + model.getPrice() + " Rs");
                Picasso.get().load(model.getImage()).into(holder.imageView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(SearchActivity.this,ProductDetailsActivity.class);
                        intent.putExtra("pid",model.getPid());
                        startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);

                ProductViewHolder holder=new ProductViewHolder(view);
                return holder;
            }
        };

        searchlist.setAdapter(adapter);
        adapter.startListening();
        Log.d("jabir",myProducts.size()+".");
        nextProduct();
    }
    //   speak starts
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
                    SearchActivity.this.runOnUiThread(new SearchActivity.runnable()
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


        if(requestCode==200){
            if(resultCode==RESULT_OK && data!=null){
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String input=result.get(0);
                if("Cart".equalsIgnoreCase(input)){

                    Intent createAccount=new Intent(SearchActivity.this,CartActivity.class);
                    startActivity(createAccount);
                    Toast.makeText(this, "Cart Activity open", Toast.LENGTH_SHORT).show();
                }else if(input.contains("caps") || input.contains("Caps")){
                    inputText.setText("Caps");
                    SearchInput="Caps";

                    inputText.setText("T shirts");
                    SearchInput="T shirts";


                    onStart();

                }else if(input.contains("glasses") || input.contains("sun Glasses")){
                    inputText.setText("Glasses");
                    SearchInput="Glasses";

                    onStart();
                }

                else if ("View".equalsIgnoreCase(input)){
                    int i=index;
                    ProductDetailsActivity.index=index;
                    Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("pid", myProducts.get(--i).id);
                    startActivity(intent);

                }
                else if ("Skip".equalsIgnoreCase(input)){
                    nextProduct();

                }



                else{
                    speak("please say only View or Skip.");
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
    //  speach recognition code stops

    //  speaks ends
}
