package com.mobireta.mobiretalabelprint;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

// POST の場合
// import java.io.BufferedWriter;
// import java.io.OutputStream;
// import java.io.OutputStreamWriter;

// doInBackground の引数の型: String
// publishProgress の引数の型: Void (今回は使用しません)
// doInBackground の返り値の型: String

public class HttpAsyncTask extends AsyncTask<String, Void, String> {

    // ロガーのタグ
    private static final String TAG = "HttpAsyncTask";
    // UI スレッドから操作するビュー
    private TextView txResult;
    Context mcontext;

    HttpAsyncTask(Context context) {
        // 本メソッドは UI スレッドで処理されます。
        super();
        GoogleLoginActivity googleLoginActivity = (GoogleLoginActivity)context;
        mcontext = (GoogleLoginActivity)context;
        txResult = (TextView)googleLoginActivity.findViewById(R.id.txResult);
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        HttpsURLConnection connection = null;
        try {
            //Basic認証設定
            String USERNAME = params[0];
            String PASSWORD  = params[1];
            String userPassword = USERNAME + ":" + PASSWORD;
            String encodeAuthorization = Base64.encodeToString(userPassword.getBytes(), Base64.NO_WRAP);
            // URL 文字列をセットします。
            URL url = new URL(params[2]);
            connection = (HttpsURLConnection)url.openConnection();
            connection.setConnectTimeout(3000); // タイムアウト 3 秒
            connection.setReadTimeout(3000);
            // GET リクエストの実行
            //connection.setRequestMethod("GET");
            //connection.connect();
            // // POST リクエストの実行
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Language", "jp");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Authorization", "Basic " + encodeAuthorization);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(params[3]);
            writer.close();
            connection.connect();
            // レスポンスコードの確認します。
            int responseCode = connection.getResponseCode();
            if(responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP responseCode: " + responseCode);
            }
            // 文字列化します。
            inputStream = connection.getInputStream();
            if(inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {
                connection.disconnect();
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        // 本メソッドは UI スレッドで処理されるため、ビューを操作できます。
        if (result != null)
        {
            try {
                JSONObject json = new JSONObject(result);
                String Message;
                if (json.getString("KANRIKBN").equals("1")){
                    MsControl msc = new MsControl();
                    msc.Kanriid = MsControlHelper.getKanriid(mcontext);
                    msc.Kanrikbn = json.getString("KANRIKBN");;
                    msc.Tourokuid = json.getString("TOUROKUID");
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    msc.Updymd = df.format(date);
                    MsControlHelper.kanriupdate(mcontext, msc);
                    Message = "ユーザ登録が完了しました。";
                    /*
                    Message = "UserID : " + json.getString("FURUNAME") +  "\r\n";
                    Message = Message + "\r\n";
                    Message = Message + "eMail : " + json.getString("EMAIL") +  "\r\n";
                    Message = Message + "\r\n";
                    Message = Message + "LicenseNO : " + json.getString("TOUROKUID") +  "\r\n";
                    Message = Message + "\r\n";
                    Message = Message + "登録が終了しました。" +  "\r\n";
                    Message = Message + "\r\n";
                    Message = Message + "アプリを終了させて再起動させてください。" +  "\r\n";
                    */
                }
                else{
                    Message = "ユーザ登録に失敗しました。Googleアカウントを確認してください。";
                    /*
                    Message = "<<重要>>　このIDでの登録はできません。" +  "\r\n";
                    Message = Message + "\r\n";
                    Message = Message + "Googleにお問い合わせください。" +  "\r\n";
                    Message = Message + "\r\n";
                    Message = Message + "また、アプリを削除して再インストールをお願いします。" +  "\r\n";
                    Message = Message + "\r\n";
                    Message = Message + "" +  "\r\n";
                    Message = Message + "\r\n";
                    Message = Message + "アプリを停止してください。" +  "\r\n";
                    */
                }
                txResult.setText(Message);
                //txResult.setText("登録が終了しました。" );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, result);
    }

}