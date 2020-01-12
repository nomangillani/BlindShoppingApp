package com.example.junaidtanoli.blindshoppingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 62;

    // Database Name
    private static final String DATABASE_NAME = "MyProjectDB";

    private static final String USER_TABLE = "theuser";

    private static final String ITEMS_TABLE = "theitems";

    private static final String ITEMS_NAMES_TABLE = "itemsnames";

    private static final String ORDERS_TABLE = "theorders";

    private static final String ORDERS_DETAILS = "ordersdetails";

    private static final String CUSTOMER_TABLE = "thecustomer";

    //columns for customer table
    private static final String CUSTOMER_PHONE = "customer_phone";
    private static final String CUSTOMER_NAME = "customer_name";

    //columns for user table
    private static final String USER_ID = "USER_ID";
    private static final String USER_FNAME = "userfname";
    private static final String USER_LNAME = "userlname";
    private static final String USER_EMAIL = "useremail";
    private static final String USER_PASS = "userpass";
    private static final String USER_PHONE = "userphone";
    private static final String USER_PICTURE = "userpicture";
    private static final String USER_STATUS = "userstatus";

    // columns for Items Table and for Orders
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_CATEGORY = "item_category";
    private static final String ITEM_NAME= "item_name";
    private static final String ITEM_PRICE = "item_price";
    private static final String ITEM_QUANTITY = "item_quantity";
    private static final String ITEM_DISCOUNT = "item_discount";


    private static final String ITEM_PICTURE = "item_picture";

    //additional columns for table Orders

    private static final String ORDER_ID = "order_id";
    private static final String ORDER_QUANTITY = "order_quantity";

    private static final String ORDER_NAME = "order_name";
    private static final String ORDER_ITEMID = "order_itemid";
    private static final String ORDER_IMAGE = "order_image";
    private static final String ORDER_DESC = "order_desc";
    private static final String ORDER_STATUS = "order_status";

    private static final String ORDER_COUNT = "order_count";



    public DBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//users table
     //   db.execSQL("CREATE TABLE THEUSER (USEREMAIL TEXT PRIMARY KEY,USERPASS TEXT,USERFNAME TEXT,USERLNAME TEXT,USERPHONE TEXT,USERPICTURE TEXT,USERSTATUS INTEGER)");

//items table
      //  db.execSQL("CREATE TABLE theitems (item_category TEXT,item_name TEXT PRIMARY KEY,item_price FLOAT,item_quantity INTEGER,item_picture TEXT,item_discount INTEGER)");

