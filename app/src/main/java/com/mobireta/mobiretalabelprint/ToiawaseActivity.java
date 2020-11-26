package com.mobireta.mobiretalabelprint;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ToiawaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiawase);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mailer 呼び出し
                callMailer();
            }
        });
    }

    private void callMailer(){
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:androidsupport@mobireta.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "ラベルプリントお問い合わせ");
        //本文作成
        String honbun = "Appli Name" + String.valueOf(versionCode) + "\r\n";
        honbun = honbun + "Appli ver" + String.valueOf(versionCode) + "\r\n";
        honbun = honbun + "** お問い合わせ内容を記載してください。**"  + "\r\n";
        honbun = honbun + "\r\n";
        intent.putExtra(Intent.EXTRA_TEXT, honbun);
        //intent.putExtra(Intent.EXTRA_TEXT, "本文です。");
        //intent.putExtra(Intent.EXTRA_CC, new String[]{"cc@hoge.com"});
        //intent.putExtra(Intent.EXTRA_BCC, new String[]{"bcc@hoge.com"});
        startActivity(Intent.createChooser(intent, null));
    }

}
