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

public class NebikiActivity extends AppCompatActivity {


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
    private Integer NebikigoPrice;
    private Integer Maxmaisu;


    Button btNebikiPrint;
    Button btNebikiClear;
    //Button btNebikiMenu;
    Button btNebiki1;
    Button btNebiki2;
    Button btNebiki3;
    Button btNebiki5;

    Button btNebiki50;
    Button btNebiki100;
    Button btNebiki150;
    Button btNebiki200;

    TextView tvNebikiStatus;
    EditText etNebikiShohinCD;
    EditText etNebiki;
    EditText etNebikiSell;
    EditText etNebikiMaisu;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private int princunt = 0;
    private JancodeCheck janchk;
    private String Code128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nebiki);
        setTitle("モバリテ 値引ラベル" );

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
        btNebikiPrint = (Button) findViewById(R.id.btNebikiPrint);
        btNebikiClear = (Button) findViewById(R.id.btNebikiClear);
        //btNebikiMenu = (Button) findViewById(R.id.btNebikiMenu);
        btNebiki1 = (Button) findViewById(R.id.btNebiki1);
        btNebiki2 = (Button) findViewById(R.id.btNebiki2);
        btNebiki3 = (Button) findViewById(R.id.btNebiki3);
        btNebiki5 = (Button) findViewById(R.id.btNebiki5);
        btNebiki50 = (Button) findViewById(R.id.btNebiki50);
        btNebiki100 = (Button) findViewById(R.id.btNebiki100);
        btNebiki150 = (Button) findViewById(R.id.btNebiki150);
        btNebiki200 = (Button) findViewById(R.id.btNebiki200);

        etNebikiShohinCD = (EditText) findViewById(R.id.etNebikiShohinCD);
        etNebiki = (EditText) findViewById(R.id.etNebiki);
        etNebikiSell = (EditText) findViewById(R.id.etNebikiSell);
        etNebikiMaisu = (EditText) findViewById(R.id.etNebikiMaisu);
        tvNebikiStatus = (TextView) findViewById(R.id.tvNebikiStatus);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (null == mBluetoothAdapter) {    // Android端末がBluetoothをサポートしていない
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show();
            finish();    // アプリ終了宣言
        }

        //キーボード付ハンディー対応
        etNebikiShohinCD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNebikiShohinCD.setTextIsSelectable(true);
                    }
                }else{
                    //etNebikiSell.requestFocus();
                    //etNebikiSell.setTextIsSelectable(false);
                }
            }
        });

        etNebikiSell.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNebikiSell.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        etNebiki.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNebiki.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        etNebikiMaisu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNebikiMaisu.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        //ボタン割り当て
        btNebiki1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNebikiMaisu.setText("1");
                CheckData();
                btNebikiPrint.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNebiki2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNebikiMaisu.setText("2");
                CheckData();
                btNebikiPrint.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNebiki3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNebikiMaisu.setText("3");
                CheckData();
                btNebikiPrint.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNebiki5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNebikiMaisu.setText("5");
                CheckData();
                btNebikiPrint.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNebiki50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNebiki.setText("50");
                CheckData();
                etNebikiMaisu.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNebiki100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNebiki.setText("100");
                CheckData();
                etNebikiMaisu.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        btNebiki150.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNebiki.setText("150");
                CheckData();
                etNebikiMaisu.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNebiki200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNebiki.setText("200");
                CheckData();
                etNebikiMaisu.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNebikiClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Code128 = "";
                btNebikiPrint.setEnabled(true);
                etNebikiShohinCD.setText("");
                etNebiki.setText("");
                etNebikiSell.setText("");
                etNebikiMaisu.setText("");
                etNebikiShohinCD.requestFocus();
            }
        });

        btNebikiPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNebikiShohinCD.getText().toString().equals("")){
                    etNebikiShohinCD.requestFocus();
                    return;
                }
                if (etNebiki.getText().toString().equals("")
                        || Integer.parseInt(etNebiki.getText().toString()) <= 0){
                    etNebiki.requestFocus();
                    return;
                }
                if (etNebikiMaisu.getText().toString().equals("")
                        || Integer.parseInt(etNebikiMaisu.getText().toString()) <= 0){
                    etNebikiMaisu.requestFocus();
                    return;
                }
                if (Nebikiformat.equals("1")) {
                    if (etNebikiSell.getText().toString().equals("")){
                        Toast toast = Toast.makeText(NebikiActivity.this, "このフォームでは、売価が必須です。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        etNebikiSell.requestFocus();
                        return;
                    }
                }
                if (Integer.parseInt(etNebikiMaisu.getText().toString()) > Maxmaisu){
                    etNebikiMaisu.requestFocus();
                    Toast toast = Toast.makeText(NebikiActivity.this, "最大印刷枚数を超えています。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (!etNebikiSell.getText().toString().equals("")){
                    if (Integer.parseInt(etNebikiSell.getText().toString()) <= Integer.parseInt(etNebiki.getText().toString())){
                        Toast toast = Toast.makeText(NebikiActivity.this, "定価より高い値引はできせません。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        etNebiki.requestFocus();
                        return;
                    }
                }
                String shohincd = etNebikiShohinCD.getText().toString().replace(".","");
                etNebikiShohinCD.setText(shohincd);
                janchk = new JancodeCheck();
                if (shohincd.length() == 13){
                    if (!janchk.checkupca(shohincd)){
                        if (!janchk.checkjan13(shohincd)){
                            Toast toast = Toast.makeText(NebikiActivity.this, "JAN13チェックデジットが異ります。", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                    }
                }else if (shohincd.length() == 12) {
                    shohincd = "0" + etNebikiShohinCD.getText();
                    if (!janchk.checkupca(shohincd)){
                        Toast toast = Toast.makeText(NebikiActivity.this, "UPC-Aチェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }else if (shohincd.length() == 8) {
                    if (!janchk.checkjan8(shohincd)) {
                        Toast toast = Toast.makeText(NebikiActivity.this, "JAN8チェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }else{
                    Toast toast = Toast.makeText(NebikiActivity.this, "JANコードではありません。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (!etNebikiSell.getText().toString().equals("")){
                    if (Integer.parseInt(etNebikiSell.getText().toString()) <= Integer.parseInt(etNebiki.getText().toString())){
                        Toast toast = Toast.makeText(NebikiActivity.this, "定価より高い値引はできません。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }
                if (!etNebikiSell.getText().toString().equals("")){
                    NebikigoPrice = Integer.parseInt(etNebikiSell.getText().toString()) - Integer.parseInt(etNebiki.getText().toString());
                }
                btNebikiPrint.setEnabled(false);
                Code128 = janchk.makeEAN128(etNebikiShohinCD.getText().toString().substring(0, etNebikiShohinCD.length()), "1", etNebiki.getText().toString());
                connect();
            }
        });
    }

    public boolean CheckData(){
        if (etNebikiShohinCD.toString().equals(""))
        {
            etNebikiShohinCD.requestFocus();
            return false;
        }
        if (etNebikiMaisu.toString().equals(""))
        {
            etNebikiMaisu.requestFocus();
            return false;
        }
        btNebikiPrint.setEnabled(true);
        return true;
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
                    etNebikiShohinCD.setText(barcode.displayValue);
                    etNebikiSell.requestFocus();
                    //Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    //statusMessage.setText(R.string.barcode_failure);
                    etNebikiShohinCD.requestFocus();
                    //Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                etNebikiShohinCD.requestFocus();
                //statusMessage.setText(String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            //etNebikiShohinCD.requestFocus();
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
            btNebikiPrint.setEnabled( true );
        }
        if (etNebikiShohinCD.getText().toString().equals("")){
            etNebikiShohinCD.requestFocus();
            return;
        }
        if (etNebikiSell.getText().toString().equals("")){
            etNebikiSell.requestFocus();
            return;
        }
        if (etNebiki.getText().toString().equals("")){
            etNebiki.requestFocus();
            return;
        }
        if (etNebikiMaisu.getText().toString().equals("")){
            etNebikiMaisu.requestFocus();
            return;
        }
        //btNebikiPrint.callOnClick();
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
        btNebikiPrint.setEnabled( true );
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
                datastring = readFile("TEB01.TXT", "TEC");
            }else{
                datastring = readFile("TEB02.TXT", "TEC");
            }
            mBluetoothService.write(datastring, "TEC", Integer.parseInt(etNebikiMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("1"))
        {
            if (Nebikiformat.equals("0")){
                datastring = readFile("TLB01.TXT", "TEC");
            }else{
                datastring = readFile("TLB02.TXT", "TEC");
            }
            mBluetoothService.write(datastring, "TEC", Integer.parseInt(etNebikiMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("2"))
        {
            if (Nebikiformat.equals("0")){
                datastring = readFile("S4B01.TXT", "SATO");
            }else{
                datastring = readFile("S4B02.TXT", "SATO");
            }
            mBluetoothService.write(datastring, "SATO", Integer.parseInt(etNebikiMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("3"))
        {
            if (Nebikiformat.equals("0")){
                datastring = readFile("S3B01.TXT", "SATO");
            }else{
                datastring = readFile("S3B02.TXT", "SATO");
            }
            mBluetoothService.write(datastring, "SATO", Integer.parseInt(etNebikiMaisu.getText().toString()));
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
                                if (!etNebikiSell.getText().toString().equals("")){
                                    if (etNebikiSell.getText().length() == 1){
                                        price = "   " + etNebikiSell.getText().toString();
                                    } else if(etNebikiSell.getText().length() == 2){
                                        price = "  " + etNebikiSell.getText().toString();
                                    } else if(etNebikiSell.getText().length() == 3){
                                        price = " " + etNebikiSell.getText().toString();
                                    } else {
                                        price = etNebikiSell.getText().toString();
                                    }
                                    lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "円を" + "^";
                                }
                                else{
                                    lineBuffer = "";
                                }
                            }else{
                                if (!etNebikiSell.getText().toString().equals("")){
                                    if (etNebikiSell.getText().length() == 1){
                                        price = "   " + etNebikiSell.getText().toString();
                                    } else if(etNebikiSell.getText().length() == 2){
                                        price = "  " + etNebikiSell.getText().toString();
                                    } else if(etNebikiSell.getText().length() == 3){
                                        price = " " + etNebikiSell.getText().toString();
                                    } else {
                                        price = etNebikiSell.getText().toString();
                                    }
                                    lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
                                }
                                else{
                                    lineBuffer = "";
                                }
                            }
                        }else if (lineBuffer.substring(1,2).equals("T")) {
                            String nebiki ="";
                            if (etNebiki.getText().length() == 1){
                                nebiki = "   " + etNebiki.getText().toString();
                            } else if(etNebiki.getText().length() == 2){
                                nebiki = "  " + etNebiki.getText().toString();
                            } else if(etNebiki.getText().length() == 3){
                                nebiki = " " + etNebiki.getText().toString();
                            } else {
                                nebiki = etNebiki.getText().toString();
                            }
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + nebiki + "^";
                        }else if (lineBuffer.substring(1,2).equals("P")) {
                            if (Nebikiformat.equals("1")) {
                                String price;
                                if (NebikigoPrice.toString().length() == 1){
                                    price = "   " + NebikigoPrice.toString();
                                } else if(NebikigoPrice.toString().length() == 2){
                                    price = "  " + NebikigoPrice.toString();
                                } else if(NebikigoPrice.toString().length() == 3){
                                    price = " " + NebikigoPrice.toString();
                                } else {
                                    price = NebikigoPrice.toString();
                                }
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
                            }
                        }
                    }else{
                        lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
                    }
                }
                else{
                    if (lineBuffer.substring(0,1).equals("#")){
                        if (lineBuffer.substring(1,2).equals("B")){
                            lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + Code128 + "^";
                        }else if (lineBuffer.substring(1,2).equals("S")) {
                            if (!etNebikiSell.getText().toString().equals("")){
                                String price;
                                if (etNebikiSell.getText().toString().length() == 1){
                                    price = "   " + etNebikiSell.getText().toString();
                                } else if(etNebikiSell.getText().toString().length() == 2){
                                    price = "  " + etNebikiSell.getText().toString();
                                } else if(etNebikiSell.getText().toString().length() == 3){
                                    price = " " + etNebikiSell.getText().toString();
                                } else {
                                    price = etNebikiSell.getText().toString();
                                }
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price  + "^";
                                if (Nebikiformat.equals("0")){
                                    lineBuffer = lineBuffer + "V40^H120^P0^L0102^K8D円を^";
                                }
                           }
                           else{
                                lineBuffer = "";
                            }
                        }else if (lineBuffer.substring(1,2).equals("T")) {
                            String nebiki ="";
                            if (etNebiki.getText().length() == 1){
                                nebiki = "   " + etNebiki.getText().toString();
                            } else if(etNebiki.getText().length() == 2){
                                nebiki = "  " + etNebiki.getText().toString();
                            } else if(etNebiki.getText().length() == 3){
                                nebiki = " " + etNebiki.getText().toString();
                            } else {
                                nebiki = etNebiki.getText().toString();
                            }
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + nebiki + "^";
                        }else if (lineBuffer.substring(1,2).equals("P")) {
                            if (Nebikiformat.equals("1")) {
                                String price;
                                if (NebikigoPrice.toString().length() == 1){
                                    price = "   " + NebikigoPrice.toString();
                                } else if(NebikigoPrice.toString().length() == 2){
                                    price = "  " + NebikigoPrice.toString();
                                } else if(NebikigoPrice.toString().length() == 3){
                                    price = " " + NebikigoPrice.toString();
                                } else {
                                    price = NebikigoPrice.toString();
                                }
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
                            }
                        }
                    }else{
                        lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
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
                            Toast.makeText( NebikiActivity.this, "プリンタと接続できません.", Toast.LENGTH_SHORT ).show();
                            break;
                        case BluetoothService.STATE_CONNECTED:    // 接続完了
                            // GUIアイテムの有効無効の設定
                            // 切断ボタン、文字列送信ボタンを有効にする
                            //btNebikiPrint.setEnabled( true );
                            write();
                            break;
                        case BluetoothService.STATE_CONNECTION_LOST:            // 接続ロスト
                            //Toast.makeText( NebikiActivity.this, "Lost connection to the device.", Toast.LENGTH_SHORT ).show();
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
                            ( (TextView)findViewById( R.id.tvNebikiStatus ) ).setText( new String( mReadBuffer, 0, mReadBufferCounter ) );
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
                    btNebikiClear.callOnClick();
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
