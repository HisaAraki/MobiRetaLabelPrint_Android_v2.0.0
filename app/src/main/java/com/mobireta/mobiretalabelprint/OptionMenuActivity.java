package com.mobireta.mobiretalabelprint;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class OptionMenuActivity extends AppCompatActivity {

    private MsControl mscontrol;
    private String kanrikbn;
    private String Licenseid;
    TextView txOptiUser;
    TextView txOptiMaster;
    TextView txOptiLabel;
    TextView txOptiDB;
    TextView txOptiAppli;
    TextView txOptiMail;
    TextView txOptiPrivate;
    TextView txOptiOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_menu);
        txOptiUser = (TextView) findViewById(R.id.txOptiUser);
        txOptiMaster = (TextView) findViewById(R.id.txOptiMaster);
        txOptiLabel = (TextView) findViewById(R.id.txOptiLabel);
        txOptiDB = (TextView) findViewById(R.id.txOptiDB);
        txOptiAppli = (TextView) findViewById(R.id.txOptiAppli);
        txOptiMail = (TextView) findViewById(R.id.txOptiMail);
        txOptiPrivate = (TextView) findViewById(R.id.txOptiPrivate);
        txOptiOpen = (TextView) findViewById(R.id.txOptiOpen);
        mscontrol = MsControlHelper.finddata(this);
        txOptiUser.setEnabled(true);
        txOptiMaster.setEnabled(true);
        txOptiDB.setEnabled(true);
        txOptiLabel.setEnabled(true);
        txOptiAppli.setEnabled(true);
        txOptiMail.setEnabled(true);
        txOptiPrivate.setEnabled(true);
        txOptiOpen.setEnabled(true);
        mscontrol = MsControlHelper.finddata(this);
        kanrikbn = mscontrol.Kanrikbn.toString();

        txOptiMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txOptiMaster.setEnabled(false);
                Intent intent = new Intent(OptionMenuActivity.this, MasterActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        txOptiUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mscontrol.Kaishakj.toString().equals("")){
                    Toast toast = Toast.makeText(OptionMenuActivity.this, "マスタ登録を行ってください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else if (mscontrol.Kanrikbn.toString().equals("1")){
                    Toast toast = Toast.makeText(OptionMenuActivity.this, "この端末のユーザ登録すでに終了しています。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else{
                    txOptiUser.setEnabled(false);
                    Intent intent = new Intent(OptionMenuActivity.this, GoogleLoginActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        });

        txOptiLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mscontrol.Kaishakj.toString().equals("")){
                    Toast toast = Toast.makeText(OptionMenuActivity.this, "マスタ登録を行ってください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else if (!mscontrol.Kanrikbn.toString().equals("1")){
                    Toast toast = Toast.makeText(OptionMenuActivity.this, "この機能には、ユーザ登録が必要です。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else{
                    txOptiLabel.setEnabled(false);
                    Intent intent = new Intent(OptionMenuActivity.this, LabelActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        });

        txOptiDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mscontrol.Kaishakj.toString().equals("")){
                    Toast toast = Toast.makeText(OptionMenuActivity.this, "マスタ登録を行ってください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else if (!mscontrol.Kanrikbn.toString().equals("1")){
                    Toast toast = Toast.makeText(OptionMenuActivity.this, "この機能には、ユーザ登録が必要です。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else{
                    txOptiDB.setEnabled(false);
                    Intent intent = new Intent(OptionMenuActivity.this, DbSettingActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        });

        txOptiAppli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txOptiAppli.setEnabled(false);
                Intent intent = new Intent(OptionMenuActivity.this, WebviewActivity.class);
                intent.putExtra("WEBACTION", "APPLICATION");
                startActivityForResult(intent, 0);
            }
        });

        txOptiMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mscontrol.Kaishakj.toString().equals("")){
                    Toast toast = Toast.makeText(OptionMenuActivity.this, "マスタ登録を行ってください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else if (!mscontrol.Kanrikbn.toString().equals("1")){
                    Toast toast = Toast.makeText(OptionMenuActivity.this, "お問い合わせには、ユーザ登録が必要です。" , Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else{
                    txOptiMail.setEnabled(false);
                    int versionCode = BuildConfig.VERSION_CODE;
                    String versionName = BuildConfig.VERSION_NAME;
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SENDTO);
                    intent.setType("text/plain");
                    intent.setData(Uri.parse("mailto:androidsupport@mobireta.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "ラベルプリントお問い合わせ");
                    //本文作成
                    String honbun = "Appli Name" + String.valueOf(versionCode) + "\r\n";
                    honbun = honbun + "Appli ver" + String.valueOf(versionCode) + "\r\n";
                    honbun = honbun + "** 下記に内容を記載してください。**"  + "\r\n";
                    honbun = honbun + "\r\n";
                    intent.putExtra(Intent.EXTRA_TEXT, honbun);
                    startActivity(Intent.createChooser(intent, null));
                    txOptiMail.setEnabled(true);
                }
            }
        }
        );

        txOptiPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txOptiPrivate.setEnabled(false);
                Intent intent = new Intent(OptionMenuActivity.this, WebviewActivity.class);
                intent.putExtra("WEBACTION", "PRIVATE");
                startActivityForResult(intent, 0);
            }
        });

        txOptiOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txOptiOpen.setEnabled(false);
                Intent intent = new Intent(OptionMenuActivity.this, WebviewActivity.class);
                intent.putExtra("WEBACTION", "OPENSOURCE");
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mscontrol = MsControlHelper.finddata(this);
        kanrikbn = mscontrol.Kanrikbn.toString();
        txOptiUser.setEnabled(true);
        txOptiMaster.setEnabled(true);
        txOptiDB.setEnabled(true);
        txOptiLabel.setEnabled(true);
        txOptiAppli.setEnabled(true);
        txOptiMail.setEnabled(true);
        txOptiPrivate.setEnabled(true);
        txOptiOpen.setEnabled(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //==== キーコード判定 ====//
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //-==- Backキー -==-//
            // 以降の処理をキャンセルする。
            Intent intent = new Intent(OptionMenuActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
