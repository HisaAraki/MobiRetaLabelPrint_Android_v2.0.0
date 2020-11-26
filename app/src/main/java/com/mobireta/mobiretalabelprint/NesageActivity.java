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

public class NesageActivity extends AppCompatActivity {

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
    private String Keyboardkbn;
    private String Nebikiformat;
    private Integer NesagegoPrice;
    private Integer Maxmaisu;

    Button btNesagePrint;
    Button btNesageClear;
    //Button btNesageMenu;
    Button btNesage1;
    Button btNesage2;
    Button btNesage3;
    Button btNesage5;

    TextView tvNesageStatus;
    EditText etNesageShohinCD;
    EditText etNesage;
    EditText etNesageSell;
    EditText etNesageMaisu;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private int princunt = 0;
    private JancodeCheck janchk;
    private String Code128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nesage);
        setTitle("モバリテ 値下処理");

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

        btNesagePrint = (Button) findViewById(R.id.btNesagePrint);
        btNesageClear = (Button) findViewById(R.id.btNesageClear);
        //btNesageMenu = (Button) findViewById(R.id.btNesageMenu);
        btNesage1 = (Button) findViewById(R.id.btNesage1);
        btNesage2 = (Button) findViewById(R.id.btNesage2);
        btNesage3 = (Button) findViewById(R.id.btNesage3);
        btNesage5 = (Button) findViewById(R.id.btNesage5);
        etNesageShohinCD = (EditText) findViewById(R.id.etNesageShohinCD);
        etNesage = (EditText) findViewById(R.id.etNesage);
        etNesageSell = (EditText) findViewById(R.id.etNesageSell);
        etNesageMaisu = (EditText) findViewById(R.id.etNesageMaisu);
        tvNesageStatus = (TextView) findViewById(R.id.tvNesageStatus);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (null == mBluetoothAdapter) {    // Android端末がBluetoothをサポートしていない
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show();
            finish();    // アプリ終了宣言
        }

        //キーボード付ハンディー対応
        etNesageShohinCD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNesageShohinCD.setTextIsSelectable(true);
                    }
                }else{
                }
            }
        });

        etNesage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNesage.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        etNesageSell.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNesageSell.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        etNesageMaisu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNesageMaisu.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        //ボタン割り当て
        btNesage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNesageMaisu.setText("1");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNesage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNesageMaisu.setText("2");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNesage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNesageMaisu.setText("3");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNesage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNesageMaisu.setText("5");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        btNesageClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Code128 = "";
                btNesagePrint.setEnabled(true);
                etNesageShohinCD.setText("");
                etNesage.setText("");
                etNesageSell.setText("");
                etNesageMaisu.setText("");
                etNesageShohinCD.requestFocus();
            }
        });

        btNesagePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNesageShohinCD.getText().toString().equals("")){
                    etNesageShohinCD.requestFocus();
                    return;
                }
                if (etNesage.getText().toString().equals("") || Integer.parseInt(etNesage.getText().toString()) <= 0){
                    etNesage.requestFocus();
                    return;
                }
                if (Integer.parseInt(etNesageMaisu.getText().toString()) > Maxmaisu){
                    etNesageMaisu.requestFocus();
                    Toast toast = Toast.makeText( NesageActivity.this, "最大印刷枚数を超えています。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (!etNesageSell.getText().toString().equals("")){
                    if (Integer.parseInt(etNesageSell.getText().toString()) <= Integer.parseInt(etNesage.getText().toString())){
                        Toast toast = Toast.makeText(NesageActivity.this, "売価より高い値引はできません。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        etNesage.requestFocus();
                        return;
                    }
                }
                if (etNesageMaisu.getText().toString().equals("")
                        && Integer.parseInt(etNesageMaisu.getText().toString()) <= 0){
                    etNesageMaisu.requestFocus();
                    return;
                }
                String shohincd = etNesageShohinCD.getText().toString().replace(".","");
                shohincd = etNesageShohinCD.getText().toString().replace("-","");
                shohincd = etNesageShohinCD.getText().toString().replace(" ","");
                etNesageShohinCD.setText(shohincd);
                janchk = new JancodeCheck();
                if (shohincd.length() == 13){
                    if (!janchk.checkupca(shohincd)){
                        if (!janchk.checkjan13(shohincd)){
                            Toast toast = Toast.makeText(NesageActivity.this, "JAN13チェックデジットが異ります。", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            etNesageShohinCD.requestFocus();
                            return;
                        }
                    }
                }else if (shohincd.length() == 12) {
                    shohincd = "0" + etNesageShohinCD.getText();
                    if (!janchk.checkupca(shohincd)){
                        Toast toast = Toast.makeText(NesageActivity.this, "UPC-Aチェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        etNesageShohinCD.requestFocus();
                        return;
                    }
                }else if (shohincd.length() == 8) {
                    if (!janchk.checkjan8(shohincd)) {
                        Toast toast = Toast.makeText(NesageActivity.this, "JAN8チェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        etNesageShohinCD.requestFocus();
                        return;
                    }
                }else{
                    Toast toast = Toast.makeText(NesageActivity.this, "JANコードではありません。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    etNesageShohinCD.requestFocus();
                    return;
                }
                if (Nebikiformat.equals("1")) {
                    if (etNesageSell.getText().toString().equals("")){
                        Toast toast = Toast.makeText(NesageActivity.this, "このフォームでは、売価が必須です。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        etNesageSell.requestFocus();
                        return;
                    }
                }
                if (!etNesageSell.getText().toString().equals("")){
                    if (Integer.parseInt(etNesageSell.getText().toString()) <= Integer.parseInt(etNesage.getText().toString())){
                        Toast toast = Toast.makeText(NesageActivity.this, "定価より高い値下はできません。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        etNesage.requestFocus();
                        return;
                    }
                    NesagegoPrice = Integer.parseInt(etNesageSell.getText().toString()) - Integer.parseInt(etNesage.getText().toString());
                }
                btNesagePrint.setEnabled(false);
                Code128 = janchk.makeEAN128(etNesageShohinCD.getText().toString().substring(0, etNesageShohinCD.length()), "3", etNesage.getText().toString());
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
        if (etNesageShohinCD.toString().equals(""))
        {
            etNesageShohinCD.requestFocus();
            return false;
        }
        if (etNesage.toString().equals(""))
        {
            etNesage.requestFocus();
            return false;
        }
        if (etNesageMaisu.toString().equals(""))
        {
            etNesageMaisu.requestFocus();
            return false;
        }
        btNesagePrint.setEnabled(true);
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
                    etNesageShohinCD.setText(barcode.displayValue);
                    etNesageSell.requestFocus();
                    //CheckData();
                    //Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    //statusMessage.setText(R.string.barcode_failure);
                    etNesageShohinCD.requestFocus();
                    //Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                etNesageShohinCD.requestFocus();
                //statusMessage.setText(String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            //etNesageShohinCD.requestFocus();
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
            btNesagePrint.setEnabled( true );
        }
        if (etNesageShohinCD.getText().toString().equals("")){
            etNesageShohinCD.requestFocus();
            return;
        }
        if (etNesageSell.getText().toString().equals("")){
            etNesageSell.requestFocus();
            return;
        }
        if (etNesage.getText().toString().equals("")){
            etNesage.requestFocus();
            return;
        }
        if (etNesageMaisu.getText().toString().equals("")){
            etNesageMaisu.requestFocus();
            return;
        }
        //btNesagePrint.callOnClick();
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
        btNesagePrint.setEnabled( true );
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
                datastring = readFile("TEG01.TXT", "TEC");
            }else{
                datastring = readFile("TEG02.TXT", "TEC");
            }
            mBluetoothService.write(datastring, "TEC", Integer.parseInt(etNesageMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("1"))
        {
            if (Nebikiformat.equals("0")){
                datastring = readFile("TLG01.TXT", "TEC");
            }else{
                datastring = readFile("TLG02.TXT", "TEC");
            }
            mBluetoothService.write(datastring, "TEC", Integer.parseInt(etNesageMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("2"))
        {
            if (Nebikiformat.equals("0")){
                datastring = readFile("S4G01.TXT", "SATO");
            }else{
                datastring = readFile("S4G02.TXT", "SATO");
            }
            mBluetoothService.write(datastring, "SATO", Integer.parseInt(etNesageMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("3"))
        {
            if (Nebikiformat.equals("0")){
                datastring = readFile("S3G01.TXT", "SATO");
            }else{
                datastring = readFile("S3G02.TXT", "SATO");
            }
            mBluetoothService.write(datastring, "SATO", Integer.parseInt(etNesageMaisu.getText().toString()));
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
                            if (!etNesageSell.getText().toString().equals("")){
                                String price = "";
                                if (etNesageSell.getText().length() == 1){
                                    price = "   " + etNesageSell.getText().toString();
                                } else if(etNesageSell.getText().length() == 2){
                                    price = "  " + etNesageSell.getText().toString();
                                } else if(etNesageSell.getText().length() == 3){
                                    price = " " + etNesageSell.getText().toString();
                                } else {
                                    price = etNesageSell.getText().toString();
                                }
                                if ((Nebikiformat.equals("0"))){
                                    lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "円を" + "^";
                                }
                                else{
                                    lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
                                }
                            }
                            else{
                                lineBuffer = "";
                            }
                        }else if (lineBuffer.substring(1,2).equals("P")) {
                            String nebiki ="";
                            if (etNesage.getText().length() == 1){
                                nebiki = "   " + etNesage.getText().toString();
                            } else if(etNesage.getText().length() == 2){
                                nebiki = "  " + etNesage.getText().toString();
                            } else if(etNesage.getText().length() == 3){
                                nebiki = " " + etNesage.getText().toString();
                            } else {
                                nebiki = etNesage.getText().toString();
                            }
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + nebiki + "^";
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
                            if (!etNesageSell.getText().toString().equals("")){
                                String price = "";
                                if (etNesageSell.getText().length() == 1){
                                    price = "   " + etNesageSell.getText().toString();
                                } else if(etNesageSell.getText().length() == 2){
                                    price = "  " + etNesageSell.getText().toString();
                                } else if(etNesageSell.getText().length() == 3){
                                    price = " " + etNesageSell.getText().toString();
                                } else {
                                    price = etNesageSell.getText().toString();
                                }
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
                                if (Nebikiformat.equals("0")){
                                    lineBuffer = lineBuffer + "V40^H120^P0^L0102^K8D円を^";;
                                }
                            }
                            else{
                                lineBuffer = "";
                            }
                        }else if (lineBuffer.substring(1,2).equals("P")) {
                            String nebiki ="";
                            if (etNesage.getText().length() == 1){
                                nebiki = "   " + etNesage.getText().toString();
                            } else if(etNesage.getText().length() == 2){
                                nebiki = "  " + etNesage.getText().toString();
                            } else if(etNesage.getText().length() == 3){
                                nebiki = " " + etNesage.getText().toString();
                            } else {
                                nebiki = etNesage.getText().toString();
                            }
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + nebiki + "^";
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
                            Toast.makeText( NesageActivity.this, "プリンタと接続できません.", Toast.LENGTH_SHORT ).show();
                            break;
                        case BluetoothService.STATE_CONNECTED:    // 接続完了
                            // GUIアイテムの有効無効の設定
                            // 切断ボタン、文字列送信ボタンを有効にする
                            //btNesagePrint.setEnabled( true );
                            write();
                            break;
                        case BluetoothService.STATE_CONNECTION_LOST:            // 接続ロスト
                            //Toast.makeText( NesageActivity.this, "Lost connection to the device.", Toast.LENGTH_SHORT ).show();
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
                            ( (TextView)findViewById( R.id.tvNesageStatus ) ).setText( new String( mReadBuffer, 0, mReadBufferCounter ) );
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
                    btNesageClear.callOnClick();
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
