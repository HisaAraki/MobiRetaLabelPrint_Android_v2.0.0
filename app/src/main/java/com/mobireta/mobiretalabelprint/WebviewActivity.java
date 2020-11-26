package com.mobireta.mobiretalabelprint;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class WebviewActivity extends AppCompatActivity {

    WebView webView;
    private String webaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = (WebView)findViewById(R.id.webView1);
        Intent intent = getIntent();
        webaction = intent.getStringExtra("WEBACTION");
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            Log.d("DEBUG", "接続されています");
            //jacascriptを許可する
            //リンクをタップしたときに標準ブラウザを起動させない
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            if (webaction.equals("APPLICATION")){
                webView.loadUrl(getString(R.string.server_address) + "/html/license.html");
            }else if (webaction.equals("PRIVATE")){
                webView.loadUrl(getString(R.string.server_address) + "/html/privacypolicy.html");
            }else if (webaction.equals("OPENSOURCE")){
                webView.loadUrl(getString(R.string.server_address) + "/html/opensource.html");
            }
        } else {
            if (webaction.equals("APPLICATION")){
                webView.loadUrl("file:///android_asset/license.html");
            }else if (webaction.equals("PRIVATE")){
                webView.loadUrl("file:///android_asset/privacypolicy.html");
            }else if (webaction.equals("OPENSOURCE")){
                webView.loadUrl("file:///android_asset/opensource.html");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //==== キーコード判定 ====//
            Intent intent = new Intent(WebviewActivity.this, OptionMenuActivity.class);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ネットワーク接続確認
    public static boolean netWorkCheck(Context context){
        ConnectivityManager cm =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if( info != null ){
            return info.isConnected();
        } else {
            return false;
        }
    }
}
