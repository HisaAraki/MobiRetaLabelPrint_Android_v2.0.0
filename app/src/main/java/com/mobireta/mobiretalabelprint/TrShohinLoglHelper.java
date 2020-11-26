package com.mobireta.mobiretalabelprint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;


public class TrShohinLoglHelper {

    public static String TABLE_NAME = "TR_SHOHINLOG";

    //private static final String DB_PASSWD = "AcrPOSClu";

    public static TrShohinLog finddata(Context context, String Shohincd) {
        SQLiteDatabase db = getReadableDB(context);
        TrShohinLog tsl = new TrShohinLog();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " WHERE SHOHINCD = ?;", new String[]{ Shohincd });
        if (cursor.moveToFirst()) {
            tsl.Tenpocd = cursor.getString(0);
            tsl.Shohincd = cursor.getString(1);
            tsl.Shohinkj = cursor.getString(2);
            tsl.Sell = cursor.getString(3);
            tsl.Shomiday = cursor.getString(4);
            tsl.Addymd = cursor.getString(5);
        }
        return tsl;
    }

    public static long getRecordCount(Context context, String shohincd){
        SQLiteDatabase db = getReadableDB(context);
        long recodeCount = DatabaseUtils.queryNumEntries(db, TABLE_NAME," SHOHINCD = '?' ", new String[]{ shohincd });
        return recodeCount;
    }

    public static long insert(Context context, TrShohinLog tsl) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("TENPOCD", tsl.Tenpocd);
        values.put("SHOHINCD", tsl.Shohincd);
        values.put("SHOHINKJ" , tsl.Shohinkj);
        values.put("SELL" , tsl.Sell);
        values.put("ADDYMD", tsl.Addymd);
        values.put("SHOMIDAY" , tsl.Shomiday);
        values.put("SELL" , tsl.Sell);
        return db.insert(TABLE_NAME, null, values);
    }

    public static long update(Context context, TrShohinLog tsl) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("SHOHINKJ" , tsl.Shohinkj);
        values.put("SELL" , tsl.Sell);
        values.put("SHOMIDAY" , tsl.Shomiday);
        values.put("ADDYMD", tsl.Addymd);
        return db.update(TABLE_NAME, values, "SHOHINCD = " + tsl.Shohincd, null);
    }

    public static long delete(Context context) {
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
