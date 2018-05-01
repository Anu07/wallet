package app.src.com.walletapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

/**
 * Created by Anu Bhalla on 25/02/18.
 */

public class MyPreferences {
    private static MyPreferences sharePref = new MyPreferences();
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private MyPreferences() {
    } //prevent creating multiple instances by making the constructor private

    public enum Keys {
        USERID, DEVICEQR, LOGINSTATUS, Country, Language, Currency, DeviceToken,
        PhoneNumber, USERNAME, TOKEN, PINSET, PIN, TXNS, LANGUAGESELECT, QrImage, LOGINRESPONSE;
    }

    //The context passed into the getInstance should be application level context.
    public static MyPreferences getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return sharePref;
    }

    public void savePlaceObj(Keys key,String str) {
        editor.putString(key.name(), str);
        editor.commit();
    }

    public String getPlaceObj(Keys key) {
        return sharedPreferences.getString(key.name(), "");
    }

    public void removePlaceObj(Keys key) {
        editor.remove(key.name());
        editor.commit();
    }

    public void clearAll() {
        editor.clear();
        editor.commit();
    }

}