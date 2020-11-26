package com.mobireta.mobiretalabelprint;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NebikiMenuActivity extends AppCompatActivity {

    Button btNebiki;
    Button btWaribiki;
    Button btNesage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nebiki_menu);
        btNebiki = (Button) findViewById(R.id.btNebikiMenu);
        btWaribiki = (Button) findViewById(R.id.btWaribiki);
        btNesage = (Button) findViewById(R.id.btNesage);
        setTitle("モバリテ 値引メニュー" );
    }

    @Override
    protected void onResume() {
        super.onResume();
        btNebiki.setEnabled(true);
        btWaribiki.setEnabled(true);
        btNesage.setEnabled(true);
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

    public void onButtonClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btNebikiMenu:
                intent = new Intent(NebikiMenuActivity.this, NebikiActivity.class);
                btNebiki.setEnabled(false);
                startActivity(intent);
                break;
            case R.id.btWaribiki:
                intent = new Intent(NebikiMenuActivity.this, WaribikiActivity.class);
                btWaribiki.setEnabled(false);
                startActivity(intent);
                break;
            case R.id.btNesage:
                intent = new Intent(NebikiMenuActivity.this, NesageActivity.class);
                btNesage.setEnabled(false);
                startActivity(intent);
                break;
        }
    }
}
