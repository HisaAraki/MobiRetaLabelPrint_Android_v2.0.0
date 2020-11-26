package com.mobireta.mobiretalabelprint;

import android.content.pm.ActivityInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // 定数
    //private static final int REQUEST_ENABLEBLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード
    //private static final int REQUEST_CONNECTDEVICE   = 2; // デバイス接続要求時の識別コード
    private static final int READBUFFERSIZE          = 1024;    // 受信バッファーのサイズ

    // メンバー変数
    private BluetoothAdapter mBluetoothAdapter;    // BluetoothAdapter : Bluetooth処理で必要

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //横固定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        //タイトルバーを非表示にする（必ずsetContentViewの前にすること）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(R.style.AppTheme);
        // レイアウトをセットする
        setContentView(R.layout.activity_main);
        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService( Context.BLUETOOTH_SERVICE );
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if( null == mBluetoothAdapter )
        {    // Android端末がBluetoothをサポートしていない
            Toast.makeText( this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT ).show();
            finish();    // アプリ終了宣言
            return;
        }
        String fileName = this.getFilesDir().getAbsolutePath() + "/" + getString(R.string.permitelicense);
        File file = new File(fileName);
        if(!file.exists()) {
            Intent intent = new Intent(MainActivity.this, LicenseActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
        try{
            Thread.sleep(2000); //3000ミリ秒Sleepする
        }catch(InterruptedException e) {}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //==== キーコード判定 ====//
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //-==- Backキー -==-//
            // 以降の処理をキャンセルする。
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
