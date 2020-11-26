package com.mobireta.mobiretalabelprint;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LabelActivity extends AppCompatActivity {

    private MsControl mscontrol;

    Button btLabelUpdate;
    EditText etLabelMaxmaisu;
    EditText etLabelNefuda;
    EditText etLabelShomi;
    EditText etLabelNebiki;
    EditText etLabelBarcode;
    Switch swReaderKbn;
    String stReader;
    Switch swKeyboardKbn;
    String stKeyboard;
    private String kanriid;
    //private String Mobilecd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        etLabelMaxmaisu = (EditText) findViewById(R.id.etLabelMaxmaisu);
        etLabelNefuda = (EditText) findViewById(R.id.etLabelNefuda);
        etLabelShomi = (EditText) findViewById(R.id.etLabelShomi);
        etLabelNebiki = (EditText) findViewById(R.id.etLabelNebiki);
        etLabelBarcode = (EditText) findViewById(R.id.etLabelBarcode);
        btLabelUpdate = (Button) findViewById(R.id.btLabelUpdate);
        //データベースクラス
        mscontrol = MsControlHelper.finddata(this);
        //テキストファリル割り当て
        kanriid = mscontrol.Kanriid.toString();
        //Mobilecd = mscontrol.Mobilecd.toString();
        etLabelMaxmaisu.setText(mscontrol.Maxmaisu.toString());
        etLabelNefuda.setText(mscontrol.Nefudaformat.toString());
        etLabelShomi.setText(mscontrol.Shomiformat.toString());
        etLabelNebiki.setText(mscontrol.Nebikiformat.toString());
        etLabelBarcode.setText(mscontrol.Barcode128.toString());
        stReader = mscontrol.Readerkbn.toString();
        stKeyboard = mscontrol.Keyboardkbn.toString();
        swReaderKbn = (Switch) findViewById(R.id.swReaderKbn);
        if (stReader.equals("1")){
            swReaderKbn.setChecked(true);
        }
        else {
            swReaderKbn.setChecked(false);
        }
        swKeyboardKbn = (Switch) findViewById(R.id.swKeyboardKbn);
        if (stKeyboard.equals("1")){
            swKeyboardKbn.setChecked(true);
        }
        else {
            swKeyboardKbn.setChecked(false);
        }
        swReaderKbn.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener(){
                    public void onCheckedChanged(CompoundButton comButton, boolean isChecked){
                        // オンなら
                        if(isChecked){
                            swReaderKbn.setChecked(true);  // 状態をONに
                            stReader = "1";
                        }
                        // オフなら
                        else{
                            swReaderKbn.setChecked(false);  // 状態をOFFに
                            stReader = "0";
                        }
                    }
                }
        );
        swKeyboardKbn.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener(){
                    public void onCheckedChanged(CompoundButton comButton, boolean isChecked){
                        // オンなら
                        if(isChecked){
                            swKeyboardKbn.setChecked(true);  // 状態をONに
                            stKeyboard = "1";
                        }
                        // オフなら
                        else{
                            swKeyboardKbn.setChecked(false);  // 状態をOFFに
                            stKeyboard = "0";
                        }
                    }
                }
        );
    }

    public void onButtonClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btLabelUpdate:
                if (etLabelMaxmaisu.getText().toString().equals("") || Integer.parseInt(etLabelMaxmaisu.getText().toString()) <= 0)
                {
                    Toast toast = Toast.makeText(LabelActivity.this, "最大枚数を入力してください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (!etLabelNefuda.getText().toString().equals("0"))
                {
                    Toast toast = Toast.makeText(LabelActivity.this, "値札ラベル区分を入力してください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (!etLabelShomi.getText().toString().equals("0"))
                {
                    Toast toast = Toast.makeText(LabelActivity.this, "賞味期限ラベル区分を入力してください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (!etLabelNebiki.getText().toString().equals("0") && !etLabelNebiki.getText().toString().equals("1"))
                {
                    Toast toast = Toast.makeText(LabelActivity.this, "値引シール区分を入力してください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (!etLabelBarcode.getText().toString().equals("0"))
                {
                    Toast toast = Toast.makeText(LabelActivity.this, "バーコード区分を入力してください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                MsControl msc = new MsControl();
                msc.Kanriid = kanriid;
                msc.Readerkbn = stReader;
                msc.Keyboardkbn = stKeyboard;
                msc.Nefudaformat = etLabelNefuda.getText().toString();;
                msc.Shomiformat = etLabelShomi.getText().toString();;
                msc.Nebikiformat = etLabelNebiki.getText().toString();;
                msc.Maxmaisu = etLabelMaxmaisu.getText().toString();;
                msc.Barcode128 = etLabelBarcode.getText().toString();;
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                msc.Updymd = df.format(date);
                MsControlHelper.labelupdate(getApplicationContext(), msc);
                intent = new Intent(LabelActivity.this, OptionMenuActivity.class);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //==== キーコード判定 ====//
            Intent intent = new Intent(LabelActivity.this, OptionMenuActivity.class);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
