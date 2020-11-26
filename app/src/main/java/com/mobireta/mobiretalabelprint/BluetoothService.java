package com.mobireta.mobiretalabelprint;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.UUID;

public class BluetoothService {

    // 定数（Bluetooth UUID）
    private static final UUID UUID_SPP = UUID.fromString( "00001101-0000-1000-8000-00805f9b34fb" );

    // 定数
    public static final int MESSAGE_STATECHANGE    = 1;
    public static final int MESSAGE_READ           = 2;
    public static final int MESSAGE_WRITTEN        = 3;
    public static final int STATE_NONE             = 0;
    public static final int STATE_CONNECT_START    = 1;
    public static final int STATE_CONNECT_FAILED   = 2;
    public static final int STATE_CONNECTED        = 3;
    public static final int STATE_CONNECTION_LOST  = 4;
    public static final int STATE_DISCONNECT_START = 5;
    public static final int STATE_DISCONNECTED     = 6;
    private BluetoothSocket mBluetoothSocket;
    private InputStream mInput;
    private OutputStream mOutput;

    // メンバー変数
    private int              mState;
    private ConnectionThread mConnectionThread;
    private Handler mHandler;

    // 接続時処理用のスレッド
    private class ConnectionThread extends Thread
    {
        // コンストラクタ
        public ConnectionThread( BluetoothDevice bluetoothdevice )
        {
            try
            {
                mBluetoothSocket = bluetoothdevice.createRfcommSocketToServiceRecord( UUID_SPP );
                mInput = mBluetoothSocket.getInputStream();
                mOutput = mBluetoothSocket.getOutputStream();
            }
            catch( IOException e )
            {
                Log.e( "BluetoothService", "failed : bluetoothdevice.createRfcommSocketToServiceRecord( UUID_SPP )", e );
            }
        }
        // 処理
        public void run()
        {
            while( STATE_DISCONNECTED != mState )
            {
                switch( mState )
                {
                    case STATE_NONE:
                        break;
                    case STATE_CONNECT_START:    // 接続開始
                        try
                        {
                            // BluetoothSocketオブジェクトを用いて、Bluetoothデバイスに接続を試みる。
                            mBluetoothSocket.connect();
                        }
                        catch( IOException e )
                        {    // 接続失敗
                            Log.d( "BluetoothService", "Failed : mBluetoothSocket.connect()" );
                            setState( STATE_CONNECT_FAILED );
                            cancel();    // スレッド終了。
                            return;
                        }
                        // 接続成功
                        setState( STATE_CONNECTED );
                        break;
                    case STATE_CONNECT_FAILED:        // 接続失敗
                        // 接続失敗時の処理の実体は、cancel()。
                        break;
                    case STATE_CONNECTED:        // 接続済み（Bluetoothデバイスから送信されるデータ受信）
                        byte[] buf = new byte[1024];
                        int bytes;
                        try
                        {
                            bytes = mInput.read( buf );
                            mHandler.obtainMessage( MESSAGE_READ, bytes, -1, buf ).sendToTarget();
                        }
                        catch( IOException e )
                        {
                            setState( STATE_CONNECTION_LOST );
                            cancel();    // スレッド終了。
                            break;
                        }
                        break;
                    case STATE_CONNECTION_LOST:    // 接続ロスト
                        // 接続ロスト時の処理の実体は、cancel()。
                        break;
                    case STATE_DISCONNECT_START:    // 切断開始
                        // 切断開始時の処理の実体は、cancel()。
                        break;
                    case STATE_DISCONNECTED:    // 切断完了
                        // whileの条件式により、STATE_DISCONNECTEDの場合は、whileを抜けるので、このケース分岐は無意味。
                        break;
                }
            }
            synchronized( BluetoothService.this )
            {    // 親クラスが保持する自スレッドオブジェクトの解放（自分自身の解放）
                mConnectionThread = null;
            }
        }

        // キャンセル（接続を終了する。ステータスをSTATE_DISCONNECTEDにすることによってスレッドも終了する）
        public void cancel()
        {
            try
            {
                mBluetoothSocket.close();
            }
            catch( IOException e )
            {
                Log.e( "BluetoothService", "Failed : mBluetoothSocket.close()", e );
            }
            setState( STATE_DISCONNECTED );
        }

        // バイト列送信
        public void write( byte[] buf )
        {
            try
            {
                synchronized( BluetoothService.this )
                {
                    mOutput.write( buf );
                }
                mHandler.obtainMessage( MESSAGE_WRITTEN ).sendToTarget();
            }
            catch( IOException e )
            {
                Log.e( "BluetoothService", "Failed : mBluetoothSocket.close()", e );
            }
        }
    }

