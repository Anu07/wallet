package app.src.com.walletapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


import app.src.com.walletapp.R;
import app.src.com.walletapp.wifip2p.wifi.WiFiDirectActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
             /*   if(MyPreferences.getInstance().getPreference(MyPreferences.Keys.USERID).equalsIgnoreCase("")){
                    startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                    finish();
                }else{*/
                    startActivity(new Intent(SplashActivity.this, WiFiDirectActivity.class));
                    finish();
//                }
            }
        },1500);
    }

}
