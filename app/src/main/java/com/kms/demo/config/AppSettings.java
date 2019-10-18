package com.kms.demo.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.kms.demo.app.Constants;

/**
 * @author ziv
 */
public class AppSettings {

    private static final String PREFERENCES_NAME = "com.juzix.wallet.appsettings";

    private static final AppSettings APP_SETTINGS = new AppSettings();

    /**
     * 文件存储
     */
    private SharedPreferences preferences;

    private AppSettings() {

    }

    public static AppSettings getInstance() {
        return APP_SETTINGS;
    }

    public void init(Context ctx) {
        preferences = ctx
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void clear() {
        preferences.edit().clear().commit();
    }

    private String getStringItem(String key, String defaultvalue) {
        return preferences.getString(key, defaultvalue);
    }

    private void setStringItem(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    private int getIntItem(String key, int defaultvalue) {
        return preferences.getInt(key, defaultvalue);
    }

    private void setIntItem(String key, int value) {
        preferences.edit().putInt(key, value).commit();
    }

    private long getLongItem(String key, long defaultvalue) {
        return preferences.getLong(key, defaultvalue);
    }

    private void setLongItem(String key, long value) {
        preferences.edit().putLong(key, value).commit();
    }

    private boolean getBooleanItem(String key, boolean defaultvalue) {
        return preferences.getBoolean(key, defaultvalue);
    }

    private void setBooleanItem(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    public void setLanguage(String language) {
        setStringItem(Constants.Preference.KEY_LANGUAGE, language);
    }

    public String getLanguage() {
        return getStringItem(Constants.Preference.KEY_LANGUAGE, null);
    }

    public void setDeviceId(String deviceId) {
        setStringItem(Constants.Preference.KEY_DEVICE_ID, deviceId);
    }

    public String getDeviceId() {
        return getStringItem(Constants.Preference.KEY_DEVICE_ID, null);
    }

    public void setFirstEnter(boolean firstEnter) {
        setBooleanItem(Constants.Preference.KEY_FIRST_ENTER, firstEnter);
    }

    public boolean getFirstEnter() {
        return getBooleanItem(Constants.Preference.KEY_FIRST_ENTER, true);
    }

}
