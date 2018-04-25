package app.src.com.walletapp.wifip2p.wifi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.src.com.walletapp.R;
import app.src.com.walletapp.view.activity.BaseActivity;

/**
 * Created by SONY on 4/12/2018.
 */

public class QRCodeActivity extends BaseActivity{


    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        getSupportFragmentManager().beginTransaction().add(R.id.qr_container,new BarcodeFragment()).commit();
    }
}
