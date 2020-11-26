package com.mobireta.mobiretalabelprint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MsUserHelper {

    public static String TABLE_NAME = "MS_USER";

    //private static final String DB_PASSWD = "AcrPOSClu";

    public static MsUser finddata(Context context, String userid) {
        SQLiteDatabase db = getReadableDB(context);
        MsUser msu = new MsUser();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " WHERE UserID = ?;", new String[]{ userid });
        if (cursor.moveToFirst()) {
            msu.UserID = cursor.getString(0);
            msu.Idtoken = cursor.getString(1);
            msu.Funame = cursor.getString(2);
            msu.Namae = cursor.getString(3);
            msu.Shimei = cursor.getString(4);
            msu.Email = cursor.getString(5);
            msu.Addymd = cursor.getString(6);
            msu.Updymd = cursor.getString(7);
        }
        return msu;
    }

    public static List<MsUser> findAll(Context context) {
        SQLiteDatabase db = getReadableDB(context);		// データベース取得
        List<MsUser> msulist = new ArrayList<MsUser>();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                MsUser msu = new MsUser();
                msu.UserID = cursor.getString(0);
                msu.Idtoken = cursor.getString(1);
                msu.Funame = cursor.getString(2);
                msu.Namae = cursor.getString(3);
                msu.Shimei = cursor.getString(4);
                msu.Email = cursor.getString(5);
                msu.Addymd = cursor.getString(6);
                msu.Updymd = cursor.getString(7);
                msulist.add(msu);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return msulist;
    }

    public static Integer getcount(Context context) {
        SQLiteDatabase db = getReadableDB(context);
        MsUser msu = new MsUser();
        Integer Count = 0;
        Cursor cursor = db.rawQuery("select count(*) as count from " + TABLE_NAME , null);
        if (cursor.moveToFirst()) {
            Count = cursor.getInt(0);
        }
        return Count;
    }

    public static long insert(Context context, MsUser msc) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("USERID", msc.UserID);
        values.put("IDTOKEN", msc.Idtoken);
        values.put("FULLNAME", msc.Funame);
        values.put("NAMAE", msc.Namae);
        values.put("SEIMEI", msc.Shimei);
        values.put("EMAIL", msc.Email);
        values.put("ADDYMD", msc.Addymd);
        values.put("UPDYMD", msc.Updymd);
        return db.insert(TABLE_NAME, null, values);
    }

    public static long update(Context context, MsUser msc) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("IDTOKEN", msc.Idtoken);
        values.put("FULLNAME", msc.Funame);
        values.put("NAMAE", msc.Namae);
        values.put("SEIMEI", msc.Shimei);
        values.put("EMAIL", msc.Email);
        values.put("UPDYMD", msc.Updymd);
        //return db.update(TABLE_NAME, values, null, null);
        return db.update(TABLE_NAME, values, "USERID = ?", new String[]{String.valueOf(msc.UserID)});
    }

    public static long delete(Context context, MsUser item) {
        SQLiteDatabase db = getWritableDB(context);        // データベース取得
        return db.delete(TABLE_NAME, null, null);
    }

    public static long count(Context context) {
        DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public static long count(SQLiteDatabase db) {
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    // 読み取り専用でデータベースを取得
    private static SQLiteDatabase getReadableDB(Context context) {
        DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
        return helper.getReadableDatabase();
    }

    // 書き込み可能でデータベースを取得
    private static SQLiteDatabase getWritableDB(Context context) {
        DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
        return helper.getWritableDatabase();
    }

}
