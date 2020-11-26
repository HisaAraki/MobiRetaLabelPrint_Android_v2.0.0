package com.mobireta.mobiretalabelprint;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;

public class LicenseActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    WebView licensewebView;
    Button BtnLicense;
    CheckBox CbLicense;
    String Soucehtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("モバリテ　利用規約");
        setContentView(R.layout.activity_license);
        BtnLicense = (Button) findViewById(R.id.BtnLicense);
        CbLicense = (CheckBox) findViewById(R.id.CbLicense);
        licensewebView = (WebView)findViewById(R.id.licensewebView);
        //webaction = intent.getStringExtra("WEBACTION");
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo info = cm.getActiveNetworkInfo();
        licensewebView.setWebViewClient(new WebViewClient());
        licensewebView.getSettings().setJavaScriptEnabled(true);
        //licensewebView.getSettings().setBuiltInZoomControls(true);
        licensewebView.loadUrl(getString(R.string.license));
        BtnLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnLicense.setEnabled(false);
                if (!CbLicense.isChecked()) {
                    Toast toast = Toast.makeText(LicenseActivity.this, "\n同意するには、チェックボックスを\n\nタップして利用規約に同意してください。\n", Toast.LENGTH_LONG);toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    BtnLicense.setEnabled(true);
                    //return;
                }
                else {
                    //saveFile(getString(R.string.permitelicense), src);
                    InitializeConfig initializeConfig = new InitializeConfig();
                    initializeConfig.Initialize(getApplicationContext());
                    Intent intent = new Intent(LicenseActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //==== キーコード判定 ====//
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //-==- Backキー -==-//
            Toast toast = Toast.makeText(LicenseActivity.this, "\n利用許諾に同意できない場合には、\n\nアプリを終了させてください。\n", Toast.LENGTH_LONG);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
