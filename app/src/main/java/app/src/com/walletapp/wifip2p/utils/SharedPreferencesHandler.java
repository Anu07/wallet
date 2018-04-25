package app.src.com.walletapp.wifip2p.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class SharedPreferencesHandler {

	public static SharedPreferences getSharedPreferences(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	public static void setStringValues(Context ctx, String key,
			String DataToSave) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putString(key, DataToSave);
		editor.commit();
	}

	public static String getStringValues(Context ctx, String key) {
		return getSharedPreferences(ctx).getString(key, null);
	}

	public static void setFloatValues(Context ctx, String key, float DataToSave) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putFloat(key, DataToSave);
		editor.commit();
	}

	public static float getFloatValues(Context ctx, String key) {
		return getSharedPreferences(ctx).getFloat(key, 0);
	}
	
	public static void setBooleanValues(Context ctx, String key, Boolean DataToSave) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putBoolean(key, DataToSave);
		editor.commit();
	}

	public static boolean getBooleanValues(Context ctx, String key) {
		return getSharedPreferences(ctx).getBoolean(key, false);
	}
	
	public static long getLongValues(Context ctx, String key) {
		return getSharedPreferences(ctx).getLong(key, 0L);
	}
	
	public static void setLongValues(Context ctx, String key, Long DataToSave) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putLong(key, DataToSave);
		editor.commit();
	}

	public static void setImage(Context ctx, String key, int DataToSave) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putInt(key, DataToSave);
		editor.commit();
	}

	public static int getImage(Context ctx, String key) {
		return getSharedPreferences(ctx).getInt(key, 0);
	}

	public static void clearPRefs(Context ctx){
		Editor editor = getSharedPreferences(ctx).edit();
		editor.clear();
		editor.commit();
	}

}