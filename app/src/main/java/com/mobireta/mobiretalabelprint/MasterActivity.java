package com.mobireta.mobiretalabelprint;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MasterActivity extends AppCompatActivity {

    private MsControl mscontrol;
    Button btMenu;
    EditText edTenpoCD;
    EditText edMobileCD;
    EditText edKaishakj;
    EditText edZipcd;
    EditText edAddress1;
    EditText edAddress2;
    EditText edTel;
    EditText edFax;
    private String kanriid;
    private String initflag;
    private static final String DEFAULT_ENCORDING = "UTF-8";//デフォルトのエンコード

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        //画面宣言
        btMenu = (Button) findViewById(R.id.btMenuNebiki);
        edTenpoCD = (EditText) findViewById(R.id.edTenpoCD);
        edMobileCD = (EditText) findViewById(R.id.edMobileCD);
        edKaishakj = (EditText) findViewById(R.id.edKaishakj);
        edZipcd = (EditText) findViewById(R.id.edZipcd);
        edAddress1 = (EditText) findViewById(R.id.edAddress1);
        edAddress2 = (EditText) findViewById(R.id.edAddress2);
        edTel = (EditText) findViewById(R.id.edTel);
        edFax = (EditText) findViewById(R.id.edFax);
        //データベースクラス
        initflag= "0";
        mscontrol = MsControlHelper.finddata(this);
        if (mscontrol.Kaishakj.toString() == "")
        {
            initflag = "1";
        }
        //テキストファリル割り当て
        kanriid = mscontrol.Kanriid.toString();
        edTenpoCD.setText(mscontrol.Tenpocd.toString());
        edMobileCD.setText(mscontrol.Mobilecd.toString());
        edKaishakj.setText(mscontrol.Kaishakj.toString());
        edZipcd.setText(mscontrol.Zipcd.toString());
        edAddress1.setText(mscontrol.Address1.toString());
        edAddress2.setText(mscontrol.Address2.toString());
        edTel.setText(mscontrol.Tel.toString());
        edFax.setText(mscontrol.Fax.toString());
    }

    public void onButtonClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btMenuNebiki:
                if (edTenpoCD.getText().toString().equals(""))
                {
                    Toast toast = Toast.makeText(MasterActivity.this, "店舗CDを入力してください。", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if (edMobileCD.getText().toString().equals(""))
                {
                    Toast toast = Toast.makeText(MasterActivity.this, "端末CDを入力してください。", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if (edKaishakj.getText().toString().equals(""))
                {
                    Toast toast = Toast.makeText(MasterActivity.this, "会社を入力してください。", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if (edAddress1.getText().toString().equals(""))
                {
                    Toast toast = Toast.makeText(MasterActivity.this, "住所を入力してください。", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                MsControl msc = new MsControl();
                if (edTel.getText().toString().equals(""))
                {
                    Toast toast = Toast.makeText(MasterActivity.this, "電話番号を入力してください。", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                msc.Kanriid = kanriid;
                msc.Kanrikbn = "0";
                msc.Tenpocd = "000000";
                msc.Mobilecd = "0000";
                msc.Kaishakj = edKaishakj.getText().toString();;
                msc.Zipcd = edZipcd.getText().toString();;
                msc.Address1 = edAddress1.getText().toString();;
                msc.Address2 = edAddress2.getText().toString();;
                msc.Tel = edTel.getText().toString();;
                msc.Fax = edFax.getText().toString();;
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                msc.Updymd = df.format(date);
                MsControlHelper.settingupdate(getApplicationContext(), msc);
                if (initflag == "0"){
                    intent = new Intent(MasterActivity.this, OptionMenuActivity.class);
                }
                else{
                    intent = new Intent(MasterActivity.this, MainMenuActivity.class);
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //==== キーコード判定 ====//
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //-==- Backキー -==-//
            // 以降の処理をキャンセルする。
            Intent intent = new Intent(MasterActivity.this, OptionMenuActivity.class);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