    // コンストラクタ
    public BluetoothService(Context context, Handler handler, BluetoothDevice device )
    {
        mHandler = handler;
        mState = STATE_NONE;
        // 接続時処理用スレッドの作成と開始
        mConnectionThread = new ConnectionThread( device );
        mConnectionThread.start();
    }

    // ステータス設定
    private synchronized void setState( int state )
    {
        mState = state;
        mHandler.obtainMessage( MESSAGE_STATECHANGE, state, -1 ).sendToTarget();
    }

    // 接続開始時の処理
    public synchronized void connect()
    {
        if( STATE_NONE != mState )
        {    // １つのBluetoothServiceオブジェクトに対して、connect()は１回だけ呼べる。
            // ２回目以降の呼び出しは、処理しない。
            return;
        }
        // ステータス設定
        setState( STATE_CONNECT_START );
    }

    // 接続切断時の処理
    public synchronized void disconnect()
    {
        if( STATE_CONNECTED != mState )
        {    // 接続中以外は、処理しない。
            return;
        }

        // ステータス設定
        setState( STATE_DISCONNECT_START );

        mConnectionThread.cancel();
    }

    // バイト列送信（非同期）
    public void write(String out , String printer, int maisu)
    {
        ConnectionThread connectionThread;
        /*
        synchronized( this )
        {
            if( STATE_CONNECTED != mState )
            {
                return;
            }
            connectionThread = mConnectionThread;
        }
        */
        String data = null;
        try
        {
            if (printer == "TEC"){
                mOutput.write(0x1b);
                mOutput.write("M;B,0".getBytes()); //TPCL
                mOutput.write(0x0a);
                mOutput.write(0x00);
                StringTokenizer st = new StringTokenizer(out, "^");
                while (st.hasMoreTokens()) {
                    mOutput.write(0x1b);
                    data = st.nextToken().replace("^","");
                    if (data.indexOf("P1=") == -1){
                        mOutput.write(data.getBytes("Shift-JIS"));
                    }
                    else
                    {
                        int foundIndex = data.indexOf("P1=");
                        String moji = data.substring( foundIndex + 3 , data.length());
                        String comm = data.substring(0, foundIndex + 3);
                        mOutput.write(comm.getBytes("Shift-JIS"));
                        byte[] bytebuff = null;
                        for (int i = 1; i <= moji.length(); i++)
                        {
                            bytebuff = moji.getBytes("Shift-JIS");;
                        }
                        if (!bytebuff.equals(null)){
                            mOutput.write(bytebuff);
                        }
                    }
                    mOutput.write(0x0a);
                    mOutput.write(0x00);
                }
                mOutput.write(0x1b);
                data = "XS;I," + String.format("%4s", maisu ).replace(" ", "0") + ",0002C4111";
                mOutput.write(data.getBytes("Shift-JIS")); //枚数
                mOutput.write(0x0a);
                mOutput.write(0x00);
            }
            else{
                // MACアドレスからBluetoothDeviceのインスタンスを得る
                mOutput.write(0x2); //PR
                mOutput.write(0x1b);
                mOutput.write("A".getBytes()); //開始
                mOutput.write(0x1b);
                mOutput.write("PR".getBytes()); //開始
                mOutput.write(0x1b);
                // 1行をデータの要素に分割
                StringTokenizer st = new StringTokenizer(out, "^");
                while (st.hasMoreTokens()) {
                    mOutput.write(0x1b);
                    mOutput.write(st.nextToken().replace("^","").getBytes("Shift-JIS"));
                }
                //枚数
                data = "Q" + String.valueOf(maisu);
                mOutput.write(0x1b);
                mOutput.write(data.getBytes());
                //終了
                mOutput.write(0x1b);
                mOutput.write("Z".getBytes()); //終了
                mOutput.write(0x3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 非同期送信
        // （送受信で同期（送信と受信を排他処理（≒同期処理））させる実装も可能だが、
        // 　そうすると、mInput.read( buf ) が完了するまで、mOutput.write( buf ) が実施されなくなる。
        // 　mInput.read( buf ) は文字列を受信すると完了するので、文字列を受信しなければいつまでたっても完了しない。
        // 　文字列が頻繁に送信されてくる場合はよいが、文字列がぜんぜん送信されてこない場合は、
        // 　こちらからの送信がいつまでたっても実施されないことになる。なので、受信と送信は非同期。）
        //connectionThread.write( out );
        //mOutput.write(out);
    }
}
