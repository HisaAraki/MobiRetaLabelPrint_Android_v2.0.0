package com.mobireta.mobiretalabelprint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    // データベース名の指定
    private static final String DB_NAME = "mobiretalabel.db";
    // データベースのバージョン指定
    private static final int DB_VERSION = 2;
    public DatabaseOpenHelper(Context context) {super(context, DB_NAME, null, DB_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE MS_USER ( " +
                        " USERID TEXT NOT NULL, " +
                        " IDTOKEN TEXT, " +
                        " FULLNAME TEXT, " +
                        " NAMAE TEXT, " +
                        " SEIMEI TEXT, " +
                        " EMAIL TEXT, " +
                        " ADDYMD TEXT, " +
                        " UPDYMD TEXT " +
                        ")";
        db.execSQL(createTable);
        createTable =
                "CREATE TABLE MS_CONTROL ( " +
                        " KANRIID TEXT NOT NULL, " +
                        " KANRIKBN TEXT NOT NULL, " +
                        " TOUROKUID TEXT NOT NULL, " +
                        " TENPOCD TEXT NOT NULL, " +
                        " MOBILECD TEXT NOT NULL, " +
                        " KAISHAKJ TEXT, " +
                        " ZIPCD TEXT, " +
                        " ADDRESS1 TEXT, " +
                        " ADDRESS2 TEXT, " +
                        " TEL TEXT, " +
                        " FAX TEXT, " +
                        " READERKBN TEXT, " +
                        " READERNM TEXT , " +
                        " READERKADDRESS TEXT, " +
                        " KEYBOARDKBN TEXT, " +
                        " PRINTERKBN TEXT, " +
                        " PRINTERNM TEXT, " +
                        " PRINTERADDRESS TEXT, " +
                        " NEFUDAFORMAT TEXT, " +
                        " SHOMIFORMAT TEXT, " +
                        " NEBIKIFORMAT TEXT, " +
                        " BARCODE128 TEXT, " +
                        " MAXMAISU TEXT, " +
                        " ADDYMD TEXT, " +
                        " UPDYMD TEXT " +
                        ")";
        db.execSQL(createTable);
        //ラベル商品ログ
        createTable =
                "CREATE TABLE TR_SHOHINLOG ( " +
                        " TENPOCD TEXT NOT NULL, " +
                        " SHOHINCD TEXT NOT NULL, " +
                        " SHOHINKJ TEXT, " +
                        " SELL TEXT, " +
                        " SHOMIDAY TEXT, " +
                        " ADDYMD TEXT " +
                        ")";
        db.execSQL(createTable);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // データベースの変更が生じた場合は、ここに処理を記述する。
    }
}
