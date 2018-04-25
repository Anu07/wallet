package app.src.com.walletapp.wifip2p.wifi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.OfflineEvent;
import app.src.com.walletapp.model.UserDataEvent;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.src.com.walletapp.wifip2p.wifi.TransferActivity.TAG;

/**
 * Created by SONY on 3/22/2018.
 */

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.fullname)
    EditText fullname;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.ccp)
    CountryCodePicker ccp;

    private String countryCode;

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        countryCode = ccp.getSelectedCountryCodeWithPlus();
        Log.i(TAG, "onCreate: " + countryCode);
    }

    @OnClick(R.id.submit_bttn)
    public void onViewClicked() {
        if (validate()) {
            EventBus.getDefault().postSticky(new UserDataEvent(fullname.getText().toString().trim(), ccp.getSelectedCountryCodeWithPlus() + phone.getText().toString().trim(), email.getText().toString().trim(), password.getText().toString()));
            startActivity(new Intent(RegisterActivity.this, VerifyOTPActivity.class));
            finish();
        }
    }

    @Subscribe
    public void onOfflineEvent(OfflineEvent event) {
        Log.i(TAG, "onOfflineEvent: ");
    }

    private boolean validate() {
        if (fullname.getText().toString().isEmpty()) {
            Toast.makeText(this, "Fullname can't be left empty.", Toast.LENGTH_LONG).show();
            return false;
        } else if (phone.getText().toString().isEmpty()) {
            Toast.makeText(this, "Phone can't be left empty.", Toast.LENGTH_LONG).show();
            return false;
        } else if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, "Email can't be left empty.", Toast.LENGTH_LONG).show();
            return false;
        } else if (!isValidEmail(email.getText().toString())) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, "Password can't be left empty.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


}
