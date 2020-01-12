package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminAddNewproduct extends AppCompatActivity {
    private Button addnewproductButton;
    private EditText productname,productprice,productDiscription;
    private ImageView addimage;
private String CategoryName,Discription,Price,Pname,saveCurrentDate,saveCurrentTime;
private static final int GalleryPick=1;
String imageString1="";
private Uri ImageUri;
private String productRandomKey, downloadImageURl;
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    private ProgressDialog loadinBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_newproduct);

       CategoryName=getIntent().getExtras().get("Category").toString();

        addnewproductButton=(Button)findViewById(R.id.add_new_product);

        productname=(EditText) findViewById(R.id.productName);
        productname.setText(CategoryName);
        productprice=(EditText) findViewById(R.id.productPrice);
        productDiscription=(EditText) findViewById(R.id.productDiscription);
        addimage=(ImageView) findViewById(R.id.selectproductImage);

        loadinBar=new ProgressDialog(this);
        
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        addnewproductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
    }
    private void ValidateProductData() {

        Discription=productDiscription.getText().toString();
        Price=productprice.getText().toString();
        Pname=productname.getText().toString();


        if (addimage.equals(""))
        {
            Toast.makeText(this,"product image is mendetory",Toast.LENGTH_SHORT);

        }else if(productDiscription.getText().equals("")){
            Toast.makeText(this,"product Discription is mendetory",Toast.LENGTH_SHORT);
        }
        else if(productprice.getText().equals("")){
            Toast.makeText(this,"product Price is mendetory",Toast.LENGTH_SHORT);
        }
        else if(productname.getText().equals("")){
            Toast.makeText(this,"product ProductName is mendetory",Toast.LENGTH_SHORT);
        }
        else {
            SaveProductIntoDatabase(Pname,Price,Discription);
        }
    }
    private void SaveProductIntoDatabase(final String pname, final String price, final String discription) {

        loadinBar.setTitle("adding new product");
        loadinBar.setMessage("Dear Admin, please wait while we are adding new product");
        loadinBar.setCanceledOnTouchOutside(false);
        loadinBar.show();

        Calendar calender=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calender.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calender.getTime());
        Bitmap bm = ((android.graphics.drawable.BitmapDrawable) addimage.getDrawable()).getBitmap();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        imageString1 =Utility.bitmapToString(((BitmapDrawable) addimage.getDrawable()).getBitmap());
        String url="https://www.studentfyp.com/blindshoppingapp/addnewitems.php";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject data= null;
                try {
                    data = new JSONObject(response);
                    String success=data.getString("success");
                    if(success.equals("1"))
                    {
                        Intent intent=new Intent(AdminAddNewproduct.this,AdminCategoryActivity.class);
                        startActivity(intent);
                        loadinBar.hide();
                    }
                    else
                    {
                        Toast.makeText(AdminAddNewproduct.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                        loadinBar.hide();
                    }

                loadinBar.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadinBar.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminAddNewproduct.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                loadinBar.hide();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",pname);
                params.put("price",price);
                params.put("description",discription);
                params.put("image",imageString1);
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(AdminAddNewproduct.this);
        queue.add(request);
    }
    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddNewproduct.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                } else if (options[item].equals("Choose from Gallery"))
                {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 1);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            addimage.setImageBitmap(bitmap);
            // img1.setImageBitmap(bitmap);
        }
        if(requestCode==1) {
            if (resultCode == RESULT_OK) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                 addimage.setImageBitmap(selectedImage);
//
//                    imageBytes = Base64.decode(imageString, Base64.DEFAULT);
//                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//                    addimage.setImageBitmap(decodedImage);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(AdminAddNewproduct.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(AdminAddNewproduct.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        }
    }
}
