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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShomiActivity extends AppCompatActivity {

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
    Switch swShomi;
    String Shomikbn;
    String Shohinsell;
    private Integer Maxmaisu;


    Button btShomiPrint;
    Button btShomiClear;
    //Button btShomiMenu;
    Button btShomi1;
    Button btShomi2;
    Button btShomi3;
    Button btShomi5;

    TextView tvShomiStatus;
    TextView tvSeizoday;
    EditText etShomiShohinCD;
    EditText etShomiHinmei;
    EditText etShomiSell;
    EditText etShomiMaisu;
    EditText etShomidate;

    SimpleDateFormat sdf;
    Date Seizodate;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private int princunt = 0;
    private JancodeCheck janchk;
    private KeyboardUtils keyutl;
    private String barcodekind = "0"; //0:JAN1, 1:JAN8, 2:UPC-C
    private String updflag = "0";
    private String tmpshohinkj;
    private String tmpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shomi);
        setTitle("モバリテ 賞味期限" );

        mscontrol = MsControlHelper.finddata(this);
        //テキストファリル割り当て
        Tenpocd = mscontrol.Tenpocd.toString();
        Keyboardkbn = mscontrol.Keyboardkbn.toString();
        Readerkbn = mscontrol.Readerkbn.toString();
        Readeraddress = mscontrol.Readeraddress.toString();
        Keyboardkbn = mscontrol.Keyboardkbn.toString();
        Printerkbn = mscontrol.Printerkbn.toString();
        mDeviceAddress = mscontrol.Printeraddress.toString();
        Maxmaisu = Integer.parseInt(mscontrol.Maxmaisu.toString());

        btShomiPrint = (Button) findViewById(R.id.btShomiPrint);
        btShomiClear = (Button) findViewById(R.id.btShomiClear);
        //btShomiMenu = (Button) findViewById(R.id.btShomiMenu);
        btShomi1 = (Button) findViewById(R.id.btShomi1);
        btShomi2 = (Button) findViewById(R.id.btShomi2);
        btShomi3 = (Button) findViewById(R.id.btShomi3);
        btShomi5 = (Button) findViewById(R.id.btShomi5);
        etShomiShohinCD = (EditText) findViewById(R.id.etShomiShohinCD);
        etShomiHinmei = (EditText) findViewById(R.id.etShomiHinmei);
        etShomidate = (EditText) findViewById(R.id.etShomidate);
        etShomiMaisu = (EditText) findViewById(R.id.etShomiMaisu);
        tvShomiStatus = (TextView) findViewById(R.id.tvShomiStatus);
        tvSeizoday =(TextView) findViewById(R.id.tvSeizoday);
        etShomidate = (EditText) findViewById(R.id.etShomidate);
        Shomikbn = "0";
        // 現在の時刻を取得
        Date date = new Date();
        // 表示形式を設定
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日'");
        Seizodate = date;
        tvSeizoday.setText(sdf.format(date));

        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService( Context.BLUETOOTH_SERVICE );
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if( null == mBluetoothAdapter )
        {    // Android端末がBluetoothをサポートしていない
            Toast.makeText( this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT ).show();
            finish();    // アプリ終了宣言
        }

        etShomiShohinCD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etShomiShohinCD.setTextIsSelectable(true);
                    }
                }else{
                    //etShomiShohinCD.requestFocus();
                }
            }
        });

        etShomiHinmei.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (!etShomiShohinCD.getText().toString().equals("")){
                        trshohilog = trshohinLoglhelper.finddata(getApplicationContext(), etShomiShohinCD.getText().toString());
                        if (trshohilog.Shohinkj != null){
                            etShomiHinmei.setText(trshohilog.Shohinkj);
                            Shohinsell = trshohilog.Sell;
                            etShomidate.setText(trshohilog.Shomiday);
                            updflag = "1";
                            tmpshohinkj = etShomiHinmei.getText().toString();
                            tmpdate = etShomidate.getText().toString();
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

        etShomidate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etShomidate.setTextIsSelectable(true);
                        etShomidate.setText(Integer.valueOf(etShomidate.getText().toString()).toString());
                    }
                }else{
                }
            }
        });

        etShomiMaisu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    if (Keyboardkbn.equals("1")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etShomiMaisu.setTextIsSelectable(true);
                    }
                }else{
                    //etShomiShohinCD.setTextIsSelectable(false);
                }
            }
        });

        btShomi1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etShomiMaisu.setText("1");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btShomi2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etShomiMaisu.setText("2");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btShomi3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etShomiMaisu.setText("3");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        btShomi5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etShomiMaisu.setText("5");
                CheckData();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        swShomi = (Switch) findViewById(R.id.swShomi);
        if (swShomi.equals("1")){
            swShomi.setChecked(true);
        }
        else {
            swShomi.setChecked(false);
        }

        swShomi.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener(){
                    public void onCheckedChanged(CompoundButton comButton, boolean isChecked){
                        // オンなら
                        if(isChecked){
                            swShomi.setChecked(true);  // 状態をONに
                            //swReaderKbn.toggle();  // 状態を反転
                            Shomikbn = "1";
                        }
                        // オフなら
                        else{
                            swShomi.setChecked(false);  // 状態をOFFに
                            Shomikbn = "0";
                        }
                    }
                }
        );


        btShomiClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updflag = "0";
                btShomiPrint.setEnabled(true);
                etShomiShohinCD.setText("");
                etShomiHinmei.setText("");
                etShomidate.setText("");
                etShomiMaisu.setText("");
                etShomiShohinCD.requestFocus();
            }
        });

        btShomiPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etShomiMaisu.getText().toString().equals("")
                        || Integer.parseInt(etShomiMaisu.getText().toString()) <= 0){
                    etShomiMaisu.requestFocus();
                    return;
                }
                if (etShomiHinmei.getText().toString().equals("")){
                    etShomiHinmei.requestFocus();
                    return;
                }
                if (etShomidate.getText().toString().equals("")
                        || Integer.parseInt(etShomidate.getText().toString()) < 0){
                    etShomidate.requestFocus();
                    return;
                }
                if (Integer.parseInt(etShomiMaisu.getText().toString()) > Maxmaisu){
                    etShomiMaisu.requestFocus();
                    Toast toast = Toast.makeText(ShomiActivity.this, "最大印刷枚数を超えています。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                String shohincd = etShomiShohinCD.getText().toString().replace(".","");
                etShomiShohinCD.setText(shohincd);
                janchk = new JancodeCheck();
                if (shohincd.length() == 13){
                    if (!janchk.checkupca(shohincd)){
                        if (!janchk.checkjan13(shohincd)){
                            Toast toast = Toast.makeText(ShomiActivity.this, "JAN13チェックデジットが異ります。", Toast.LENGTH_LONG);
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
                    shohincd = "0" + etShomiShohinCD.getText();
                    if (!janchk.checkupca(shohincd)){
                        Toast toast = Toast.makeText(ShomiActivity.this, "UPC-Aチェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    else{
                        etShomiShohinCD.setText(shohincd);
                        barcodekind = "2"; //0:JAN1, 1:JAN8, 2:UPC-C
                    }
                }else if (shohincd.length() == 8) {
                    if (!janchk.checkjan8(shohincd)) {
                        Toast toast = Toast.makeText(ShomiActivity.this, "JAN8チェックデジットが異ります。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    else{
                        barcodekind = "1"; //0:JAN1, 1:JAN8, 2:UPC-C
                    }
                }else{
                    if (!etShomiShohinCD.getText().toString().equals("")){
                        Toast toast = Toast.makeText(ShomiActivity.this, "JANコードではありません。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }
                btShomiPrint.setEnabled(false);
                connect();
                TrShohinLog tsl = new TrShohinLog();
                if (updflag.equals("0")){
                    tsl.Tenpocd = Tenpocd;
                    tsl.Shohincd = etShomiShohinCD.getText().toString();
                    tsl.Shohinkj = etShomiHinmei.getText().toString();
                    tsl.Sell = Shohinsell;
                    tsl.Shomiday = etShomidate.getText().toString();
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    tsl.Addymd = df.format(date);
                    TrShohinLoglHelper.insert(getApplicationContext(), tsl);
                }
                else{
                    if (etShomiHinmei.getText().toString() != tmpshohinkj
                            && etShomidate.getText().toString() != tmpdate){
                        tsl.Shohincd = etShomiShohinCD.getText().toString();
                        tsl.Shohinkj = etShomiHinmei.getText().toString();
                        tsl.Sell = Shohinsell;
                        tsl.Shomiday = etShomidate.getText().toString();
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date(System.currentTimeMillis());
                        tsl.Addymd = df.format(date);
                        TrShohinLoglHelper.update(getApplicationContext(), tsl);
                    }
                }
                updflag = "0";
                tmpdate= "";
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
        if (etShomiShohinCD.toString().equals(""))
        {
            etShomiShohinCD.requestFocus();
            return false;
        }
        if (etShomiMaisu.toString().equals(""))
        {
            etShomiMaisu.requestFocus();
            return false;
        }
        btShomiPrint.setEnabled(true);
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
                    etShomiShohinCD.setText(barcode.displayValue);
                } else {
                    //statusMessage.setText(R.string.barcode_failure);
                    etShomiShohinCD.requestFocus();
                    //Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                etShomiShohinCD.requestFocus();
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
            btShomiPrint.setEnabled( true );
        }
        if (etShomiShohinCD.getText().toString().equals("")){
            etShomiShohinCD.requestFocus();
            return;
        }
        if (etShomiHinmei.getText().toString().equals("")){
            etShomiHinmei.requestFocus();
            return;
        }
        if (etShomidate.getText().toString().equals("")){
            etShomidate.requestFocus();
            return;
        }
        if (etShomiMaisu.getText().toString().equals("")){
            etShomiMaisu.requestFocus();
            return;
        }
        //btShomiPrint.callOnClick();
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
        btShomiPrint.setEnabled( true );
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
            datastring = readFile("TES01.TXT", "TEC");
            mBluetoothService.write(datastring, "TEC", Integer.parseInt(etShomiMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("1"))
        {
            datastring = readFile("TLS01.TXT", "TEC");
            mBluetoothService.write(datastring, "TEC", Integer.parseInt(etShomiMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("2"))
        {
            datastring = readFile("S4S01.TXT", "SATO");
            mBluetoothService.write(datastring, "SATO", Integer.parseInt(etShomiMaisu.getText().toString()));
        }
        else if (Printerkbn.equals("3") || Printerkbn.equals("3"))
        {
            datastring = readFile("S3S01.TXT", "SATO");
            mBluetoothService.write(datastring, "SATO", Integer.parseInt(etShomiMaisu.getText().toString()));
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
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + etShomiHinmei.getText().toString() + "^";
                        }else if (lineBuffer.substring(1,2).equals("M")) {
                            // カレンダークラスのインスタンスを取得
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(Seizodate);
                            Integer shomidate = Integer.parseInt(etShomidate.getText().toString());
                            cal.add(Calendar.DATE,  shomidate);
                            sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日'");
                            String shimebi = sdf.format(cal.getTime());
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + shimebi + "^";
                        }else if (lineBuffer.substring(1,2).equals("D")) {
                            if (Shomikbn.equals("1")){
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + "^";
                            }
                            else{
                                lineBuffer = null;
                            }
                        }else if (lineBuffer.substring(1,2).equals("Z")) {
                            if (Shomikbn.equals("1")){
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + tvSeizoday.getText().toString() + "^";
                            }
                            else{
                                lineBuffer = null;
                            }
                        }
                    }else{
                        lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
                    }
                }
                else{
                    if (lineBuffer.substring(0,1).equals("#")){
                        if (lineBuffer.substring(1,2).equals("H")){
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + etShomiHinmei.getText().toString() + "^";
                        }else if (lineBuffer.substring(1,2).equals("M")) {
                            // カレンダークラスのインスタンスを取得
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(Seizodate);
                            Integer shomidate = Integer.parseInt(etShomidate.getText().toString());
                            cal.add(Calendar.DATE,  shomidate);
                            sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日'");
                            String shimebi = sdf.format(cal.getTime());
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + shimebi + "^";
                        }else if (lineBuffer.substring(1,2).equals("D")) {
                            if (Shomikbn.equals("1")){
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + "^";
                            }
                            else{
                                lineBuffer = null;
                            }
                        }else if (lineBuffer.substring(1,2).equals("Z")) {
                            if (Shomikbn.equals("1")){
                                lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + tvSeizoday.getText().toString() + "^";
                            }
                        }
                        else{
                            lineBuffer = null;
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
                            Toast.makeText( ShomiActivity.this, "プリンタと接続できません.", Toast.LENGTH_SHORT ).show();
                            break;
                        case BluetoothService.STATE_CONNECTED:    // 接続完了
                            // GUIアイテムの有効無効の設定
                            // 切断ボタン、文字列送信ボタンを有効にする
                            //btShomiPrint.setEnabled( true );
                            write();
                            break;
                        case BluetoothService.STATE_CONNECTION_LOST:            // 接続ロスト
                            //Toast.makeText( ShomiActivity.this, "Lost connection to the device.", Toast.LENGTH_SHORT ).show();
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
                            ( (TextView)findViewById( R.id.tvShomiStatus ) ).setText( new String( mReadBuffer, 0, mReadBufferCounter ) );
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
                    btShomiClear.callOnClick();
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
