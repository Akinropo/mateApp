package com.akinropo.taiwo.coursemate.PrivateClasses;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAIWO on 3/6/2017.
 */
public class RecentChatManager extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "recentChat";
    public static final String DB_COLUMN_ONE = "flag";
    public static final String DB_COLUMN_TWO = "id";
    public static final String DB_COLUMN_THREE = "name";
    public static final String DB_COLUMN_FOUR = "unreadCount";
    public static final String DB_COLUMN_FIVE = "photoUrl";
    public static final String DB_COLUMN_SIX = "timeStamp";
    public static final String DB_COLUMN_SEVEN = "groupOwner";
    Context context;

    public RecentChatManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+DB_NAME+"( "+DB_COLUMN_ONE +" INT,"+DB_COLUMN_TWO+" INT," + DB_COLUMN_THREE+" TEXT,"
                +DB_COLUMN_FOUR+" INT,"+DB_COLUMN_FIVE+" TEXT,"+DB_COLUMN_SIX+" TEXT,"+DB_COLUMN_SEVEN+" INT )";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

    public void addRecent(int flag,int id,String name,int unreadCount,String photoUrl,String timeStamp,int groupOwner){
        if(!checkExist(id)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DB_COLUMN_ONE,flag);
            values.put(DB_COLUMN_TWO,id);
            values.put(DB_COLUMN_THREE,name);
            values.put(DB_COLUMN_FOUR,unreadCount);
            values.put(DB_COLUMN_FIVE,photoUrl);
            values.put(DB_COLUMN_SIX, timeStamp);
            values.put(DB_COLUMN_SEVEN, groupOwner);
            db.insert(DB_NAME, null, values);
        }else {
            updateTimestamp(id,timeStamp,false);
        }
    }
    public boolean checkExist(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + DB_NAME +" WHERE id = ' "+id+"'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }


    }
    public List<RecentChatModel> getRecent(){
        List<RecentChatModel> recentChatModels = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT * FROM "+DB_NAME+" ORDER BY "+DB_COLUMN_SIX+" DESC ";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                int flag = cursor.getInt(0);
                int id = cursor.getInt(1);
                String name = cursor.getString(2);
                int unreadCount = cursor.getInt(3);
                String photoUrl = cursor.getString(4);
                String timeStamp = cursor.getString(5);
                int groupOwner = cursor.getInt(6);
                recentChatModels.add(new RecentChatModel(flag,id,name,unreadCount,photoUrl,timeStamp,groupOwner));
            }while (cursor.moveToNext());
        }
        return recentChatModels;
    }
    public void updateTimestamp(int id,String timeStamp,boolean reset){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(reset){
            values.put(DB_COLUMN_FOUR,0);
        }else {
            int unreadCount = getUnreadCount(id) + 1;
            values.put(DB_COLUMN_FOUR,unreadCount);
        }
        values.put(DB_COLUMN_SIX,timeStamp);
        db.update(DB_NAME,values,DB_COLUMN_TWO +" = ?",new String[]{Integer.toString(id)});
    }
    public int getUnreadCount(int id ){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+DB_NAME +" WHERE id ='"+id+"'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToLast()){
            return cursor.getInt(3);
        }else return 0;
    }
    public void deleteRecent(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_NAME,"?",new String[]{Integer.toString(1)});
        //String query = "DELETE FROM "+DB_NAME+" WHERE 1";
        //db.execSQL(query);
    }

    public boolean isNotification(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+DB_NAME+" WHERE "+DB_COLUMN_FOUR+" > 0 ";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() > 0) return true;
        else return false;
    }
}
