package app.src.com.walletapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import app.src.com.walletapp.R;
import app.src.com.walletapp.view.activity.SplashActivity;
import app.src.com.walletapp.wifip2p.GlobalActivity;

/**
 * Created by Anu Bhalla on 25/02/18.
 */


public class CommonUtils {

    public static final String DEVICETYPE = "Android";
    public static final String OFFLINE = "1";
    public static final String ONLINE = "2";
    public static final String TRANSFERKEY = "credits";
    private static final String LOG = CommonUtils.class.getName();
    static Dialog verifydialog;
    private static final String TAG = CommonUtils.class.getName();
    private static BluetoothAdapter bluetoothAdapter = null;
    static WifiConfiguration wifiConfig;
    public static final String[] GetCountryZipCode(Context ctx) {

        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = ctx.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                return g;
            }
        }
        Log.i(TAG, "GetCountryZipCode: " + CountryZipCode);
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void displayCurrencyInfoForLocale(@Nullable Locale loc, Context ctt) {
        Locale locale;
        if (loc == null) {
            locale = new Locale.Builder().setRegion(GetCountryZipCode(ctt)[1]).build();
        } else {
            locale = loc;
        }
        System.out.println("Locale: " + locale.getDisplayName());
        Currency currency = Currency.getInstance(locale);
        System.out.println("Currency Code: " + currency.getCurrencyCode());
        System.out.println("Symbol: " + currency.getSymbol());
        MyPreferences.getInstance().Initialize(ctt);
        MyPreferences.getInstance().writePreference(MyPreferences.Keys.Country, locale.getDisplayName());
        MyPreferences.getInstance().writePreference(MyPreferences.Keys.Language, locale.getDisplayLanguage());
        MyPreferences.getInstance().writePreference(MyPreferences.Keys.Currency, currency.getCurrencyCode());
        Log.i(TAG, "displayCurrencyInfoForLocale:Language " + locale.getDisplayLanguage());
    }

    /**
     * get All languages
     */
    public static final ArrayList<String> getAllLanguages() {
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> localcountries = new ArrayList<String>();
        for (Locale l : locales) {
            localcountries.add(l.getDisplayLanguage().toString());
        }
        return localcountries;
    }

    /**
     * get Device unique Id
     */
    public static final String getUniqueDeviceId() {
        return Settings.Secure.getString(GlobalActivity.getGlobalContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }


    /**
     * @param context
     * @return
     */

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /**
     * network unavailable dialog
     *
     * @param context
     */

    public static void networkErrorDialog(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ERROR !!");
        builder.setMessage("Internet connection Unavailable!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        Toast.makeText(context, "Network Unavailable!", Toast.LENGTH_LONG).show();
    }

    public static void logoutApp(final Activity ctx) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getString(R.string.logout)).setMessage("Are you sure you want to logout?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MyPreferences.getInstance().writePreference(MyPreferences.Keys.USERID, "");
                Intent intent = new Intent(GlobalActivity.getGlobalContext(), SplashActivity.class);
                ctx.startActivity(intent);
                ctx.finish();
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {                        // do nothing
                dialog.dismiss();
            }
        })
                .show();
    }

    /**
     * To set device name
     */

    public static final void setDeviceName() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.i(LOG, "localdevicename : " + bluetoothAdapter.getName() + " localdeviceAddress : " + bluetoothAdapter.getAddress());
        bluetoothAdapter.setName("Payme");
        Log.i(LOG, "localdevicename changed: " + bluetoothAdapter.getName() + " localdeviceAddress : " + bluetoothAdapter.getAddress());
    }

    /**
     * Rename hotspot name
     * @param newName
     * @param context
     * @return
     */


    public static boolean setHotspotName(String newName, Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

            wifiConfig.SSID = "Payme";

            Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifiManager, wifiConfig);
            Log.i(TAG, "setHotspotName: "+ wifiConfig.SSID);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final String getHotspotName(){
        return wifiConfig.SSID;
    }


    /**
     * Send email
     */
    public static void sendEmail(String address,String mailmsg,Context ctx){

        if(!address.trim().equalsIgnoreCase("")){
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ address});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "PDF Receipt");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mailmsg);
            ctx.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
        else{
            Toast.makeText(ctx, "Please enter an email address..", Toast.LENGTH_LONG).show();
        }
    }

    public static final String getMacAddress(Context ctx){
        WifiManager manager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        return address;
    }

    /**
     * To check if databaase created successfully
     * @param context
     * @param dbName
     * @return
     */


    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
}