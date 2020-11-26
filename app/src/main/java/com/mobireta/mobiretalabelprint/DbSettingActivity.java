package com.mobireta.mobiretalabelprint;

import android.content.DialogInterface;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DbSettingActivity extends AppCompatActivity {

    private TrShohinLog trshohilog;
    private TrShohinLoglHelper trshohinLoglhelper;

    Button btDbdelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbsetting);
        btDbdelete = (Button) findViewById(R.id.btDbdelete);

        btDbdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDbdelete.setEnabled(false);
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("モバリテ")
                        .setMessage("商品マスタを削除しますか？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TrShohinLoglHelper.delete(getApplicationContext());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                btDbdelete.setEnabled(true);
            }
        });
    }
}
