package com.example.junaidtanoli.blindshoppingapp;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.junaidtanoli.blindshoppingapp.Adapters.ShowHomeDataAdapter;
import com.example.junaidtanoli.blindshoppingapp.DataClasses.showhomedata;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import io.paperdb.Paper;
import de.hdodenhof.circleimageview.CircleImageView;
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TextToSpeech.OnInitListener {
    private RecyclerView recyclerView;
    private TextView welcome_text,productname;
    ListView homelistview;
    String address,phone,username;
    ArrayList<showhomedata> thedata;
    String desc,price,image,name;
    ArrayAdapter<showhomedata> adapter;
    SessionManager sessionManager;
    private TextToSpeech tts;
    private boolean initialized;
    private String queuedText;
    private String TAG = "TTS";
    SharedPreferences pref;
    int productid;
    SharedPreferences.Editor editor;
  public static   Activity fa;
    LoginSessionManager loginSessionManager;

    int sideBar=0;
    int index=0;

    public void init() {
        productname=(TextView)findViewById(R.id.product_name);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fa=HomeActivity.this;
        loginSessionManager=new LoginSessionManager(getApplicationContext());
        loginSessionManager.checkLogin();
        sessionManager=new SessionManager(HomeActivity.this);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        username = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");
        index=getIntent().getIntExtra("nextproductindex",0);

        homelistview=findViewById(R.id.homelistview);
        thedata=new ArrayList<>();
        alldata();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

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
        //  usernameTextView.setText(Prevalent.currentOnlineUser.getName());
        homelistview = findViewById(R.id.homelistview);
        init();
        tts = new TextToSpeech(this /* context */, this /* listener */);
        tts.setOnUtteranceProgressListener(mProgressListener);
        speak("welcome"+username + " which you Want to go, category, search, view histoy, favourite item or logout");
        sideBar=1;
    }
    @Override
    protected void onStart() {
        super.onStart();
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
        int id = item.getItemId();

        if (id == R.id.nav_Category) {
            Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
            //finish();
            startActivity(intent);
        } else if (id == R.id.nav_Search) {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            finish();
            startActivity(intent);
        }
        else if (id == R.id.nav_history) {
            Intent intent = new Intent(HomeActivity.this, ViewHistory.class);
            finish();
            startActivity(intent);
        }
        else if (id == R.id.nav_logout) {
            LoginSessionManager loginSessionManagermanager=new LoginSessionManager(getApplicationContext());
            loginSessionManagermanager.logoutUser();
        }
        else if (id == R.id.nav_favouriteitems) {
            Intent intent = new Intent(HomeActivity.this, favouriteitems.class);
            finish();
            startActivity(intent);
        }
        else if (id == R.id.nav_history) {
            Intent intent = new Intent(HomeActivity.this, ViewHistory.class);
            finish();
            startActivity(intent);
        }
        else if (id == R.id.nav_setting) {
            Intent intent = new Intent(HomeActivity.this, Updateuserprofile.class);
           finish();
            startActivity(intent);
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
            tts.setLanguage(Locale.US);

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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.US);
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
                    Intent createAccount=new Intent(HomeActivity.this,HomeActivity.class);
                    startActivity(createAccount);
                    Toast.makeText(this, "Cart Activity open", Toast.LENGTH_SHORT).show();
                }
                else if ("Favorite Item".equalsIgnoreCase(input)) {
                    Intent intent=new Intent(HomeActivity.this,favouriteitems.class);
                    startActivity(intent);
                }
                else if ("history".equalsIgnoreCase(input)||"view history".equalsIgnoreCase(input)) {
                    Intent Search = new Intent(HomeActivity.this, ViewHistory.class);
                    startActivity(Search);
                }
                else if ("Logout".equalsIgnoreCase(input)) {
                   // Intent Logout = new Intent(HomeActivity.this, LoginActivity.class);
                    sessionManager.logoutUser();

                }
                else if ("Category".equalsIgnoreCase(input)){
                    Intent Search = new Intent(HomeActivity.this, CategoryActivity.class);
                    Search.putExtra("name", username);
                    Search.putExtra("phone", phone);
                    Search.putExtra("address", address);
                    startActivity(Search);
                }
                else if ("Search".equalsIgnoreCase(input)){
                    Intent Search = new Intent(HomeActivity.this, SearchActivity.class);
                    Search.putExtra("name", username);
                    Search.putExtra("phone", phone);
                    Search.putExtra("address", address);
                    startActivity(Search);
                }
                else if ("Settings".equalsIgnoreCase(input)){
                    Intent Settings=new Intent(HomeActivity.this,Updateuserprofile.class);
                    startActivity(Settings);
                }
                else if ("Log out".equalsIgnoreCase(input)||("log out".equalsIgnoreCase(input))){
                    loginSessionManager.logoutUser();
                    /*Intent Logout=new Intent(HomeActivity.this,SignupActivity.class);
                    startActivity(Logout);*/
                }
                else if ("Show details".equalsIgnoreCase(input)||("Show detail".equalsIgnoreCase(input))){
                    Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("name", thedata.get(index).getName());
                    intent.putExtra("desc", thedata.get(index).getDesc());
                    intent.putExtra("price", thedata.get(index).getPrice());
                    intent.putExtra("image", thedata.get(index).getImage());
                    intent.putExtra("id", thedata.get(index).getId());
                    editor.putInt("nextproductindex", index);
                    editor.commit();
                    editor.apply();
                    startActivity(intent);
                }
                else if ("Skip".equalsIgnoreCase(input)){
                    index++;
                    nextProduct();
                }
                else{
                    speak("please say Category, Search, History, Setting or logout.");
                }

            }

        }
    }
    public void alldata(){
        String url="https://www.studentfyp.com/blindshoppingapp/showallitems.php";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject data = null;
                try {
                    data = new JSONObject(response);
                    JSONArray jsonArray=data.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject object= (JSONObject) jsonArray.get(i);
                        name=object.getString("name");
                        desc=object.getString("description");
                        price=object.getString("price");
                        image=object.getString("image");
                        productid=object.getInt("id");
                        thedata.add(new showhomedata(name,desc,price,image,productid));
                    }
                    adapter=new ShowHomeDataAdapter(HomeActivity.this,thedata);
                    homelistview.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        queue.add(request);
    }
    public  void nextProduct()
    {
        if(thedata.size()>index) {
            // MyProducts obj = myProducts.get(index++);
            speak(thedata.get(index).getDesc()+" with "+thedata.get(index).getPrice()+" rupees  show details or Skip");
        }
    }
    //  speach recognition code stops
}
