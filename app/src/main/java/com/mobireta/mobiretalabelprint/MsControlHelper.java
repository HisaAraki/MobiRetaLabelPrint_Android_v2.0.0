package com.mobireta.mobiretalabelprint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MsControlHelper {

    public static String TABLE_NAME = "MS_CONTROL";

    //private static final String DB_PASSWD = "AcrPOSClu";

    public static String getKanriid(Context context) {
        SQLiteDatabase db = getReadableDB(context);
        String kanriid = "";
        Cursor cursor = db.rawQuery("select kanriid from " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            kanriid = cursor.getString(0);
        }
        return kanriid;
    }

    public static MsControl finddata(Context context) {
        SQLiteDatabase db = getReadableDB(context);
        MsControl msc = new MsControl();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            msc.Kanriid = cursor.getString(0);
            msc.Kanrikbn = cursor.getString(1);
            msc.Tourokuid = cursor.getString(2);
            msc.Tenpocd = cursor.getString(3);
            msc.Mobilecd = cursor.getString(4);
            msc.Kaishakj = cursor.getString(5);
            msc.Zipcd = cursor.getString(6);
            msc.Address1 = cursor.getString(7);
            msc.Address2 = cursor.getString(8);
            msc.Tel = cursor.getString(9);
            msc.Fax = cursor.getString(10);
            msc.Readerkbn = cursor.getString(11);
            msc.Readernm = cursor.getString(12);
            msc.Readeraddress = cursor.getString(13);
            msc.Keyboardkbn = cursor.getString(14);
            msc.Printerkbn = cursor.getString(15);
            msc.Printernm = cursor.getString(16);
            msc.Printeraddress = cursor.getString(17);
            msc.Nefudaformat = cursor.getString(18);
            msc.Shomiformat = cursor.getString(19);
            msc.Nebikiformat = cursor.getString(20);
            msc.Barcode128 = cursor.getString(21);
            msc.Maxmaisu = cursor.getString(22);
            msc.Addymd = cursor.getString(23);
            msc.Updymd = cursor.getString(24);
        }
        return msc;
    }

    public static List<MsControl> findAll(Context context) {
        SQLiteDatabase db = getReadableDB(context);		// データベース取得
        List<MsControl> msclist = new ArrayList<MsControl>();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                MsControl msc = new MsControl();
                msc.Kanriid = cursor.getString(0);
                msc.Kanrikbn = cursor.getString(1);
                msc.Tourokuid = cursor.getString(2);
                msc.Tenpocd = cursor.getString(3);
                msc.Mobilecd = cursor.getString(4);
                msc.Kaishakj = cursor.getString(5);
                msc.Zipcd = cursor.getString(6);
                msc.Address1 = cursor.getString(7);
                msc.Address2 = cursor.getString(8);
                msc.Tel = cursor.getString(9);
                msc.Fax = cursor.getString(10);
                msc.Readerkbn = cursor.getString(11);
                msc.Readernm = cursor.getString(12);
                msc.Readeraddress = cursor.getString(13);
                msc.Keyboardkbn = cursor.getString(14);
                msc.Printerkbn = cursor.getString(15);
                msc.Printernm = cursor.getString(16);
                msc.Printeraddress = cursor.getString(17);
                msc.Nefudaformat = cursor.getString(18);
                msc.Shomiformat = cursor.getString(19);
                msc.Nebikiformat = cursor.getString(20);
                msc.Barcode128 = cursor.getString(21);
                msc.Maxmaisu = cursor.getString(22);
                msc.Addymd = cursor.getString(23);
                msc.Updymd = cursor.getString(24);
                msclist.add(msc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return msclist;
    }

    public static long insert(Context context, MsControl msc) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("KANRIID", msc.Kanriid);
        values.put("KANRIKBN", msc.Kanrikbn);
        values.put("TOUROKUID", msc.Tourokuid);
        values.put("TENPOCD", msc.Tenpocd);
        values.put("MOBILECD", msc.Mobilecd);
        values.put("KAISHAKJ" , msc.Kaishakj);
        values.put("ZIPCD" , msc.Zipcd);
        values.put("ADDRESS1" , msc.Address1);
        values.put("ADDRESS2" , msc.Address2);
        values.put("TEL" , msc.Tel);
        values.put("FAX" , msc.Fax);
        values.put("READERKBN", msc.Readerkbn);
        values.put("READERNM", msc.Readernm);
        values.put("READERKADDRESS", msc.Readeraddress);
        values.put("KEYBOARDKBN", msc.Keyboardkbn);
        values.put("PRINTERKBN", msc.Printerkbn);
        values.put("PRINTERNM", msc.Printernm);
        values.put("PRINTERADDRESS", msc.Printeraddress);
        values.put("NEFUDAFORMAT", msc.Nefudaformat);
        values.put("SHOMIFORMAT", msc.Shomiformat);
        values.put("NEBIKIFORMAT", msc.Nebikiformat);
        values.put("BARCODE128", msc.Barcode128);
        values.put("MAXMAISU", msc.Maxmaisu);
        values.put("ADDYMD", msc.Addymd);
        values.put("UPDYMD", msc.Updymd);
        return db.insert(TABLE_NAME, null, values);
    }

    public static long update(Context context, MsControl msc) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("KANRIKBN", msc.Kanrikbn);
        values.put("TOUROKUID", msc.Tourokuid);
        values.put("TENPOCD", msc.Tenpocd);
        values.put("MOBILECD", msc.Mobilecd);
        values.put("KAISHAKJ" , msc.Kaishakj);
        values.put("ZIPCD" , msc.Zipcd);
        values.put("ADDRESS1" , msc.Address1);
        values.put("ADDRESS2" , msc.Address2);
        values.put("TEL" , msc.Tel);
        values.put("FAX" , msc.Fax);
        values.put("READERKBN", msc.Readerkbn);
        values.put("READERNM", msc.Readernm);
        values.put("READERKADDRESS", msc.Readeraddress);
        values.put("KEYBOARDKBN", msc.Keyboardkbn);
        values.put("PRINTERKBN", msc.Printerkbn);
        values.put("PRINTERKNM", msc.Printernm);
        values.put("PRINTERADDRESS", msc.Printeraddress);
        values.put("NEFUDAFORMAT", msc.Nefudaformat);
        values.put("SHOMIFORMAT", msc.Shomiformat);
        values.put("NEBIKIFORMAT", msc.Nebikiformat);
        values.put("BARCODE128", msc.Barcode128);
        values.put("MAXMAISU", msc.Maxmaisu);
        values.put("UPDYMD", msc.Updymd);
        //return db.update(TABLE_NAME, values, null, null);
        return db.update(TABLE_NAME, values, "KANRIID = ?", new String[]{String.valueOf(msc.Kanriid)});
    }

    public static long settingupdate(Context context, MsControl msc) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("KANRIID", msc.Kanriid);
        values.put("KANRIKBN", msc.Kanrikbn);
        values.put("MOBILECD", msc.Mobilecd);
        values.put("KAISHAKJ" , msc.Kaishakj);
        values.put("ZIPCD" , msc.Zipcd);
        values.put("ADDRESS1" , msc.Address1);
        values.put("ADDRESS2" , msc.Address2);
        values.put("TEL" , msc.Tel);
        values.put("FAX" , msc.Fax);
        values.put("UPDYMD", msc.Updymd);
        //return db.update(TABLE_NAME, values, "TENPOCD = ?", new String[]{String.valueOf(msc.Tenpocd)});
        return db.update(TABLE_NAME, values, "KANRIID = ?", new String[]{String.valueOf(msc.Kanriid)});
    }

    public static long printerupdate(Context context, MsControl msc) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("PRINTERKBN", msc.Printerkbn);
        values.put("PRINTERNM", msc.Printernm);
        values.put("PRINTERADDRESS", msc.Printeraddress);
        values.put("UPDYMD", msc.Updymd);
        //return db.update(TABLE_NAME, values, "TENPOCD = ?", new String[]{String.valueOf(msc.Tenpocd)});
        return db.update(TABLE_NAME, values, "KANRIID = ?", new String[]{String.valueOf(msc.Kanriid)});
    }

    public static long barcodeupdate(Context context, MsControl msc) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("KEYBOARDKBN", msc.Keyboardkbn);
        values.put("READERKBN", msc.Readerkbn);
        values.put("READERNM", msc.Readernm);
        values.put("READERKADDRESS", msc.Readeraddress);
        values.put("NEFUDAFORMAT", msc.Nefudaformat);
        values.put("SHOMIFORMAT", msc.Shomiformat);
        values.put("NEBIKIFORMAT", msc.Nebikiformat);
        values.put("BARCODE128", msc.Barcode128);
        values.put("MAXMAISU", msc.Maxmaisu);
        values.put("UPDYMD", msc.Updymd);
        //return db.update(TABLE_NAME, values, "TENPOCD = ?", new String[]{String.valueOf(msc.Tenpocd)});
        return db.update(TABLE_NAME, values, "KANRIID = ?", new String[]{String.valueOf(msc.Kanriid)});
    }

    public static long labelupdate(Context context, MsControl msc) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("READERKBN", msc.Readerkbn);
        values.put("KEYBOARDKBN", msc.Keyboardkbn);
        values.put("NEFUDAFORMAT", msc.Nefudaformat);
        values.put("SHOMIFORMAT", msc.Shomiformat);
        values.put("NEBIKIFORMAT", msc.Nebikiformat);
        values.put("BARCODE128", msc.Barcode128);
        values.put("MAXMAISU", msc.Maxmaisu);
        values.put("UPDYMD", msc.Updymd);
        //return db.update(TABLE_NAME, values, "TENPOCD = ?", new String[]{String.valueOf(msc.Tenpocd)});
        return db.update(TABLE_NAME, values, "KANRIID = ?", new String[]{String.valueOf(msc.Kanriid)});
    }

    public static long kanriupdate(Context context, MsControl msc) {
        SQLiteDatabase db = getWritableDB(context);		// データベース取得
        ContentValues values = new ContentValues();
        values.put("KANRIKBN", msc.Kanrikbn);
        values.put("TOUROKUID", msc.Tourokuid);
        values.put("UPDYMD", msc.Updymd);
        return db.update(TABLE_NAME, values, "KANRIID = ?", new String[]{String.valueOf(msc.Kanriid)});
    }

    public static long delete(Context context, MsControl item) {
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
