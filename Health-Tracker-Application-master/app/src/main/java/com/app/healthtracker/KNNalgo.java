package com.app.healthtracker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.List;

public class KNNalgo extends SQLiteOpenHelper {
    private String CREATE_TABLE_DIABETES="CREATE TABLE IF NOT EXISTS Diabetes (id INTEGER PRIMARY KEY, pregnant INTEGER,glucose INTEGER, " +
            "bp INTEGER, skin INTEGER, insulin INTEGER, bmi REAL, diabet REAL,age INTEGER, outcome INTEGER)";
    private static KNNalgo mInstance=null;

    public static KNNalgo getInstance(Context context){
        if(mInstance==null){
            mInstance=new KNNalgo(context);
        }
        return mInstance;
    }
    private KNNalgo(@Nullable Context context) {
        super(context, "HealthTracker", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TABLE_DIABETES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void bulkInsertDataset(List<String[]> list){
        SQLiteDatabase db=getWritableDatabase();
        for(String[] data:list) {
            ContentValues values=new ContentValues();
            values.put("pregnant",data[0]);
            values.put("glucose",data[1]);
            values.put("bp",data[2]);
            values.put("skin",data[3]);
            values.put("insulin",data[4]);
            values.put("bmi",data[5]);
            values.put("diabet",data[6]);
            values.put("age",data[7]);
            values.put("outcome",data[8]);
            db.insert("Diabetes", null, values);
        }
    }

    @SuppressLint("Range")
    public int getDatasetCount(){
        int cnt=0;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.query("Diabetes",new String[]{"count(*) as cnt"},null,null,null,null,null);
        if(cursor!=null && cursor.moveToFirst()){
            cnt=cursor.getInt(cursor.getColumnIndex("cnt"));
            cursor.close();
        }
        return cnt;
    }
    @SuppressLint("Range")
    public int prediction(String[] where){
        int result=0;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.query("Diabetes",new String[]{"outcome"},"pregnant=? AND glucose=? AND bp=? AND skin=? AND insulin=? AND bmi=? AND diabet=? AND age=?",where,null,null,null,"1");
        if(cursor!=null && cursor.moveToFirst()){
            result=cursor.getInt(cursor.getColumnIndex("outcome"));
            cursor.close();
        }
        return result;
    }
}
