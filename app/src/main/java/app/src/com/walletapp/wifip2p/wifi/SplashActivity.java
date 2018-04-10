package app.src.com.walletapp.wifip2p.wifi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.src.com.walletapp.R;
import app.src.com.walletapp.model.OfflineEvent;
import app.src.com.walletapp.wifip2p.utils.SharedPreferencesHandler;

import static app.src.com.walletapp.wifip2p.wifi.WiFiDirectActivity.TAG;

/**
 * Created by SONY on 3/23/2018.
 */

public class SplashActivity extends AppCompatActivity {
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SharedPreferencesHandler.getStringValues(SplashActivity.this,"loginId")!=null && SharedPreferencesHandler.getStringValues(SplashActivity.this,"user").equalsIgnoreCase("1")){
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this,MainNewActivity.class));
                    finish();
                }
            }
        },1000);
    }

    @Subscribe
    public void onOfflineEvent(OfflineEvent event) {
        Log.i(TAG, "onOfflineEvent: ");
    }
}
