package com.example.junaidtanoli.blindshoppingapp;

import android.graphics.Bitmap;

/**
 * Created by Malik A Pasha on 3/25/2018.
 */
public class User {
    public static String cid = "null";
    int userid;
    String useremail,userpass;
    String customerphone,customername;
    String userfname,userlname,userphone;
    public Bitmap userpicture;
    public User()
    {
    }
    public User(String fname,String lname,String email,String pass,String phone)
    {

        this.userfname=fname;
        this.userlname=lname;
        this.useremail=email;
        this.userpass=pass;
        this.userphone=phone;
    }

    public User(String fname,String lname,String email,String pass,String phone,Bitmap picture)
    {

        this.userfname=fname;
        this.userlname=lname;
        this.useremail=email;
        this.userpass=pass;
        this.userphone=phone;
        this.userpicture=picture;
    }

    public String getCustomerphone() {
        return customerphone;
    }

    public void setCustomerphone(String customerphone) {
        this.customerphone = customerphone;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserfname(String userfname) {
        this.userfname = userfname;
    }

    public String getUserfname() {
        return userfname;
    }

    public void setUserlname(String userlname) {
        this.userlname = userlname;
    }

    public String getUserlname() {
        return userlname;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getUserphone() {
        return userphone;
    }

    public Bitmap getUserpicture() {
        return userpicture;
    }

    public void setUserpicture(Bitmap userpicture) {
        this.userpicture = userpicture;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    public String getUserpass() {
        return userpass;
    }
}
