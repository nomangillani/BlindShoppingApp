package com.example.junaidtanoli.blindshoppingapp.DataClasses;

public class showallordersclass {
    String desc,name,price;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    int oid,aid;
    String image,username,usercontact,useraddress,quantity;

    public String getDesc() {
        return desc;
    }

    public String getUsername() {
        return username;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsercontact() {
        return usercontact;
    }

    public void setUsercontact(String usercontact) {
        this.usercontact = usercontact;
    }

    public String getUseraddress() {
        return useraddress;
    }

    public void setUseraddress(String useraddress) {
        this.useraddress = useraddress;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public showallordersclass(int id,String name, String desc, String price, String image,int aid)
    {
        this.desc=desc;
        this.image=image;
        this.aid=aid;
        this.oid=id;
        this.name= name;
        this.price=price;
        this.useraddress=useraddress;
        this.usercontact=usercontact;
        this.username=username;
        this.quantity=quantity;
    }

    public showallordersclass(int id,String name, String desc, String price, String image)
    {
        this.desc=desc;
        this.image=image;
        this.oid=id;
        this.name= name;
        this.price=price;
        this.useraddress=useraddress;
        this.usercontact=usercontact;
        this.username=username;
        this.quantity=quantity;

    }

}
