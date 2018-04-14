package app.src.com.walletapp.model;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by SONY on 4/13/2018.
 */

public class MyDeviceEvent {
    WifiP2pDevice mDevice;

    public MyDeviceEvent(WifiP2pDevice mDevice) {
        this.mDevice = mDevice;
    }

    public WifiP2pDevice getmDevice() {
        return mDevice;
    }

    public void setmDevice(WifiP2pDevice mDevice) {
        this.mDevice = mDevice;
    }
}
