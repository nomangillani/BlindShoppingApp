package com.example.junaidtanoli.blindshoppingapp;

import android.graphics.Bitmap;

/**
 * Created by Malik A Pasha on 4/21/2018.
 */

public class theorder {
    int itemid,orderid,itemquantity,totalquantity;
    float itemprice,orderprice;
    String itemcategory,itemname,orderdate,ordertime,orderstatus;

    public String getItemdesc() {
        return itemdesc;
    }

    public void setItemdesc(String itemdesc) {
        this.itemdesc = itemdesc;
    }

    public String getItemimage() {
        return itemimage;
    }

    public void setItemimage(String itemimage) {
        this.itemimage = itemimage;
    }

    public static float getTotalprice() {
        return totalprice;
    }

    public static void setTotalprice(float totalprice) {
        theorder.totalprice = totalprice;
    }

    int itemdiscount;
    String customerphone,customername,itemdesc,itemimage;
    int ordercount;

    public static float totalprice = 0;

    public int getItemdiscount() {
        return itemdiscount;
    }

    public void setItemdiscount(int itemdiscount) {
        this.itemdiscount = itemdiscount;
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

    public int getOrdercount() {
        return ordercount;
    }

    public void setOrdercount(int ordercount) {
        this.ordercount = ordercount;
    }


    public void setTotalquantity(int totalquantity) {
        this.totalquantity = totalquantity;
    }

    public int getTotalquantity() {
        return totalquantity;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getOrderid() {
        return orderid;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public float getOrderprice() {
        return orderprice;
    }

    public void setOrderprice(float orderprice) {
        this.orderprice = orderprice;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }

    public int getItemid() {
        return itemid;
    }

    public String getItemcategory() {
        return itemcategory;
    }

    public void setItemcategory(String itemcategory) {
        this.itemcategory = itemcategory;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public float getItemprice() {
        return itemprice;
    }

    public void setItemprice(float itemprice) {
        this.itemprice = itemprice;
    }

    public int getItemquantity() {
        return itemquantity;
    }

    public void setItemquantity(int itemquantity) {
        this.itemquantity = itemquantity;
    }

    public Bitmap getItempicture() {
        return itempicture;
    }

    public void setItempicture(Bitmap itempicture) {
        this.itempicture = itempicture;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public Bitmap itempicture;



}
