package com.mobireta.mobiretalabelprint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.provider.Settings;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.mobireta.mobiretalabelprint.barcodereader.ui.camera.GraphicOverlay;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NefudaActivity extends AppCompatActivity {

    private MsControl mscontrol;
    private TrShohinLog trshohilog;
    private TrShohinLoglHelper trshohinLoglhelper;
    // 定数
    private static final int REQUEST_ENABLEBLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード
    private static final int REQUEST_CONNECTDEVICE   = 2; // デバイス接続要求時の識別コード
    private static final int READBUFFERSIZE          = 1024;    // 受信バッファーのサイズ

    // メンバー変数
    private BluetoothAdapter mBluetoothAdapter;    // BluetoothAdapter : Bluetooth処理で必要
    private String mDeviceAddress = "";    // デバイスアドレス
    private BluetoothService mBluetoothService;    // BluetoothService : Bluetoothデバイスとの通信処理を担う
    private byte[] mReadBuffer        = new byte[READBUFFERSIZE];
    private int    mReadBufferCounter = 0;
    private String Tenpocd;
    private String Readerkbn;
    private String Readeraddress;
    private String Keyboardkbn;
    private String Printerkbn;
    private Integer Maxmaisu;

    Button btNefudaPrint;
    Button btNefudaClear;
    //Button btNefudaMenu;
    Button btNefuda1;
    Button btNefuda2;
    Button btNefuda3;
    Button btNefuda5;

    TextView tvNefudaStatus;
    EditText etNefudaShohinCD;
    EditText etNefudaHinmei;
    EditText etNefudaSell;
    EditText etNefudaMaisu;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private int princunt = 0;
    private JancodeCheck janchk;
    private KeyboardUtils keyutl;
    private String barcodekind = "0"; //0:JAN1, 1:JAN8, 2:UPC-C
    private String updflag = "0";
    private String tmpshohinkj;
    private String tmpsell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nefuda);
        setTitle("モバリテ 値札ラベル" );

        mscontrol = MsControlHelper.finddata(this);
        //テキストファリル割り当て
        Tenpocd = mscontrol.Tenpocd.toString();
        Keyboardkbn = mscontrol.Keyboardkbn.toString();
        Readerkbn = mscontrol.Readerkbn.toString();
        Readeraddress = mscontrol.Readeraddress.toString();
        Printerkbn = mscontrol.Printerkbn.toString();
        mDeviceAddress = mscontrol.Printeraddress.toString();
        Maxmaisu = Integer.parseInt(mscontrol.Maxmaisu.toString());

        btNefudaPrint = (Button) findViewById(R.id.btNefudaPrint);
        btNefudaClear = (Button) findViewById(R.id.btNefudaClear);
        btNefuda1 = (Button) findViewById(R.id.btNefuda1);
        btNefuda2 = (Button) findViewById(R.id.btNefuda2);
        btNefuda3 = (Button) findViewById(R.id.btNefuda3);
        btNefuda5 = (Button) findViewById(R.id.btNefuda5);
        etNefudaShohinCD = (EditText) findViewById(R.id.etNefudaShohinCD);
        etNefudaHinmei = (EditText) findViewById(R.id.etNefudaHinmei);
        etNefudaSell = (EditText) findViewById(R.id.etNefudaSell);
        etNefudaMaisu = (EditText) findViewById(R.id.etNefudaMaisu);
        tvNefudaStatus = (TextView) findViewById(R.id.tvNefudaStatus);
        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService( Context.BLUETOOTH_SERVICE );
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if( null == mBluetoothAdapter )
        {    // Android端末がBluetoothをサポートしていない
            Toast.makeText( this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT ).show();
            finish();    // アプリ終了宣言
        }
        etNefudaShohinCD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNefudaShohinCD.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        etNefudaHinmei.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (!etNefudaShohinCD.getText().toString().equals("")){
                        trshohilog = trshohinLoglhelper.finddata(getApplicationContext(), etNefudaShohinCD.getText().toString());
                        if (trshohilog.Shohinkj != null){
                            etNefudaHinmei.setText(trshohilog.Shohinkj);
                            etNefudaSell.setText(trshohilog.Sell);
                            updflag = "1";
                            tmpshohinkj = etNefudaHinmei.getText().toString();
                            tmpsell = etNefudaSell.getText().toString();
                            //etNefudaHinmei.requestFocus();
                        }
                    }
                    else{
                        updflag = "0";
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        etNefudaSell.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNefudaSell.setTextIsSelectable(true);
                    }
                }else{
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        etNefudaMaisu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etNefudaMaisu.setTextIsSelectable(true);
                    }
                }else{
                    etNefudaMaisu.setTextIsSelectable(false);
                    //etNefudaShohinCD.setTextIsSelectable(false);
                }
            }
        });

        btNefuda1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNefudaMaisu.setText("1");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNefuda2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNefudaMaisu.setText("2");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNefuda3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNefudaMaisu.setText("3");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNefuda5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNefudaMaisu.setText("5");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btNefudaClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btNefudaPrint.setEnabled(true);
                etNefudaShohinCD.setText("");
                etNefudaHinmei.setText("");
                etNefudaSell.setText("");
                etNefudaMaisu.setText("");
                etNefudaShohinCD.requestFocus();
            }
        });

        btNefudaPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNefudaShohinCD.getText().toString().equals("")){
                    etNefudaShohinCD.requestFocus();
                    return;
                }
                if (etNefudaMaisu.getText().toString().equals("")
                        || Integer.parseInt(etNefudaMaisu.getText().toString()) <= 0){
                    etNefudaMaisu.requestFocus();
                    return;
                }
                if (etNefudaHinmei.getText().toString().equals("")){
                    etNefudaHinmei.requestFocus();
                    return;
                }
                if (etNefudaSell.getText().toString().equals("")){
                    etNefudaSell.requestFocus();
                    return;
                }
                if (Integer.parseInt(etNefudaMaisu.getText().toString()) > Maxmaisu){
                    etNefudaMaisu.requestFocus();
                    Toast toast = Toast.makeText(NefudaActivity.this, "最大印刷枚数を超えています。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                String shohincd = etNefudaShohinCD.getText().toString().replace(".","");
                etNefudaShohinCD.setText(shohincd);
                janchk = new JancodeCheck();
                if (shohincd.length() == 13){
                    if (!janchk.checkupca(shohincd)){
                        if (!janchk.checkjan13(shohincd)){
                            Toast toast = Toast.makeText(NefudaActivity.this, "JAN13チェックデジットが異ります。", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        else{
                            barcodekind = "0"; //0:JAN1, 1:JAN8, 2:UPC-C
                        }
                    }
                    else{
                        barcodekind = "2"; //0:JAN1, 1:JAN8, 2:UPC-C
                    }
                }else if (shohincd.length() == 12) {
                    shohincd = "0" + etNefudaShohinCD.getText();
                    if (!janchk.checkupca(shohincd)){
                        Toast toast = Toast.makeText(NefudaActivity.this, "UPC-Aチェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    else{
                        etNefudaShohinCD.setText(shohincd);
                        barcodekind = "2"; //0:JAN1, 1:JAN8, 2:UPC-C
                    }
                }else if (shohincd.length() == 8) {
                    if (!janchk.checkjan8(shohincd)) {
                        Toast toast = Toast.makeText(NefudaActivity.this, "JAN8チェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    else{
                        barcodekind = "1"; //0:JAN1, 1:JAN8, 2:UPC-C
                    }
                }else{
                    Toast toast = Toast.makeText(NefudaActivity.this, "JANコードではありません。", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                btNefudaPrint.setEnabled(false);
                connect();
                TrShohinLog tsl = new TrShohinLog();
                if (updflag.equals("0")){
                    tsl.Tenpocd = Tenpocd;
                    tsl.Shohincd = etNefudaShohinCD.getText().toString();
                    tsl.Shohinkj = etNefudaHinmei.getText().toString();
                    tsl.Sell = etNefudaSell.getText().toString();
                    tsl.Shomiday = "0";
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    tsl.Addymd = df.format(date);
                    TrShohinLoglHelper.insert(getApplicationContext(), tsl);
                }
                else{
                    if (etNefudaHinmei.getText().toString() != tmpshohinkj
                            && etNefudaSell.getText().toString() != tmpsell){
                        tsl.Shohincd = etNefudaShohinCD.getText().toString();
                        tsl.Shohinkj = etNefudaHinmei.getText().toString();
                        tsl.Sell = etNefudaSell.getText().toString();
                        tsl.Shomiday = "0";
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date(System.currentTimeMillis());
                        tsl.Addymd = df.format(date);
                        TrShohinLoglHelper.update(getApplicationContext(), tsl);
                    }
                }
                updflag = "0";
                tmpsell= "";
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
        if (etNefudaShohinCD.toString().equals(""))
        {
            etNefudaShohinCD.requestFocus();
            return false;
        }
        if (etNefudaMaisu.toString().equals(""))
        {
            etNefudaMaisu.requestFocus();
            return false;
        }
        btNefudaPrint.setEnabled(true);
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
                    etNefudaShohinCD.setText(barcode.displayValue);
                } else {
                    //statusMessage.setText(R.string.barcode_failure);
                    etNefudaShohinCD.requestFocus();
                    //Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                etNefudaShohinCD.requestFocus();
                //statusMessage.setText(String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            //etNefudaShohinCD.requestFocus();
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
            btNefudaPrint.setEnabled( true );
        }
        if (etNefudaShohinCD.getText().toString().equals("")){
            etNefudaShohinCD.requestFocus();
            return;
        }
        if (etNefudaHinmei.getText().toString().equals("")){
            etNefudaHinmei.requestFocus();
            return;
        }
        if (etNefudaMaisu.getText().toString().equals("")){
            etNefudaMaisu.requestFocus();
            return;
        }
        //btNefudaPrint.callOnClick();
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
        btNefudaPrint.setEnabled( true );
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
            datastring = readFile("TEF01.TXT", "TEC");
            mBluetoothService.write(datastring, "TEC", Integer.parseInt(etNefudaMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("1"))
        {
            datastring = readFile("TLF01.TXT", "TEC");
            mBluetoothService.write(datastring, "TEC", Integer.parseInt(etNefudaMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("2"))
        {
            datastring = readFile("S4F01.TXT", "SATO");
            mBluetoothService.write(datastring, "SATO", Integer.parseInt(etNefudaMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("3"))
        {
            datastring = readFile("S3F01.TXT", "SATO");
            mBluetoothService.write(datastring, "SATO", Integer.parseInt(etNefudaMaisu.getText().toString()));
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
                        if (lineBuffer.substring(1,2).equals("H")){
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + etNefudaHinmei.getText().toString() + "^";
                        }else if (lineBuffer.substring(1,2).equals("B")){
                            if (barcodekind.equals("0")){ //JAN13
                                lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + etNefudaShohinCD.getText().toString().substring(0, etNefudaShohinCD.length() - 1) + "^";
                            }
                            else if (barcodekind.equals("1")){ //JAN8
                                //LP2DL対応
                                if (Printerkbn.equals("1")){
                                    lineBuffer = lineBuffer.substring(2,lineBuffer.length()).replace("XB00;0050,0080,5,", "XB00;0030,0100,0,") + etNefudaShohinCD.getText().toString().substring(0, etNefudaShohinCD.length() - 1) + "^";
                                }else{
                                    lineBuffer = lineBuffer.substring(2,lineBuffer.length()).replace("XB00;0030,0100,5,3,", "XB00;0030,0100,0,3,") + etNefudaShohinCD.getText().toString().substring(0, etNefudaShohinCD.length()) + "^";
                                }
                            }
                            else { //UPC-A
                                lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + etNefudaShohinCD.getText().toString().substring(0, etNefudaShohinCD.length() - 1) + "^";
                                //lineBuffer = lineBuffer.substring(2,lineBuffer.length()).replace("XB00;0030,0100,5,3,", "XB00;0030,0100,5,1,")  + etNefudaShohinCD.getText().toString().substring(1, etNefudaShohinCD.length())  + "^";
                                //lineBuffer = lineBuffer.substring(2,lineBuffer.length()).replace("XB00;0030,0100,5,", "XB00;0030,0100,K,")  + etNefudaShohinCD.getText().toString() + "^";
                            }
                        }else if (lineBuffer.substring(1,2).equals("P")) {
                            String price;
                            if (etNefudaSell.getText().length() == 1){
                                price = "    " + etNefudaSell.getText().toString();
                            } else if(etNefudaSell.getText().length() == 2){
                                price = "   " + etNefudaSell.getText().toString();
                            } else if(etNefudaSell.getText().length() == 3){
                                price = "  " + etNefudaSell.getText().toString();
                            } else if(etNefudaSell.getText().length() == 4){
                                price = " " + etNefudaSell.getText().toString();
                            } else {
                                price = etNefudaSell.getText().toString();
                            }
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
                        }
                    }else{
                        lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
                    }
                }
                else{
                    if (lineBuffer.substring(0,1).equals("#")){
                         if (lineBuffer.substring(1,2).equals("H")){
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + etNefudaHinmei.getText().toString() + "^";
                        }else if (lineBuffer.substring(1,2).equals("B")){
                            //lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + etNefudaShohinCD.getText().toString().substring(0, etNefudaShohinCD.length() - 1) + "^";
                            if (etNefudaShohinCD.length() == 8) {
                                lineBuffer = lineBuffer.substring(2,lineBuffer.length()).replace("^B3", "^B4") + etNefudaShohinCD.getText().toString().substring(0, etNefudaShohinCD.length() - 1) + "^";

                            }else{
                                lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + etNefudaShohinCD.getText().toString().substring(0, etNefudaShohinCD.length() - 1) + "^";
                                //lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + etNefudaShohinCD.getText().toString() + "^";
                            }
                        }else if (lineBuffer.substring(1,2).equals("P")) {
                            String price;
                            if (etNefudaSell.getText().length() == 1){
                                price = "    " + etNefudaSell.getText().toString();
                            } else if(etNefudaSell.getText().length() == 2){
                                price = "   " + etNefudaSell.getText().toString();
                            } else if(etNefudaSell.getText().length() == 3){
                                price = "  " + etNefudaSell.getText().toString();
                            } else if(etNefudaSell.getText().length() == 4){
                                price = " " + etNefudaSell.getText().toString();
                            } else {
                                price = etNefudaSell.getText().toString();
                            }
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + price + "^";
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
                            Toast.makeText( NefudaActivity.this, "プリンタと接続できません.", Toast.LENGTH_SHORT ).show();
                            break;
                        case BluetoothService.STATE_CONNECTED:    // 接続完了
                            // GUIアイテムの有効無効の設定
                            // 切断ボタン、文字列送信ボタンを有効にする
                            //btNefudaPrint.setEnabled( true );
                            write();
                            break;
                        case BluetoothService.STATE_CONNECTION_LOST:            // 接続ロスト
                            //Toast.makeText( NefudaActivity.this, "Lost connection to the device.", Toast.LENGTH_SHORT ).show();
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
                            ( (TextView)findViewById( R.id.tvNefudaStatus ) ).setText( new String( mReadBuffer, 0, mReadBufferCounter ) );
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
                    btNefudaClear.callOnClick();
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
