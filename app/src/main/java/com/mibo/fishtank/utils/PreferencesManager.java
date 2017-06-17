package com.mibo.fishtank.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Administrator
 * on 2017/5/7 0007.
 */
public class PreferencesManager {

    private static final String PREF_NAME = "com.mibo.fishtank";
    public static final String IS_LOGIN = "is_login";

    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
        return sInstance;
    }

    public void setStringValue(String key, String value) {
        mPref.edit()
                .putString(key, value)
                .apply();
    }

    public String getStringValue(String key) {
        return mPref.getString(key, "");
    }
    public String getStringValue(String key,String defaultValue) {
        return mPref.getString(key, defaultValue);
    }

    public boolean getBooleanValue(String key) {
        return mPref.getBoolean(key, false);
    }

    public void setBooleanValue(String key, boolean value) {
        mPref.edit()
                .putBoolean(key, value)
                .apply();
    }

    public Set<String> getSetValue(String key) {
        return mPref.getStringSet(key, null);
    }

    public void setSetValue(String key, Set<String> set) {
        mPref.edit()
                .putStringSet(key, set)
                .apply();
    }

    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .apply();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }
}
