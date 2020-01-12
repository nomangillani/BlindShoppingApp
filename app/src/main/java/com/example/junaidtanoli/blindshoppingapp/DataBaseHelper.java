package com.example.junaidtanoli.blindshoppingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "id10574130_gsmbasedhomeappliancescontroller.db";
    public static final String TABLE_NAME = "status";
    public static final String COL_1 = "status_id";
    public static final String COL_2 = "pid";
    public static final String COL_3 = "quantity";
    public static final String COL_4 = "appliances_status";
    public static final String COL_5 = "date";
    public static final String COL_6 = "time";
    public DataBaseHelper(Context context){super(context,DATABASE_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table " + TABLE_NAME +" (status_id INTEGER PRIMARY KEY AUTOINCREMENT,pid TEXT,quantity TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String pid,String quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,pid);
        contentValues.put(COL_3,quantity);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

    public void deleteRow(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+COL_1+"='"+value+"'");
        db.close();
    }

    public void delete(String id)
    {
        String[] args={id};
        getWritableDatabase().delete("texts", "_ID=?", args);
    }
}