//orders table
        db.execSQL("CREATE TABLE theorders (ORDER_ID INTEGER PRIMARY KEY,itemid INTEGER,quantity INTEGER,itemname TEXT,itemdesc TEXT,itemimage TEXT)");

        //orders details
     //   db.execSQL("CREATE TABLE ordersdetails (ORDER_ID INTEGER,item_name TEXT,item_price FLOAT,item_quantity INTEGER,item_picture TEXT,item_discount INTEGER)");

        //orders details
     //   db.execSQL("CREATE TABLE itemsnames (ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT,item_category TEXT)");

        //Customer details
     //   db.execSQL("CREATE TABLE thecustomer (customer_phone TEXT PRIMARY KEY,customer_name TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE);
        onCreate(db);

    }

    public boolean loginuser(User user)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from THEUSER where USEREMAIL = '"+user.getUseremail()+"'and USERPASS = '"+user.getUserpass()+"'",null);
        theorder item = new theorder();
        if(cursor.moveToFirst()) {
            SQLiteDatabase DB = this.getWritableDatabase();
            String strSQL = "UPDATE THEUSER SET USERSTATUS='" + 1 + "' WHERE USEREMAIL ='" + user.getUseremail() + "'";
            DB.execSQL(strSQL);
            Log.d("get",cursor.getString(0));
            db.close();
            return true;
        }
        else
        {
            db.close();
            return false;
        }

    }
    public boolean logout(User user)
    {
            SQLiteDatabase DB = this.getWritableDatabase();
            String strSQL = "UPDATE THEUSER SET USERSTATUS='" + 0 + "' WHERE USEREMAIL ='" + user.getUseremail() + "'";
            DB.execSQL(strSQL);

            DB.close();
            return true;
    }

    public User checkuser()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from THEUSER where USERSTATUS = '"+1+"'",null);
        User user = new User();
        if(cursor.moveToFirst()) {
            user.setUseremail(cursor.getString(0));
            user.setUserpass(cursor.getString(1));
            user.setUserfname(cursor.getString(2));
            user.setUserlname(cursor.getString(3));
            user.setUserphone(cursor.getString(4));
            user.setUserpicture(Utility.stringToBitmap(cursor.getString(5)));
            db.close();
            return user;
        }
        else
        {
            db.close();
            return null;
        }
    }


    public boolean addcustomer(User user)
    {
        try{
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(CUSTOMER_PHONE,user.getCustomerphone());
            contentValues.put(CUSTOMER_NAME,user.getCustomername());

            db.insert(CUSTOMER_TABLE,null,contentValues);
            db.close(); // Closing database connection

            return true;
        }
        catch (Exception er)
        {
            return false;
        }
    }

    public ArrayList<User> getallcustomers()
    {
        ArrayList<User> allcustomers = new ArrayList<User>();
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery("select * from thecustomer",null);

        Log.d("customer",cursor.getCount()+" ");

        if(cursor.moveToFirst())
        {
            do {
                User customer = new User();

                Log.d("getcustomerid",cursor.getString(0));

                customer.setCustomerphone(cursor.getString(0));
                customer.setCustomername(cursor.getString(1));

                allcustomers.add(customer);
            }while (cursor.moveToNext());
        }

        db.close();
        return allcustomers;
    }

    public ArrayList<User> getallcustomers(String cid)
    {
        ArrayList<User> allcustomers = new ArrayList<User>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from "+CUSTOMER_TABLE+" where customer_phone ='"+cid+"' ",null);

        Log.d("customer",cursor.getCount()+" ");

        if(cursor.moveToFirst())
        {
            do {
                User customer = new User();

                Log.d("getcustomerid",cursor.getString(0));

                customer.setCustomerphone(cursor.getString(0));
                customer.setCustomername(cursor.getString(1));

                allcustomers.add(customer);
            }while (cursor.moveToNext());
        }

        db.close();
        return allcustomers;
    }


    public boolean adduser(User user)
    {
        try{
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(USER_EMAIL,user.getUseremail());
            contentValues.put(USER_PASS,user.getUserpass());
            contentValues.put(USER_FNAME,user.getUserfname());
            contentValues.put(USER_LNAME,user.getUserlname());
            contentValues.put(USER_PHONE,user.getUserphone());
            contentValues.put(USER_PICTURE,Utility.bitmapToString(user.getUserpicture()));

            contentValues.put(USER_STATUS,0);

            db.insert(USER_TABLE,null,contentValues);
            db.close(); // Closing database connection

            return true;
        }
        catch (Exception er)
        {
            return false;
        }
    }

    public boolean additem(theorder order)
    {
        try{

            ArrayList<String> allitems = new ArrayList<String>();
            SQLiteDatabase thedb = this.getReadableDatabase();

          //  db.execSQL("CREATE TABLE theitems (item_category TEXT,item_name TEXT PRIMARY KEY,item_price FLOAT,item_quantity INTEGER,item_picture TEXT,item_discount INTEGER)");


            Cursor cursor = thedb.rawQuery("select * from itemsnames where item_category = '"+order.getItemcategory().trim().toLowerCase().toString()+"'",null);

            Log.d("itemcount",cursor.getCount()+" ");

            if(!cursor.moveToFirst())
            {
                SQLiteDatabase db = this.getWritableDatabase();

                ContentValues contentValues = new ContentValues();

                contentValues.put(ITEM_CATEGORY,order.getItemcategory().trim().toLowerCase().toString());

                db.insert(ITEMS_NAMES_TABLE,null,contentValues);

            }

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(ITEM_CATEGORY,order.getItemcategory());
            contentValues.put(ITEM_NAME,order.getItemname());
            contentValues.put(ITEM_PRICE,order.getItemprice());
            contentValues.put(ITEM_QUANTITY,order.getItemquantity());

            contentValues.put(ITEM_DISCOUNT,0);

            contentValues.put(ITEM_PICTURE,Utility.bitmapToString(order.getItempicture()));

            db.insert(ITEMS_TABLE,null,contentValues);
            db.close(); // Closing database connection

            return true;
        }
        catch (Exception er)
        {
            return false;
        }
    }

    public ArrayList<String> allcatnames()
    {
        ArrayList<String> allitems = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();



        Cursor cursor = db.rawQuery("select * from itemsnames",null);

        Log.d("itemcount",cursor.getCount()+" ");

        if(cursor.moveToFirst())
        {
            do {
                Log.d("getitem",cursor.getString(0));

                allitems.add(cursor.getString(1));
            }while (cursor.moveToNext());
        }

        db.close();
        return allitems;
    }

    public boolean updateordercount(Integer orderid) {
        SQLiteDatabase thedb = this.getReadableDatabase();

        int count = 0;

        Cursor cursor = thedb.rawQuery("select * from theorders where ORDER_ID = '"+orderid+"'",null);

        Log.d("itemcount",cursor.getCount()+" ");

        if(cursor.moveToFirst())
        {
            int acount = Integer.parseInt(cursor.getString(6));
            acount++;
            SQLiteDatabase DB = this.getWritableDatabase();
            Log.e("thecounterhere",acount+" ");
            String strSQL = "UPDATE theorders SET order_count='" + acount + "' WHERE ORDER_ID ='" + orderid + "'";
            DB.execSQL(strSQL);


            return true;
        }

        return false;
    }
    public boolean addorder(theorder order)
    {

        SQLiteDatabase thedb = this.getReadableDatabase();

        Cursor cursor = thedb.rawQuery("select * from theorders where ORDER_ID = '"+order.getOrderid()+"'",null);

        Log.d("orderexist",cursor.getCount()+" ");

        if(cursor.moveToFirst())
        {
            Log.e("alreadyexisted","Already Existed");
            return false;
        }
        else
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ORDER_IMAGE,order.getItemimage());
            contentValues.put(ORDER_DESC,order.getItemdesc());
            contentValues.put(ORDER_NAME,order.getItemname());
            contentValues.put(ORDER_ID,order.getOrderid());
            contentValues.put(ORDER_QUANTITY,order.getItemquantity());
            contentValues.put(ORDER_ITEMID,order.getItemid());
