package com.mobireta.mobiretalabelprint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.provider.Settings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class WaribikiActivity extends AppCompatActivity {

    private MsControl mscontrol;
    // 定数
    private static final int REQUEST_ENABLEBLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード
    private static final int REQUEST_CONNECTDEVICE   = 2; // デバイス接続要求時の識別コード
    private static final int READBUFFERSIZE          = 1024;    // 受信バッファーのサイズ

    // メンバー変数
    private BluetoothAdapter mBluetoothAdapter;    // BluetoothAdapter : Bluetooth処理で必要
    private String mDeviceAddress = "";    // デバイスアドレス
    private BluetoothService mBluetoothService;    // BluetoothService : Bluetoothデバイスとの通信処理を担う
    private byte[] mReadBuffer  = new byte[READBUFFERSIZE];
    private int    mReadBufferCounter = 0;
    private String Tenpocd;
    private String Readerkbn;
    private String Readeraddress;
    private String Printerkbn;
    private String Nebikiformat;
    private String Keyboardkbn;
    private Integer WaribikigoPrice;
    private String Hangakuflag;
    private Integer Maxmaisu;

    Button btWaribikiPrint;
    Button btWaribikiHangaku;
    Button btWaribikiClear;
    //Button btWaribikiMenu;
    Button btWaribiki1;
    Button btWaribiki2;
    Button btWaribiki3;
    Button btWaribiki5;

    Button btWaribiki10;
    Button btWaribiki20;
    Button btWaribiki30;
    Button btWaribiki50;

    TextView tvWaribikiStatus;
    EditText etWaribikiShohinCD;
    EditText etWaribiki;
    EditText etWaribikiSell;
    EditText etWaribikiMaisu;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private int princunt = 0;
    private JancodeCheck janchk;
    private String Code128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waribiki);
        setTitle("モバリテ 割引処理" );

        mscontrol = MsControlHelper.finddata(this);
        //テキストファリル割り当て
        Tenpocd = mscontrol.Tenpocd.toString();
        Readerkbn = mscontrol.Readerkbn.toString();
        Readeraddress = mscontrol.Readeraddress.toString();
        Printerkbn = mscontrol.Printerkbn.toString();
        Keyboardkbn = mscontrol.Keyboardkbn.toString();
        Nebikiformat = mscontrol.Nebikiformat.toString();
        Maxmaisu = Integer.parseInt(mscontrol.Maxmaisu.toString());

        mDeviceAddress = mscontrol.Printeraddress.toString();
        btWaribikiPrint = (Button) findViewById(R.id.btWaribikiPrint);
        btWaribikiHangaku = (Button) findViewById(R.id.btWaribikiHangaku);
        btWaribikiClear = (Button) findViewById(R.id.btWaribikiClear);
        //btWaribikiMenu = (Button) findViewById(R.id.btWaribikiMenu);
        btWaribiki1 = (Button) findViewById(R.id.btWaribiki1);
        btWaribiki2 = (Button) findViewById(R.id.btWaribiki2);
        btWaribiki3 = (Button) findViewById(R.id.btWaribiki3);
        btWaribiki5 = (Button) findViewById(R.id.btWaribiki5);
        btWaribiki10 = (Button) findViewById(R.id.btWaribiki10);
        btWaribiki20 = (Button) findViewById(R.id.btWaribiki20);
        btWaribiki30 = (Button) findViewById(R.id.btWaribiki30);
        btWaribiki50 = (Button) findViewById(R.id.btWaribiki50);
        etWaribikiShohinCD = (EditText) findViewById(R.id.etWaribikiShohinCD);
        etWaribiki = (EditText) findViewById(R.id.etWaribiki);
        etWaribikiSell = (EditText) findViewById(R.id.etWaribikiSell);
        etWaribikiMaisu = (EditText) findViewById(R.id.etWaribikiMaisu);
        tvWaribikiStatus = (TextView) findViewById(R.id.tvWaribikiStatus);
        btWaribikiHangaku.setEnabled(false);
        Hangakuflag = "0";
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (null == mBluetoothAdapter) {    // Android端末がBluetoothをサポートしていない
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show();
            finish();    // アプリ終了宣言
        }

        //キーボード付ハンディー対応
        etWaribikiShohinCD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etWaribikiShohinCD.setTextIsSelectable(true);
                    }
                }else{
                    if (!etWaribikiShohinCD.getText().toString().equals("")){
                        etWaribikiSell.requestFocus();
                    }
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        etWaribiki.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (etWaribiki.getText().toString().equals("50")){
                    Hangakuflag = "1";
                    btWaribikiHangaku.setEnabled(true);
                }
                else{
                    Hangakuflag = "0";
                    btWaribikiHangaku.setEnabled(false);
                }
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etWaribiki.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        etWaribikiSell.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etWaribikiSell.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        etWaribikiMaisu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etWaribikiMaisu.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        //ボタン割り当て
        btWaribiki1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWaribikiMaisu.setText("1");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btWaribiki2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWaribikiMaisu.setText("2");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btWaribiki3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWaribikiMaisu.setText("3");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btWaribiki5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWaribikiMaisu.setText("5");
                btWaribikiHangaku.setEnabled(false);
                CheckData();
                etWaribikiMaisu.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btWaribiki10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWaribiki.setText("10");
                btWaribikiHangaku.setEnabled(false);
                CheckData();
                etWaribikiMaisu.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btWaribiki20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWaribiki.setText("20");
                btWaribikiHangaku.setEnabled(false);
                CheckData();
                etWaribikiMaisu.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btWaribiki30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWaribiki.setText("30");
                btWaribikiHangaku.setEnabled(false);
                CheckData();
                etWaribikiMaisu.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btWaribiki50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWaribiki.setText("50");
                CheckData();
                Hangakuflag = "1";
                btWaribikiHangaku.setEnabled(true);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btWaribikiClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hangakuflag = "0";
                Code128 = "";
                btWaribikiPrint.setEnabled(true);
                btWaribikiHangaku.setEnabled(false);
                etWaribikiShohinCD.setText("");
                etWaribiki.setText("");
                etWaribikiSell.setText("");
                etWaribikiMaisu.setText("");
                etWaribikiShohinCD.requestFocus();
            }
        });

        btWaribikiPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hangakuflag = "0";
                if (etWaribikiShohinCD.getText().toString().equals("")){
                    etWaribikiShohinCD.requestFocus();
                    return;
                }
                if (etWaribiki.getText().toString().equals("")
                        || Integer.parseInt(etWaribiki.getText().toString()) <= 0){
                    etWaribiki.requestFocus();
                    return;
                }
                if (etWaribikiMaisu.getText().toString().equals("")
                        || Integer.parseInt(etWaribikiMaisu.getText().toString()) <= 0){
                    etWaribikiMaisu.requestFocus();
                    return;
                }
                if (Integer.parseInt(etWaribiki.getText().toString()) != 50){
                    if (btWaribikiHangaku.isEnabled()){
                        etWaribiki.requestFocus();
                        return;
                    }
                }
                if (Integer.parseInt(etWaribikiMaisu.getText().toString()) > Maxmaisu){
                    etWaribikiMaisu.requestFocus();
                    Toast toast = Toast.makeText(WaribikiActivity.this, "最大印刷枚数を超えています。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                String shohincd = etWaribikiShohinCD.getText().toString().replace(".","");
                etWaribikiShohinCD.setText(shohincd);
                janchk = new JancodeCheck();
                if (shohincd.length() == 13){
                    if (!janchk.checkupca(shohincd)){
                        if (!janchk.checkjan13(shohincd)){
                            Toast toast = Toast.makeText(WaribikiActivity.this, "JAN13チェックデジットが異ります。", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                    }
                }else if (shohincd.length() == 12) {
                    shohincd = "0" + etWaribikiShohinCD.getText();
                    if (!janchk.checkupca(shohincd)){
                        Toast toast = Toast.makeText(WaribikiActivity.this, "UPC-Aチェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }else if (shohincd.length() == 8) {
                    if (!janchk.checkjan8(shohincd)) {
                        Toast toast = Toast.makeText(WaribikiActivity.this, "JAN8チェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }else{
                    Toast toast = Toast.makeText(WaribikiActivity.this, "JANコードではありません。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (!etWaribikiSell.getText().toString().equals("")){
                    if (Integer.parseInt(etWaribikiSell.getText().toString()) < Integer.parseInt(etWaribiki.getText().toString())){
                        Toast toast = Toast.makeText(WaribikiActivity.this, "定価より低い値引はできません。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }
                if (Nebikiformat.equals("1")) {
                    if (!etWaribikiSell.getText().toString().equals("")){
                        double waribiki = Float.parseFloat(etWaribikiSell.getText().toString()) * (Float.parseFloat(etWaribiki.getText().toString()) / 100);
                        WaribikigoPrice = Integer.parseInt(etWaribikiSell.getText().toString()) - (int) Math.floor(waribiki);
                    }
                    else{
                        Toast toast = Toast.makeText(WaribikiActivity.this, "このフォームでは、売価が必須です。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        etWaribikiSell.requestFocus();
                        return;
                    }
                }
                btWaribikiPrint.setEnabled(false);
                btWaribikiHangaku.setEnabled(false);
                Code128 = janchk.makeEAN128(etWaribikiShohinCD.getText().toString().substring(0, etWaribikiShohinCD.length()), "2", etWaribiki.getText().toString());
                connect();
            }
        });

        btWaribikiHangaku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hangakuflag = "1";
                if (etWaribikiShohinCD.getText().toString().equals("")){
                    etWaribikiShohinCD.requestFocus();
                    return;
                }
                if (Integer.parseInt(etWaribiki.getText().toString()) != 50){
                    etWaribiki.requestFocus();
                    return;
                }
                if (etWaribikiMaisu.getText().toString().equals("")
                        || Integer.parseInt(etWaribikiMaisu.getText().toString()) <= 0){
                    etWaribikiMaisu.requestFocus();
                    return;
                }
                if (Nebikiformat.equals("1")) {
                    if (etWaribikiSell.getText().toString().equals("")){
                        Toast toast = Toast.makeText(WaribikiActivity.this, "価格が必須ラベルが選択されています。", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                }
                String shohincd = etWaribikiShohinCD.getText().toString().replace(".","");
                etWaribikiShohinCD.setText(shohincd);
                janchk = new JancodeCheck();
                if (shohincd.length() == 13){
                    if (!janchk.checkupca(shohincd)){
                        if (!janchk.checkjan13(shohincd)){
                            Toast toast = Toast.makeText(WaribikiActivity.this, "JAN13チェックデジットが異ります。", Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                    }
                }else if (shohincd.length() == 12) {
                    shohincd = "0" + etWaribikiShohinCD.getText();
                    if (!janchk.checkupca(shohincd)){
                        Toast toast = Toast.makeText(WaribikiActivity.this, "UPC-Aチェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }else if (shohincd.length() == 8) {
                    if (!janchk.checkjan8(shohincd)) {
                        Toast toast = Toast.makeText(WaribikiActivity.this, "JAN8チェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }else{
                    Toast toast = Toast.makeText(WaribikiActivity.this, "JANコードではありません。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (Nebikiformat.equals("1")) {
                    if (!etWaribikiSell.getText().toString().equals("")){
                        double waribiki = Float.parseFloat(etWaribikiSell.getText().toString()) * (Float.parseFloat(etWaribiki.getText().toString()) / 100);
                        WaribikigoPrice = Integer.parseInt(etWaribikiSell.getText().toString()) - (int) Math.floor(waribiki);
                    }
                }
                btWaribikiPrint.setEnabled(false);
                btWaribikiHangaku.setEnabled(false);
                Code128 = janchk.makeEAN128(etWaribikiShohinCD.getText().toString().substring(0, etWaribikiShohinCD.length()), "2", etWaribiki.getText().toString());
                connect();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //==== キーコード判定 ====//
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean CheckData(){
        if (etWaribikiShohinCD.toString().equals(""))
        {
            etWaribikiShohinCD.requestFocus();
            return false;
        }
        if (etWaribikiMaisu.toString().equals(""))
        {
            etWaribikiMaisu.requestFocus();
            return false;
        }
        btWaribikiPrint.setEnabled(true);
        return true;
    }

    // オプションメニュー作成時の処理
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.activity_scanbt, menu );
        return true;
    }

    // オプションメニューのアイテム選択時の処理
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        Intent intent;
        switch( item.getItemId() )
        {
            case R.id.menuitem_scan:
                if (Readerkbn.equals("0")){
                    intent = new Intent(this, BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                    startActivityForResult(intent, RC_BARCODE_CAPTURE);
                    return true;
                }
                else{
                    return false;
                }
            case R.id.menuitem_bt:
                intent = new Intent(Intent.ACTION_MAIN, null);
                intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //statusMessage.setText(R.string.barcode_success);
                    //barcodeValue.setText(barcode.displayValue);
                    etWaribikiShohinCD.setText(barcode.displayValue);
                    etWaribikiSell.requestFocus();
                    //CheckData();
                    //Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    //statusMessage.setText(R.string.barcode_failure);
                    etWaribikiShohinCD.requestFocus();
                    //Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                etWaribikiShohinCD.requestFocus();
                //statusMessage.setText(String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            //etWaribikiShohinCD.requestFocus();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 初回表示時、および、ポーズからの復帰時
    @Override
    protected void onResume()
    {
        super.onResume();
        // Android端末のBluetooth機能の有効化要求
        requestBluetoothFeature();
        // デバイスアドレスが空でなければ、接続ボタンを有効にする。
        if( !mDeviceAddress.equals( "" ) )
        {
            btWaribikiPrint.setEnabled( true );
        }
        if (etWaribikiShohinCD.getText().toString().equals("")){
            etWaribikiShohinCD.requestFocus();
            return;
        }
        if (etWaribikiSell.getText().toString().equals("")){
            etWaribikiSell.requestFocus();
            return;
        }
        if (etWaribiki.getText().toString().equals("")){
            etWaribiki.requestFocus();
            return;
        }
        if (etWaribikiMaisu.getText().toString().equals("")){
            etWaribikiMaisu.requestFocus();
            return;
        }
        //btWaribikiPrint.callOnClick();
    }

    // 別のアクティビティ（か別のアプリ）に移行したことで、バックグラウンドに追いやられた時
    @Override
    protected void onPause()
    {
        super.onPause();
        // 切断
        disconnect();
    }

    // アクティビティの終了直前
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if( null != mBluetoothService )
        {
            mBluetoothService.disconnect();
            mBluetoothService = null;
        }
    }

    // Android端末のBluetooth機能の有効化要求
    private void requestBluetoothFeature()
    {
        if( mBluetoothAdapter.isEnabled() )
        {
            return;
        }
        // デバイスのBluetooth機能が有効になっていないときは、有効化要求（ダイアログ表示）
        Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
        startActivityForResult( enableBtIntent, REQUEST_ENABLEBLUETOOTH );
        btWaribikiPrint.setEnabled( true );
    }

    // 接続
    private void connect()
    {
        if( mDeviceAddress.equals( "" ) )
        {    // DeviceAddressが空の場合は処理しない
            return;
        }
        if( null != mBluetoothService )
        {    // mBluetoothServiceがnullでないなら接続済みか、接続中。
            return;
        }
        // 接続
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice( mDeviceAddress );
        mBluetoothService = new BluetoothService( this, mHandler, device );
        mBluetoothService.connect();
    }

    // 切断
    private void disconnect()
    {
        if( null == mBluetoothService )
        {    // mBluetoothServiceがnullなら切断済みか、切断中。
            return;
        }
        // 切断
        mBluetoothService.disconnect();
        mBluetoothService = null;
    }

    // 文字列送信
    private void write()
    {
        if( null == mBluetoothService )
        {    // mBluetoothServiceがnullなら切断済みか、切断中。
            return;
        }
        String datastring = "";
        Boolean result = false;
        if (Printerkbn.equals("0"))
        {
            if (Nebikiformat.equals("0")){
                datastring = readFile("TEW01.TXT", "TEC");
            }else{
                datastring = readFile("TEW02.TXT", "TEC");
            }
            mBluetoothService.write(datastring, "TEC", Integer.parseInt(etWaribikiMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("1"))
        {
            if (Nebikiformat.equals("0")){
                datastring = readFile("TLW01.TXT", "TEC");
            }else{
                datastring = readFile("TLW02.TXT", "TEC");
            }
            mBluetoothService.write(datastring, "TEC", Integer.parseInt(etWaribikiMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("2"))
        {
            if (Nebikiformat.equals("0")){
                datastring = readFile("S4W01.TXT", "SATO");
            }else{
                datastring = readFile("S4W02.TXT", "SATO");
            }
            mBluetoothService.write(datastring, "SATO", Integer.parseInt(etWaribikiMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("3"))
        {
            if (Nebikiformat.equals("0")){
                datastring = readFile("S3W01.TXT", "SATO");
            }else{
                datastring = readFile("S3W02.TXT", "SATO");
            }
            mBluetoothService.write(datastring, "SATO", Integer.parseInt(etWaribikiMaisu.getText().toString()));
        }
    }

    // ファイルを読み出し
    public String readFile(String file, String printer) {
        String lineBuffer = null;
        String lineBuffer2 = "";
        try (FileInputStream fileInputStream = openFileInput(file);
             BufferedReader reader= new BufferedReader(new InputStreamReader(fileInputStream,"UTF-8"));
        ) {
            while( (lineBuffer = reader.readLine()) != null ) {
                if (printer.equals("TEC")){
                    if (lineBuffer.substring(0,1).equals("#")){
                        if (lineBuffer.substring(1,2).equals("B")){
                            lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + Code128 + "^";
                        }else if (lineBuffer.substring(1,2).equals("S")) {
                            String price = "";
                            if (Nebikiformat.equals("0")) {
                                if (!etWaribikiSell.getText().toString().equals("")){
                                    if (etWaribikiSell.getText().length() == 1) {
                                        price = "   " + etWaribikiSell.getText().toString();
                                    } else if (etWaribikiSell.getText().length() == 2) {
                                        price = "  " + etWaribikiSell.getText().toString();
                                    } else if (etWaribikiSell.getText().length() == 3) {
                                        price = " " + etWaribikiSell.getText().toString();
                                    } else {
                                        price = etWaribikiSell.getText().toString();
                                    }
                                    lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "円を" + "^";
                                }
                                else{
                                    lineBuffer = "";
                                }
                            } else {
                                if (!etWaribikiSell.getText().toString().equals("")){
                                    if (etWaribikiSell.getText().length() == 1) {
                                        price = "   " + etWaribikiSell.getText().toString();
                                    } else if (etWaribikiSell.getText().length() == 2) {
                                        price = "  " + etWaribikiSell.getText().toString();
                                    } else if (etWaribikiSell.getText().length() == 3) {
                                        price = " " + etWaribikiSell.getText().toString();
                                    } else {
                                        price = etWaribikiSell.getText().toString();
                                    }
                                    lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
                                }
                                else{
                                    lineBuffer = "";
                                }
                            }
                        }else if (lineBuffer.substring(1,2).equals("T")) {
                            String nebiki ="";
                            if (Hangakuflag.equals("0")){
                                if (etWaribiki.getText().length() == 1){
                                    nebiki = " " + etWaribiki.getText().toString();
                                } else {
                                    nebiki = etWaribiki.getText().toString();
                                }
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + nebiki + "^";
                            }else{
                                if (Nebikiformat.equals("0")){
                                    lineBuffer = "PC02;0330,0090,20,20,V,00,B,J0101,P1=半額^";
                                }
                                else{
                                    lineBuffer = "PC02;0065,0130,20,20,V,00,B,J0101,P1=半額^";
                                }
                            }
                        }else if (lineBuffer.substring(1,2).equals("P")) {
                            if (Nebikiformat.equals("1")) {
                                String price;
                                Integer a = WaribikigoPrice.toString().length();
                                if (WaribikigoPrice.toString().length() == 1){
                                    price = "   " + WaribikigoPrice.toString();
                                } else if(WaribikigoPrice.toString().length() == 2){
                                    price = "  " + WaribikigoPrice.toString();
                                } else if(WaribikigoPrice.toString().length() == 3){
                                    price = " " + WaribikigoPrice.toString();
                                } else {
                                    price = WaribikigoPrice.toString();
                                }
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
                            }
                        }
                    }else{
                        if (etWaribikiSell.getText().toString().equals("")){
                            if (lineBuffer.indexOf("の品を") == -1){
                                lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
                            } else {
                                lineBuffer = "";
                            }
                        }
                        else{
                            lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
                        }
                        if (Hangakuflag.equals("1")){
                            if (lineBuffer.indexOf("％引") == -1){
                                lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
                            } else {
                                lineBuffer = "";
                            }
                        }
                        else{
                            lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
                        }
                    }
                }
                else{
                    if (lineBuffer.substring(0,1).equals("#")){
                        if (lineBuffer.substring(1,2).equals("B")){
                            lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + Code128 + "^";
                        }else if (lineBuffer.substring(1,2).equals("S")) {
                            if (!etWaribikiSell.getText().toString().equals("")){
                                String price;
                                if (etWaribikiSell.getText().length() == 1){
                                    price = "   " + etWaribikiSell.getText().toString();
                                } else if(etWaribikiSell.getText().length() == 2){
                                    price = "  " + etWaribikiSell.getText().toString();
                                } else if(etWaribikiSell.getText().length() == 3){
                                    price = " " + etWaribikiSell.getText().toString();
                                } else {
                                    price = etWaribikiSell.getText().toString();
                                }
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
                                if (Nebikiformat.equals("0")){
                                    lineBuffer = lineBuffer + "V40^H120^P0^L0102^K8D円を^";
                                }
                            }
                            else{
                                lineBuffer = "";
                            }
                        }else if (lineBuffer.substring(1,2).equals("T")) {
                            String nebiki ="";
                            if (Hangakuflag.equals("0")){
                                if (etWaribiki.getText().length() == 1){
                                    nebiki = " " + etWaribiki.getText().toString();
                                } else {
                                    nebiki = etWaribiki.getText().toString();
                                }
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + nebiki + "^";
                            }else{
                                if (Nebikiformat.equals("1")) {
                                    if (Printerkbn.equals("2")){
                                        //PW208
                                        lineBuffer = "V50^H60^P0^L0202^K9D半額^";
                                    }else{
                                        //PT208
                                        lineBuffer = "V50^H60^P0^L0202^K9D半額^";
                                    }
                                }
                                else{
                                    if (Printerkbn.equals("2")){
                                        //PW208
                                        lineBuffer = "V30^H230^P0^L0202^K9D半額^";
                                    }else{
                                        //PT208
                                        lineBuffer = "V30^H240^P0^L0202^K9D半額^";
                                    }
                                }
                            }
                        }else if (lineBuffer.substring(1,2).equals("P")) {
                            String price;
                            if (WaribikigoPrice.toString().length() == 1){
                                price = "   " + WaribikigoPrice.toString();
                            } else if(WaribikigoPrice.toString().length() == 2){
                                price = "  " + WaribikigoPrice.toString();
                            } else if(WaribikigoPrice.toString().length() == 3){
                                price = " " + WaribikigoPrice.toString();
                            } else {
                                price = WaribikigoPrice.toString();
                            }
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
                        }
                    }else{
                        if (Hangakuflag.equals("1")) {
                            if (lineBuffer.indexOf("％引") == -1){
                                lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
                            }
                            else{
                                lineBuffer = "^";
                            }
                        }
                        else{
                            lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
                        }
                    }
                }
                lineBuffer2 = lineBuffer2 + lineBuffer;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineBuffer2;
    }

    // Bluetoothサービスから情報を取得するハンドラ
    private final Handler mHandler = new Handler()
    {
        // ハンドルメッセージ
        // UIスレッドの処理なので、UI処理について、runOnUiThread対応は、不要。
        @Override
        public void handleMessage( Message msg )
        {
            switch( msg.what )
            {
                case BluetoothService.MESSAGE_STATECHANGE:
                    switch( msg.arg1 )
                    {
                        case BluetoothService.STATE_NONE:            // 未接続
                            break;
                        case BluetoothService.STATE_CONNECT_START:        // 接続開始
                            break;
                        case BluetoothService.STATE_CONNECT_FAILED:            // 接続失敗
                            Toast.makeText( WaribikiActivity.this, "プリンタと接続できません.", Toast.LENGTH_SHORT ).show();
                            break;
                        case BluetoothService.STATE_CONNECTED:    // 接続完了
                            // GUIアイテムの有効無効の設定
                            // 切断ボタン、文字列送信ボタンを有効にする
                            //btWaribikiPrint.setEnabled( true );
                            write();
                            break;
                        case BluetoothService.STATE_CONNECTION_LOST:            // 接続ロスト
                            //Toast.makeText( WaribikiActivity.this, "Lost connection to the device.", Toast.LENGTH_SHORT ).show();
                            break;
                        case BluetoothService.STATE_DISCONNECT_START:
                            // GUIアイテムの有効無効の設定
                            // 切断ボタン、文字列送信ボタンを無効にする
                            break;
                        case BluetoothService.STATE_DISCONNECTED:            // 切断完了
                            // GUIアイテムの有効無効の設定
                            // 接続ボタンを有効にする
                            //mButton_Connect.setEnabled( true );
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_READ:
                    byte[] abyteRead = (byte[])msg.obj;
                    int iCountBuf = msg.arg1;
                    for( int i = 0; i < iCountBuf; i++ )
                    {
                        byte c = abyteRead[i];
                        if( '\r' == c )
                        {    // 終端
                            mReadBuffer[mReadBufferCounter] = '\0';
                            // GUIアイテムへの反映
                            ( (TextView)findViewById( R.id.tvWaribikiStatus ) ).setText( new String( mReadBuffer, 0, mReadBufferCounter ) );
                            mReadBufferCounter = 0;
                        }
                        else if( '\n' == c )
                        {
                            ;    // 何もしない
                        }
                        else
                        {    // 途中
                            if( ( READBUFFERSIZE - 1 ) > mReadBufferCounter )
                            {    // mReadBuffer[READBUFFERSIZE - 2] までOK。
                                // mReadBuffer[READBUFFERSIZE - 1] は、バッファー境界内だが、「\0」を入れられなくなるのでNG。
                                mReadBuffer[mReadBufferCounter] = c;
                                mReadBufferCounter++;
                            }
                            else
                            {    // バッファーあふれ。初期化
                                mReadBufferCounter = 0;
                            }
                        }
                    }
                    btWaribikiClear.callOnClick();
                    disconnect();
                    break;
                case BluetoothService.MESSAGE_WRITTEN:
                    // GUIアイテムの有効無効の設定
                    // 文字列送信ボタンを有効にする（連打対策で無効になっているボタンを復帰させる）
                    break;
            }
        }
    };

}
