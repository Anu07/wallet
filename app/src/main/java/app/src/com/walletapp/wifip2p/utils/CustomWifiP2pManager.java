package app.src.com.walletapp.wifip2p.utils;

/**
 * Created by Anu Bhalla on 2/21/18.
 */


import android.annotation.SuppressLint;
import android.net.nsd.NsdManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import java.lang.reflect.Method;

public class CustomWifiP2pManager {

    static Method turnOffICS;
    static Method turnOnICS;
    static Method turnOnJB;
    static String TAG=CustomWifiP2pManager.class.getName();


    @SuppressLint({"NewApi"})
    public static void turnOnP2P(Channel c, WifiP2pManager m) throws Exception {
        Log.d(TAG, "turning on Wifi Direct");
        if (android.os.Build.VERSION.SDK_INT == 14 || android.os.Build.VERSION.SDK_INT == 15) {
            Log.d(TAG, "Version is ICS");
            try {
                turnOnICS = WifiP2pManager.class.getDeclaredMethod("enableP2p",Channel.class);
                turnOnICS.setAccessible(true);
                turnOnICS.invoke(m, c);
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "ICS enableP2p() not found");
            } catch (Exception e) {
                Log.d(TAG, "turnOnICS invocation failure");
            }
        } else if (android.os.Build.VERSION.SDK_INT >= 16) {
            Log.d(TAG, "Version is JB and higher");
            try {
                turnOnJB = NsdManager.class.getDeclaredMethod("setEnabled", boolean.class);
                turnOnJB.setAccessible(true);
                turnOnJB.invoke(NsdManager.class, true);
                //must feed it an nsdmanager, but none exists in wifidirectactivity
                Log.d(TAG, "problem");
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "JB setEnabled() not found");
            } catch (Exception e) {
                Log.d(TAG, "turnOnJB invocation failure");
                e.printStackTrace();
            }
        }
    }

    public static void turnOffP2P(Channel c, WifiP2pManager m) {
        Log.d(TAG, "turning Off Wifi Direct");
        if (android.os.Build.VERSION.SDK_INT == 14 || android.os.Build.VERSION.SDK_INT == 15) {
            Log.d(TAG, "Version is ICS");
            try {
                turnOffICS = WifiP2pManager.class.getDeclaredMethod("disableP2p", Channel.class);
                turnOffICS.setAccessible(true);
                turnOffICS.invoke(m, c);
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "ICS disableP2P() not found");
            } catch (Exception e) {
                Log.d(TAG, "turnOffICS invocation failure");
            }
        }
    }
}
