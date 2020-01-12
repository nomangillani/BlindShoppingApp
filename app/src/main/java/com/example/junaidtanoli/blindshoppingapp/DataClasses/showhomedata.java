package com.example.junaidtanoli.blindshoppingapp.DataClasses;

public class showhomedata {
    String desc,name,price,image;
    int id;
    public String getDesc() {
        return desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    public showhomedata(){}

    public showhomedata(String name, String desc, String price, String image,int id)
    {
        this.desc=desc;
        this.image=image;
        this.name= name;
        this.id=id;
        this.price=price;

    }
    public showhomedata(String name,int id)
    {
        this.name= name;
        this.id=id;
    }
}
