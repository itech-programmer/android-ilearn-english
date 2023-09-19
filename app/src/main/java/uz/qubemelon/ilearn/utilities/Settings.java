package uz.qubemelon.ilearn.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

	public static int getIntValue(Context context, String name, int defaultValue){
		return preferences(context).getInt(name, defaultValue);
	}

	public static String getStringValue(Context context, String name, String defaultValue){
		return preferences(context).getString(name, defaultValue);
	}

	public static boolean getBooleanValue(Context context, String name, boolean defaultValue){
		return preferences(context).getBoolean(name, defaultValue);
	}

	public static void saveStringValue(Context context, String name, String value){
		SharedPreferences.Editor editor = preferences(context).edit();
		editor.putString(name, value);
		editor.apply();
	}

	public static void saveBooleanValue(Context context, String name, boolean value){
		SharedPreferences.Editor editor = preferences(context).edit();
		editor.putBoolean(name, value);
		editor.apply();
	}

	private static SharedPreferences preferences(Context context){
		
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
