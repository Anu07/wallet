package app.src.com.walletapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Anu Bhalla on 25/02/18.
 */

public class MyPreferences {
    private static MyPreferences mInstance;
    private Context mContext;
    public static String LOGINSTATUS="loginStatus";
    //
    private SharedPreferences mMyPreferences;

    private MyPreferences(){ }

    public enum Keys{
        USERID,LOGINSTATUS, Country, Language, Currency, DeviceToken, PhoneNumber, USERNAME, TOKEN, PINSET, PIN, TXNS, LANGUAGESELECT, LOGINRESPONSE
    }

    public static MyPreferences getInstance(){
        if (mInstance == null) mInstance = new MyPreferences();
        return mInstance;
    }

    public void Initialize(Context ctxt){
        mContext = ctxt;
        //
        mMyPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    // Within Singleton class

    public void writePreference(Keys key, String value){
        SharedPreferences.Editor e = mMyPreferences.edit();
        e.putString(key.name(), value);
        e.commit();
    }

    // Within Singleton class

    public String getPreference(Keys key){
        return mMyPreferences.getString(key.name(),"");
    }
}
