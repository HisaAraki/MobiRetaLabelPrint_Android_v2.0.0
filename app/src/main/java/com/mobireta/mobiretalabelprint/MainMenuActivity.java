package com.mobireta.mobiretalabelprint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;

public class MainMenuActivity extends AppCompatActivity {

    private MsControl mscontrol;
    private MsControlHelper msControlHelper;
    private String mDeviceAddress = "";    // デバイスアドレス
    private String kanrikbn;
    private String Licenseid;
    Button btNefuda;
    Button btShomi;
    Button btNebikiMenu;
    Button btSetting;
    TextView txUertouroku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.activity_main_menu);
        setTitle("モバリテ ラベルプリント");
        // タイトルバー左の1つ目のアイコンリソースを使用します
        txUertouroku = (TextView) findViewById(R.id.txUertouroku);
        btNefuda = (Button) findViewById(R.id.btNefuda);
        btShomi = (Button) findViewById(R.id.btShomi);
        btNebikiMenu = (Button) findViewById(R.id.btNebikiMenu);
        btSetting = (Button) findViewById(R.id.btSetting);
        mscontrol = msControlHelper.finddata(this);
        String m = mscontrol.Kanrikbn.toString();
        kanrikbn = mscontrol.Kanrikbn.toString();
        Licenseid = mscontrol.Tourokuid.toString();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,},
                    20000);

        }

        mDeviceAddress = mscontrol.Printeraddress.toString();
        if (kanrikbn.equals("0")){
            txUertouroku.setTextColor(Color.RED);
            txUertouroku.setText("ライセンスID : 未登録");
        }else{
            //txUertouroku.setTextColor(Color.BLACK);
            //txUertouroku.setText(Licenseid);
            txUertouroku.setText("ライセンスID : " + Licenseid);
            //txUertouroku.setText("ライセンスID : " + mscontrol.Tourokuid.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.activity_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuitem_option:
                intent = new Intent(MainMenuActivity.this, OptionMenuActivity.class);
                startActivity(intent);
                //finish();
                break;
            //case R.id.action_labelsetting:
              //  intent = new Intent(MainMenuActivity.this, LabelActivity.class);
                //startActivity(intent);
                //finish();
                //break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        btNefuda.setEnabled(true);
        btShomi.setEnabled(true);
        btNebikiMenu.setEnabled(true);
        btSetting.setEnabled(true);
    }

    public void onButtonClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btNefuda:
                if (kanrikbn.equals("")) {
                    Toast toast = Toast.makeText(MainMenuActivity.this, "マスタ登録を行ってください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                else if (mDeviceAddress.equals("")){
                    Toast toast = Toast.makeText(MainMenuActivity.this, "プリンタが設定させていません。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }else{
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA,},
                                1000);
                    }
                    intent = new Intent(MainMenuActivity.this, NefudaActivity.class);
                    startActivity(intent);
                    //startActivityForResult(intent, REQUEST_CODE);
                    btNefuda.setEnabled(false);
                    //finish();
                }
                break;
            case R.id.btShomi:
                if (kanrikbn.equals("")){
                    Toast toast = Toast.makeText(MainMenuActivity.this, "マスタ登録を行ってください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                else if(kanrikbn.equals("0")){
                    Toast toast = Toast.makeText(MainMenuActivity.this, "ユーザ登録を行ってください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                else if(mDeviceAddress.equals("")){
                        Toast toast = Toast.makeText(MainMenuActivity.this, "プリンタが設定させていません。", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                }else{
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA,},
                                1000);
                    }
                    intent = new Intent(MainMenuActivity.this, ShomiActivity.class);
                    btShomi.setEnabled(false);
                    startActivity(intent);
                }
                break;
            case R.id.btNebikiMenu:
                if (kanrikbn.equals("")){
                    Toast toast = Toast.makeText(MainMenuActivity.this, "マスタ登録を行ってください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                else if(kanrikbn.equals("0")){
                    Toast toast = Toast.makeText(MainMenuActivity.this, "ユーザ登録を行ってください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                else if(mDeviceAddress.equals("")){
                    Toast toast = Toast.makeText(MainMenuActivity.this, "プリンタが設定させていません。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }else{
                    intent = new Intent(MainMenuActivity.this, NebikiMenuActivity.class);
                    btNebikiMenu.setEnabled(false);
                    startActivity(intent);
                }
                break;
            case R.id.btSetting:
                if (kanrikbn.equals("")){
                    Toast toast = Toast.makeText(MainMenuActivity.this, "マスタ登録を行ってください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                else{
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,},
                                20000);

                    }
                }
                intent = new Intent(MainMenuActivity.this, PrinterActivity.class);
                startActivity(intent);
                btSetting.setEnabled(false);
                //finish();
                break;
        }
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
