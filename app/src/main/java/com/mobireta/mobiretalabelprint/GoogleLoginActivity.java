package com.mobireta.mobiretalabelprint;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GoogleLoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private MsControl mscontrol;
    private MsUserHelper msuserhelper;
    private MsControlHelper mscontrolhelper;
    private static final String TAG = "GoogleLoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    TextView txResult;
    String hppdresult = "";
    String kanriid;
    String torokuid;
    public String tourokuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlelogin);
        txResult = (TextView) findViewById(R.id.txResult);
        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        mscontrol = MsControlHelper.finddata(this);
        kanriid = mscontrol.Kanriid;
        tourokuid = mscontrol.Tourokuid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(@ Nullable GoogleSignInAccount account) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            MsUser msu = new MsUser();
            msu.UserID = account.getId();
            msu.Idtoken = account.getIdToken();
            msu.Funame = account.getDisplayName();
            msu.Namae = account.getFamilyName();
            msu.Shimei = account.getGivenName();
            msu.Email = account.getEmail();
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            msu.Addymd = df.format(date);
            msuserhelper.insert(getApplicationContext(),msu);
            String idtoken = acct.getIdToken();
            String postdata = "";
            try {
                org.json.JSONObject json = new org.json.JSONObject();
                json.put("IDTOKEN", idtoken);
                //モバリテ情報
                json.put("MOBIRETAPMN", "MobiretaLabelPrint");
                json.put("FULLNAME", acct.getDisplayName());
                json.put("MAILADDRESS", acct.getEmail());
                json.put("TENPOCD", mscontrol.Tenpocd.toString());
                json.put("MOBILECD", mscontrol.Mobilecd.toString());
                json.put("KAISHAKJ", mscontrol.Kaishakj.toString());
                json.put("ZIPCD", mscontrol.Zipcd.toString());
                json.put("ADDRESS1", mscontrol.Address1.toString());
                json.put("ADDRESS2", mscontrol.Address2.toString());
                json.put("TEL", mscontrol.Tel.toString());
                json.put("FAX", mscontrol.Fax.toString());
                //デバイス情報
                json.put("PROGRAMNM", BuildConfig.VERSION_NAME);
                json.put("PROGRAMVER", String.valueOf(BuildConfig.VERSION_CODE));
                json.put("BRAND", Build.BRAND);
                json.put("DEVICE", Build.DISPLAY);
                json.put("HARDWARE", Build.HARDWARE);
                json.put("ID", Build.ID);
                json.put("MANUFACTURER", Build.MANUFACTURER);
                json.put("MODEL", Build.MODEL);
                json.put("PRODUCT", Build.PRODUCT);
                json.put("TIME", Build.TIME);
                json.put("TYPE", Build.TYPE);
                org.json.JSONArray  jary = new org.json.JSONArray();
                jary.put(json);
                postdata = jary.toString();
                //配列ではないので、Arrayを使わない
                postdata = json.toString();
            }
            catch (org.json.JSONException e) {
                Log.w(TAG, "failed code=" + e.getMessage());
                //return;
            }
            String urladd = getString(R.string.server_address) + getString(R.string.google_oauth);
            HttpAsyncTask task = new HttpAsyncTask(this);
            task.execute(getString(R.string.basic_id), getString(R.string.basic_pw), urladd , postdata);// POST の場合
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //==== キーコード判定 ====//
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //-==- Backキー -==-//
            // 以降の処理をキャンセルする。
            Intent intent = new Intent(GoogleLoginActivity.this, MainMenuActivity.class);
            setResult(RESULT_OK, intent);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