//            contentValues.put(ORDER_TIME,order.getOrdertime());
//
//            contentValues.put(ORDER_STATUS,"Pending");
//            contentValues.put(CUSTOMER_PHONE,User.cid);
//            contentValues.put(ORDER_COUNT,0);
            db.insert(ORDERS_TABLE,null,contentValues);
            db.close(); // Closing database connection
            return true;
        }
    }
    public boolean deleteorder(String orderid)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.e("oid",orderid);
        db.delete(ORDERS_TABLE,"ORDER_ID= ? ",new String[]{orderid});

        db.delete(ORDERS_DETAILS,"ORDER_ID= ? ",new String[]{orderid});
        db.close();
        return true;
    }
    public boolean deleteitemfromorder(String itemname,String orderid)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ORDERS_DETAILS,"item_name= ? AND ORDER_ID = ?",new String[]{itemname,orderid+""});
        db.close();
        return true;
    }
    public boolean updatequantity(String itemname,Integer quantity) {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from theitems where item_name = '"+itemname+"'",null);

        String strSQL = "UPDATE theitems SET item_quantity='" + quantity + "' WHERE item_name ='" + itemname + "'";

        DB.execSQL(strSQL);
        return true;
    }
    public ArrayList<theorder> getallorders()
    {
        ArrayList<theorder> allitems = new ArrayList<theorder>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from theorders",null);

        if(cursor.moveToFirst())
        {
            do {
                theorder item = new theorder();
                item.setOrderid(Integer.parseInt(cursor.getString(0)));
                Log.d("get",cursor.getString(0));
                item.setItemid(Integer.parseInt(cursor.getString(1)));
                item.setItemquantity(Integer.parseInt(cursor.getString(2)));
                item.setItemname(cursor.getString(3));
                item.setItemdesc(cursor.getString(4));
                item.setItemimage(cursor.getString(5));
                item.setItemdiscount(Integer.parseInt(cursor.getString(5)));
                allitems.add(item);
            }while (cursor.moveToNext());
        }

        db.close();
        return allitems;
    }
}
