package com.mobireta.mobiretalabelprint;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrinterActivity extends AppCompatActivity implements View.OnClickListener {

    private MsControl mscontrol;
    // 定数
    private static final int REQUEST_ENABLEBLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード
    private static final int REQUEST_CONNECTDEVICE   = 2; // デバイス接続要求時の識別コード
    private static final int READBUFFERSIZE          = 1024;    // 受信バッファーのサイズ

    // メンバー変数
    private BluetoothAdapter mBluetoothAdapter;    // BluetoothAdapter : Bluetooth処理で必要
    private String mDeviceName = "";
    private String mDeviceAddress = "";    // デバイスアドレス
    private BluetoothService mBluetoothService;    // BluetoothService : Bluetoothデバイスとの通信処理を担う
    private byte[] mReadBuffer        = new byte[READBUFFERSIZE];
    private int    mReadBufferCounter = 0;
    private String kanriid;
    private String injiflg = "0";

    RadioGroup rgPrinter;
    RadioButton rbEP2DL;
    RadioButton rbLP2DL;
    RadioButton rbPw208;
    RadioButton rbPt208;
    String radiokbn;
    Button btInji;
    Button btPSetting;

    TextView tvPrintername;
    TextView tvPrinterAddress;
    //private MsControl msc;
    private static final String DEFAULT_ENCORDING = "UTF-8";//デフォルトのエンコード

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        setTitle("モバリテ プリンタ設定" );

        //店舗コード
        Intent intent = getIntent();
        // インテントに保存されたデータを取得
        mscontrol = MsControlHelper.finddata(this);
        //テキストファリル割り当て

        kanriid = mscontrol.Kanriid.toString();
        radiokbn = mscontrol.Printerkbn.toString();
        mDeviceName = mscontrol.Printernm.toString();
        mDeviceAddress = mscontrol.Printeraddress.toString();
        btInji = (Button)findViewById( R.id.btInji);
        btInji.setOnClickListener( this );
        btInji.setEnabled( false );    // 切断ボタンの無効化（連打対策）
        btPSetting = (Button)findViewById( R.id.btPSetting);
        btPSetting.setOnClickListener( this );
        rgPrinter = (RadioGroup) findViewById(R.id.rgPrinter);
        rbEP2DL = (RadioButton) findViewById( R.id.rbEP2DL);
        rbLP2DL = (RadioButton) findViewById( R.id.rbLP2DL);
        rbPw208 = (RadioButton) findViewById( R.id.rbPw208);
        rbPt208 = (RadioButton) findViewById( R.id.rbPt208);
        tvPrintername = (TextView) findViewById( R.id.tvPrintername);
        tvPrinterAddress  = (TextView) findViewById( R.id.tvPrinterAddress);
        tvPrintername.setText(mDeviceName);
        tvPrinterAddress.setText(mDeviceAddress);
        // Bluetoothアダプタの取得
        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService( Context.BLUETOOTH_SERVICE );
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if( null == mBluetoothAdapter )
        {    // Android端末がBluetoothをサポートしていない
            Toast.makeText( this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT ).show();
            finish();    // アプリ終了宣言
            return;
        }
        if (mDeviceAddress.equals("")){
            btInji.setEnabled(false);
            btPSetting.setEnabled(false);
        }
        //TEC EP2DLをデフォルト
        //radiokbn = "0";
        rgPrinter = (RadioGroup)findViewById(R.id.rgPrinter);
        if (radiokbn.equals("0")){
            rbEP2DL.setChecked(true);
            rgPrinter.check(R.id.rbEP2DL);
        }
        else if(radiokbn.equals("1")){
            rbLP2DL.setChecked(true);
            rgPrinter.check(R.id.rbLP2DL);
        }
        else if(radiokbn.equals("2")){
            rbPw208.setChecked(true);
            rgPrinter.check(R.id.rbPw208);
        }
        else if(radiokbn.equals("3")){
            rbPt208.setChecked(true);
            rgPrinter.check(R.id.rbPt208);
        }
        rgPrinter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = rgPrinter.findViewById(checkedId);
                int index = rgPrinter.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        radiokbn = "0";
                        break;
                    case 1:
                        radiokbn = "1";
                        break;
                    case 2:
                        radiokbn = "2";
                        break;
                    case 3:
                        radiokbn = "3";
                        break;
                }
            }
        });
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
            btInji.setEnabled( true );
        }
        // 接続ボタンを押す
        //btInji.callOnClick();
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
        //btInji.setEnabled( true );
    }

    // 機能の有効化ダイアログの操作結果
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        switch( requestCode )
        {
            case REQUEST_ENABLEBLUETOOTH: // Bluetooth有効化要求
                if( Activity.RESULT_CANCELED == resultCode )
                {    // 有効にされなかった
                    Toast.makeText( this, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT ).show();
                    finish();    // アプリ終了宣言
                    return;
                }
                break;
            case REQUEST_CONNECTDEVICE: // デバイス接続要求
                String strDeviceName;
                if( Activity.RESULT_OK == resultCode )
                {
                    // デバイスリストアクティビティからの情報の取得
                    strDeviceName = data.getStringExtra( DeviceListActivity.EXTRAS_DEVICE_NAME );
                    mDeviceAddress = data.getStringExtra( DeviceListActivity.EXTRAS_DEVICE_ADDRESS );
                }
                else
                {
                    strDeviceName = "";
                    mDeviceAddress = "";
                }
                ( (TextView)findViewById( R.id.tvPrintername ) ).setText( strDeviceName );
                ( (TextView)findViewById( R.id.tvPrinterAddress ) ).setText( mDeviceAddress );
                btInji.setEnabled( true );
                break;
        }
        super.onActivityResult( requestCode, resultCode, data );
    }

    // オプションメニュー作成時の処理
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.activity_main, menu );
        return true;
    }

    // オプションメニューのアイテム選択時の処理
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case R.id.menuitem_search:
                Intent devicelistactivityIntent = new Intent( this, DeviceListActivity.class );
                startActivityForResult( devicelistactivityIntent, REQUEST_CONNECTDEVICE );
                return true;
            case R.id.menuitem_bt:
                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onClick( View v )
    {
        if( btInji.getId() == v.getId() )
        {
            if (tvPrintername.getText().toString().equals("") || tvPrinterAddress.getText().toString().equals(""))
            {
                return;
            }
            if (radiokbn.equals("0") || radiokbn.equals("1")){
                if (!tvPrintername.getText().toString().substring(0, 4).equals("TOSH")){
                    Toast toast = Toast.makeText(PrinterActivity.this, "選択したプリンタと機種の設定にミスがあります。", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
            }
            else{
                //String a = tvPrintername.getText().toString().substring(0, 4);
                if (!tvPrintername.getText().toString().substring(0, 4).equals("SATO") && !tvPrintername.getText().toString().substring(0, 4).equals("RJ-2")){
                    Toast toast = Toast.makeText(PrinterActivity.this, "選択したプリンタと機種の設定にミスがあります。", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
            }
            btInji.setEnabled(false);
            MsControl msc = new MsControl();
            msc.Printerkbn = radiokbn;
            msc.Kanriid = kanriid;
            msc.Printernm = tvPrintername.getText().toString();
            msc.Printeraddress = tvPrinterAddress.getText().toString();
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            msc.Updymd = df.format(date);
            MsControlHelper.printerupdate(getApplicationContext(), msc);
            connect();            // 接続
            //btInji.setEnabled(false);
            btPSetting.setEnabled(true);
            return;
        }
        else if( btPSetting.getId() == v.getId() )
        {
            if (tvPrintername.getText().toString().equals("") || tvPrinterAddress.getText().toString().equals(""))
            {
                Toast toast = Toast.makeText(PrinterActivity.this, "プリンタとの接続を必ず確認してください。", Toast.LENGTH_LONG);
                toast.show();
                toast.setGravity(Gravity.CENTER, 0, 0);
                return;
            }
            if (radiokbn.equals("0") || radiokbn.equals("1")){
                if (!tvPrintername.getText().toString().substring(0, 14).equals("TOSHIBA TEC BT")){
                    Toast toast = Toast.makeText(PrinterActivity.this, "選択したプリンタと機種の設定にミスがあります。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
            }else if (radiokbn.equals("2")) {
                if (!tvPrintername.getText().toString().substring(0, 12).equals("SATO PRINTER")){
                    Toast toast = Toast.makeText(PrinterActivity.this, "選択したプリンタと機種の設定にミスがあります。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
            }else if (radiokbn.equals("2")) {
                if (!tvPrintername.getText().toString().substring(0, 6).equals("RJ-2150")){
                    Toast toast = Toast.makeText(PrinterActivity.this, "選択したプリンタと機種の設定にミスがあります。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
            }
            if (injiflg =="0"){
                Toast toast = Toast.makeText(PrinterActivity.this, "プリンタに印字していません。", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            Intent intent = new Intent(PrinterActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
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
        if (rbEP2DL.isChecked())
        {
            datastring = readFile("TET01.TXT", "TEC");
            mBluetoothService.write(datastring, "TEC", 1);
        }
        else if (rbLP2DL.isChecked())
        {
            datastring = readFile("TLT01.TXT", "TEC");
            mBluetoothService.write(datastring, "TEC", 1);
        }
        else if (rbPw208.isChecked())
        {
            datastring = readFile("S4T01.TXT", "SATO");
            mBluetoothService.write(datastring, "SATO", 1);
        }
        else if (rbPt208.isChecked())
        {
            datastring = readFile("S3T01.TXT", "SATO");
            mBluetoothService.write(datastring, "SATO", 1);
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
                        if (lineBuffer.substring(1,2).equals("T")){
                            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date(System.currentTimeMillis());
                            String printtime = df.format(date);
                            lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + printtime + "^";
                        }else if (lineBuffer.substring(1,2).equals("D")){
                            lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + tvPrintername.getText().toString()+ "^";
                        }else if (lineBuffer.substring(1,2).equals("A")) {
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + tvPrinterAddress.getText().toString() + "^";
                        }
                    }else{
                        lineBuffer = lineBuffer + "^"; //Stringにてくくられるので、＃分割処理
                    }
                }
                else{
                    if (lineBuffer.substring(0,1).equals("#")){
                        if (lineBuffer.substring(1,2).equals("T")){
                            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date(System.currentTimeMillis());
                            String printtime = df.format(date);
                            lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + printtime + "^";
                        }else if (lineBuffer.substring(1,2).equals("D")){
                            lineBuffer = lineBuffer.substring(2,lineBuffer.length()) + tvPrintername.getText().toString()+ "^";
                        }else if (lineBuffer.substring(1,2).equals("A")) {
                            lineBuffer = lineBuffer.substring(2, lineBuffer.length()) + tvPrinterAddress.getText().toString() + "^";
                        }
                    }else{
                        lineBuffer = lineBuffer + "^"; //Windows のツールと頃なるため、最後に「:」をつけること
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
                            Toast.makeText( PrinterActivity.this, "Failed to connect to the device.", Toast.LENGTH_SHORT ).show();
                            break;
                        case BluetoothService.STATE_CONNECTED:    // 接続完了
                            // GUIアイテムの有効無効の設定
                            // 切断ボタン、文字列送信ボタンを有効にする
                            write();
                            injiflg = "1";
                            //btInji.setEnabled( true );
                            break;
                        case BluetoothService.STATE_CONNECTION_LOST:            // 接続ロスト
                            //Toast.makeText( PrinterActivity.this, "Lost connection to the device.", Toast.LENGTH_SHORT ).show();
                            break;
                        case BluetoothService.STATE_DISCONNECT_START:
                            // GUIアイテムの有効無効の設定
                            // 切断ボタン、文字列送信ボタンを無効にする
                            break;
                        case BluetoothService.STATE_DISCONNECTED:            // 切断完了
                            // GUIアイテムの有効無効の設定
                            // 接続ボタンを有効にする
                            //mButton_Connect.setEnabled( true );
                            mBluetoothService = null;    // BluetoothServiceオブジェクトの解放
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
