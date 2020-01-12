package com.example.junaidtanoli.blindshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junaidtanoli.blindshoppingapp.Model.MyProducts;
import com.example.junaidtanoli.blindshoppingapp.Model.Products;
import com.example.junaidtanoli.blindshoppingapp.Prevalent.Prevalent;
import com.example.junaidtanoli.blindshoppingapp.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.paperdb.Paper;

import de.hdodenhof.circleimageview.CircleImageView;


    public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TextToSpeech.OnInitListener {
        private DatabaseReference productReference;
        private RecyclerView recyclerView;
        private TextView welcome_text,productname;
        RecyclerView.LayoutManager layoutManager;


        // speech
        private TextToSpeech tts;
        private boolean initialized;
        private String queuedText;
        private String TAG = "TTS";

        ArrayList<MyProducts> myProducts=new ArrayList<>();
        int sideBar=0;
        int index=0;

        //  end speech
        public void init() {
            productname=(TextView)findViewById(R.id.product_name);

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            index=getIntent().getIntExtra("index",0);

            productReference = FirebaseDatabase.getInstance().getReference().child("Products");

            Paper.init(this);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Home");
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);


                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View headerView = navigationView.getHeaderView(0);
            TextView usernameTextView = headerView.findViewById(R.id.userprofilename);
            CircleImageView profileImageView = headerView.findViewById(R.id.profile_image);

            usernameTextView.setText(Prevalent.currentOnlineUser.getName());

            recyclerView = findViewById(R.id.recycler_menu);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);


            init();
            tts = new TextToSpeech(this /* context */, this /* listener */);
            tts.setOnUtteranceProgressListener(mProgressListener);
            speak("welcome"+usernameTextView.getText().toString() + " which you Want to go cart, category, search, setting or logout");
            sideBar=1;
        }

        @Override
        protected void onStart() {
            super.onStart();
            FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                    .setQuery(productReference, Products.class)
                    .build();

            FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                    myProducts.add(new MyProducts(model.getPid(),model.getPname(),model.getDiscription(),model.getPrice()));

                    holder.txtProductName.setText(model.getPname());
                    holder.txtProductDescription.setText(model.getDiscription());
                    holder.txtProductPrice.setText("Price=" + model.getPrice() + " Rs");
                    Picasso.get().load(model.getImage()).into(holder.imageView);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ProductDetailsActivity.index=index;
                            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                    });
                }

                @NonNull
                @Override
                public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);

    ProductViewHolder holder = new ProductViewHolder(view);
    return holder;
}


            };
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }

        @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.home, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_cart) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_Category) {

                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);

            } else if (id == R.id.nav_Search) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);

            } else if (id == R.id.nav_setting) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);

            } else if (id == R.id.nav_logout) {
                Paper.book().destroy();

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
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
                        HomeActivity.this.runOnUiThread(new HomeActivity.runnable()
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

                        Intent createAccount=new Intent(HomeActivity.this,CartActivity.class);
                        startActivity(createAccount);
                        Toast.makeText(this, "Cart Activity open", Toast.LENGTH_SHORT).show();
                    }


                    else if ("Category".equalsIgnoreCase(input)){
                         nextProduct();

                    }
                    else if ("Search".equalsIgnoreCase(input)){
                        Intent Search=new Intent(HomeActivity.this,SearchActivity.class);
                        startActivity(Search);
                        Toast.makeText(this, "Search Activity open", Toast.LENGTH_SHORT).show();

                    }
                    else if ("Settings".equalsIgnoreCase(input)){


                        Intent Settings=new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(Settings);
                        Toast.makeText(this, "Settings Activity open", Toast.LENGTH_SHORT).show();

                    }
                    else if ("Logout".equalsIgnoreCase(input)){
                        Intent Logout=new Intent(HomeActivity.this,SignupActivity.class);
                        startActivity(Logout);
                        Toast.makeText(this, "Logout Activity open", Toast.LENGTH_SHORT).show();

                    }
                    else if ("View".equalsIgnoreCase(input)){
                        int i=index;
                        ProductDetailsActivity.index=index;
                        Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("pid", myProducts.get(--i).id);

                        startActivity(intent);

                    }
                    else if ("Skip".equalsIgnoreCase(input)){
                        nextProduct();

                    }



                    else{
                        speak("please say Cart, Category, Search, Setting, logout , View or Skip.");
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
    }
