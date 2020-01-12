package com.example.junaidtanoli.blindshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import javax.security.auth.PrivateCredentialPermission;

import static android.text.TextUtils.isEmpty;

public class SignupActivity extends AppCompatActivity {
private Button Register,gotologin;
private EditText username,phone,passward;
private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username=(EditText)findViewById(R.id.editname);
        phone=(EditText)findViewById(R.id.edittextphone);
        passward=(EditText)findViewById(R.id.edittextpassward);
        Register=(Button) findViewById(R.id.Register);

        loadingBar=new ProgressDialog(this);



        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }
    private void CreateAccount() {
        String name=username.getText().toString();
        String phoneNo=phone.getText().toString();
        String pass=passward.getText().toString();
        if (isEmpty(name)) {
            username.setError("please enter  Name");
        }
       else if (isEmpty(phoneNo)){

            phone.setError("please enter phone Number");
        }
       else if (isEmpty(pass)){
            passward.setError("please enter passward");
        }
        else{
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            ValidatePhoneNumber(name,phoneNo,pass);
        }
    }

    private void ValidatePhoneNumber(final String name, final String phoneNo, final String pass) {

        final DatabaseReference Rootref;
        Rootref=FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Users").child(phoneNo).exists()){

                    HashMap<String,Object>userDataMap=new HashMap<>();
                    userDataMap.put("phone",phoneNo);
                    userDataMap.put("passward",pass);
                    userDataMap.put("name",name);

                    Rootref.child("Users").child(phoneNo).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this,"successful",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        loadingBar.dismiss();
                                       Toast.makeText(SignupActivity.this,"Network Error Please Try Again",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {

                    Toast.makeText(SignupActivity.this,"This"+phoneNo+"alread Exist.",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(SignupActivity.this,"Please Try Again Using Another Phone Number",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
