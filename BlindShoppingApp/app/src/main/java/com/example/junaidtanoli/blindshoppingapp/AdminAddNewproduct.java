package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewproduct extends AppCompatActivity {
    private Button addnewproductButton;
    private EditText productname,productprice,productDiscription;
    private ImageView addimage;
private String CategoryName,Discription,Price,Pname,saveCurrentDate,saveCurrentTime;
private static final int GalleryPick=1;
private Uri ImageUri;
private String productRandomKey, downloadImageURl;
private StorageReference ProductImageRef;
private DatabaseReference productRef;
    private ProgressDialog loadinBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_newproduct);

        CategoryName=getIntent().getExtras().get("Category").toString();
        ProductImageRef=FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef=FirebaseDatabase.getInstance().getReference().child("Products");


        addnewproductButton=(Button)findViewById(R.id.add_new_product);
        productname=(EditText) findViewById(R.id.productName);
        productprice=(EditText) findViewById(R.id.productPrice);
        productDiscription=(EditText) findViewById(R.id.productDiscription);
        addimage=(ImageView) findViewById(R.id.selectproductImage);

        loadinBar=new ProgressDialog(this);
        
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
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


        if (ImageUri==null)
        {
            Toast.makeText(this,"product image is mendetory",Toast.LENGTH_SHORT);

        }else if(TextUtils.isEmpty(Discription)){
            Toast.makeText(this,"product Discription is mendetory",Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(Price)){
            Toast.makeText(this,"product Price is mendetory",Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(Pname)){
            Toast.makeText(this,"product ProductName is mendetory",Toast.LENGTH_SHORT);
        }
        else {
            StorePrductInformation();
        }
    }

    private void StorePrductInformation() {

        loadinBar.setTitle("adding new product");
        loadinBar.setMessage("Dear Admin, please wait while we are adding new product");
        loadinBar.setCanceledOnTouchOutside(false);
        loadinBar.show();

        Calendar calender=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calender.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calender.getTime());

        productRandomKey=saveCurrentDate+saveCurrentTime;

        final StorageReference filepath=ProductImageRef.child(ImageUri.getLastPathSegment()+ productRandomKey);

        final UploadTask uploadTask=filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AdminAddNewproduct.this,"Error!"+message,Toast.LENGTH_SHORT).show();
                loadinBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewproduct.this,"image upload successfull",Toast.LENGTH_SHORT).show();

                Task<Uri> urltask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }


                        downloadImageURl=filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageURl=task.getResult().toString();

                            Toast.makeText(AdminAddNewproduct.this,"Get product image url Successfully",Toast.LENGTH_SHORT).show();


                            SaveProductIntoDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductIntoDatabase() {
        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("discription",Discription);
        productMap.put("image",downloadImageURl);
        productMap.put("category",CategoryName);
        productMap.put("price",Price);
        productMap.put("pname",Pname);

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent=new Intent(AdminAddNewproduct.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadinBar.dismiss();
                            Toast.makeText(AdminAddNewproduct.this,"product is added successfully",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadinBar.dismiss();

                            String message=task.getException().toString();
                            Toast.makeText(AdminAddNewproduct.this,"Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void openGallery() {
        Intent  galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {

            ImageUri=data.getData();
            addimage.setImageURI(ImageUri);
        }
    }
}
