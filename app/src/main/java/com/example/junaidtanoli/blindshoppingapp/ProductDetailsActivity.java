package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.junaidtanoli.blindshoppingapp.DataClasses.cartlist;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.junaidtanoli.blindshoppingapp.LoginActivity.MY_PREFS_NAME;
import static com.example.junaidtanoli.blindshoppingapp.SearchActivity.MYPREFERNCES_Example;

public class ProductDetailsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Button addtoCart;
    Date c;
    SimpleDateFormat simpleDateFormat;
    int userid;
    String formattedDate;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productprice, productname, productdiscription;
    private String productID = "", state = "";
    public static int index;
    protected boolean _active = true;
    protected int _splashTime = 2000;
    ArrayList<cartlist> carlist;
private ProgressDialog loadinBar;

    // speech
    private TextToSpeech tts;
    DataBaseHelper myDb;
    private boolean initialized;
    DBHelper dbHelper;
    private String queuedText;
    private String TAG = "TTS";
String name,price,desc,image,phone,itemname;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    DateFormat dateFormat;
    Date date;
    //  end speech
    public void init() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
      dbHelper=new DBHelper(ProductDetailsActivity.this);
         dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        date = new Date();
        c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = simpleDateFormat.format(c);
        myDb = new DataBaseHelper(ProductDetailsActivity.this);
        carlist=new ArrayList<>();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        userid=pref.getInt("userid",0);
index=pref.getInt("nextproductindex",0);

        itemname = getIntent().getStringExtra("itemname");
        desc = getIntent().getStringExtra("desc");
        Toast.makeText(this, "here is value of index"+index, Toast.LENGTH_SHORT).show();
        price = getIntent().getStringExtra("price");
        image = getIntent().getStringExtra("image");
        phone = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
        loadinBar = new ProgressDialog(this);
loadinBar.setTitle("Requesting Order");
loadinBar.setMessage("Please wait your order is being processed");
loadinBar.setCanceledOnTouchOutside(false);
        addtoCart = (Button) findViewById(R.id.add_to_cart_btn);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button1);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        productname = (TextView) findViewById(R.id.product_name_details);
        productdiscription = (TextView) findViewById(R.id.product_discription_details);
        productprice = (TextView) findViewById(R.id.product_price_details);
productname.setText(name);
productdiscription.setText(desc);
productprice.setText(price);
productImage.setImageBitmap(Utility.stringToBitmap(image));
        speak(  productname.getText().toString()+" "+productdiscription.getText().toString()+" with Price "+productprice.getText().toString()
                +" Rupees DO you want to Add to Cart or go back or confirm cart or add to favourite");
   //     getProductDetails(productID);
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
        theorder anorder = new theorder();
        anorder.setItemid(getIntent().getIntExtra("id",0));
        anorder.setItemquantity(Integer.parseInt(numberButton.getNumber()));
        anorder.setItemimage(image);
        anorder.setItemdesc(desc);
        anorder.setItemname(name);
        editor.putInt("id",getIntent().getIntExtra("id",0));
        editor.putString("finaldesc",desc);
        editor.putString("finalname",name);
        editor.commit();
        if(dbHelper.addorder(anorder))
            Toast.makeText(this, "Has been Added ", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(ProductDetailsActivity.this,SearchActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
      //  checkorderstate();
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
    public void speaktoupdate(String text) {

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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
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
                if ("Add to Cart".equalsIgnoreCase(input) || input.contains("Add to Card")) {
                    Submitorder();
                } else if ("go back".equalsIgnoreCase(input) || "back".equalsIgnoreCase(input)) {
                    Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                    editor.putInt("nextproductindex",index++);
                    editor.commit();
                    editor.apply();
                    startActivity(intent);
                }
                else if ("confirm cart".equalsIgnoreCase(input) || "confirm card".equalsIgnoreCase(input)) {
                    speak("item added successfully into cart go back to home ");

                    Thread splashTread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                int waited = 0;
                                while (_active && (waited < _splashTime)) {
                                    sleep(100);
                                    if (_active) {
                                        waited += 100;
                                    }
                                }
                            } catch (Exception e) {

                            } finally {
                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                editor.putInt("nextproductindex",index++);
                                editor.commit();
                                editor.apply();
                                intent.putExtra("pid",getIntent().getIntExtra("id",0));
                                startActivity(intent);
                                /*startActivity(new Intent(ProductDetailsActivity.this,
                                        HomeActivity.class));*/
                                finish();
                            }
                        };
                    };
                    splashTread.start();





                }
                else if("yes".equalsIgnoreCase(input))
                {
                    Intent intent=new Intent(ProductDetailsActivity.this,Updateadress.class);
                    startActivity(intent);
                }
                else if("no".equalsIgnoreCase(input))
                {
                    Intent intent=new Intent(ProductDetailsActivity.this,HomeActivity.class);
                    startActivity(intent);
                }
                else if ("add to favourite".equalsIgnoreCase(input)||("add to favorite".equalsIgnoreCase(input))){
                    favourite(getIntent().getIntExtra("id",0),userid);
                }
                else {
                    speak("speak only back or Add to Cart");
                }
            }
        }
    }
    private void favourite(final int itemid,final int userid) {
        String url="https://www.studentfyp.com/blindshoppingapp/favourite.php";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ProductDetailsActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                JSONObject data= null;
                try {
                    data = new JSONObject(response);
                    String success=data.getString("success");
                    if(success.equals("1"))
                    {
                        loadinBar.hide();
                        Intent intent=new Intent(ProductDetailsActivity.this,HomeActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        loadinBar.show();
                        Toast.makeText(ProductDetailsActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    loadinBar.hide();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadinBar.hide();
                Toast.makeText(ProductDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("id",""+itemid);
                params.put("userid",""+new LoginSessionManager(getApplicationContext()).getUserKey());
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(ProductDetailsActivity.this);
        queue.add(request);
    }
    private void Submitorder() {
        loadinBar.show();
        String url="https://www.studentfyp.com/blindshoppingapp/finalcart.php";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ProductDetailsActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                JSONObject data= null;
                try {
                    data = new JSONObject(response);
                    String success=data.getString("success");
                    if(success.equals("1"))
                    {
                        loadinBar.hide();
                        Toast.makeText(ProductDetailsActivity.this, "Your Order Has Been Submited", Toast.LENGTH_SHORT).show();
                        speaktoupdate("Do you want to update address?");


                    }
                    else
                    {
                        loadinBar.show();
                        Toast.makeText(ProductDetailsActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    loadinBar.hide();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadinBar.hide();
                Toast.makeText(ProductDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("itemid",2+"");
                params.put("userid",new LoginSessionManager(getApplicationContext()).getUserKey());
                params.put("quantity",numberButton.getNumber()+"");
                params.put("date",formattedDate);
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(ProductDetailsActivity.this);
        queue.add(request);
    }
}