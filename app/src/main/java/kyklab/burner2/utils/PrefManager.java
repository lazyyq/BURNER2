package kyklab.burner2.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import kyklab.burner2.App;

@SuppressWarnings({"WeakerAccess", "unused"})
public class PrefManager {
    public static final String KEY_SELECT_PICTURE = "select_picture";
    public static final String KEY_SELECTED_PICTURE_INDEX = "selected_picture_index";
    public static final String KEY_USE_CUSTOM_PICTURE = "use_custom_picture";
    public static final String KEY_ROTATE_ANGLE = "rotation";
    public static final String KEY_BATTERY_LIMIT = "battery_limit";
    public static final String KEY_BATTERY_LIMIT_ENABLED = "battery_limit_enabled";
    public static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
    public static final String KEY_MAX_BRIGHTNESS = "max_brightness";
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    private PrefManager() {
        pref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        editor = pref.edit();
        editor.apply();
    }

    @SuppressWarnings("SameReturnValue")
    public static PrefManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static void registerPrefChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterPrefChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .unregisterOnSharedPreferenceChangeListener(listener);
    }

    public int getSelectedPictureIndex() {
        return pref.getInt(KEY_SELECTED_PICTURE_INDEX, 0);
    }

    public void setSelectedPictureIndex(int i) {
        editor.putInt(KEY_SELECTED_PICTURE_INDEX, i).apply();
    }

    public boolean getUseCustomPicture() {
        return pref.getBoolean(KEY_USE_CUSTOM_PICTURE, false);
    }

    public void setUseCustomPicture(boolean b) {
        editor.putBoolean(KEY_USE_CUSTOM_PICTURE, b).apply();
    }

    public String getRotateAngle() {
        return pref.getString(KEY_ROTATE_ANGLE, "0");
    }

    public void setRotateAngle(String s) {
        editor.putString(KEY_ROTATE_ANGLE, s).apply();
    }

    public int getBatteryLimit() {
        return pref.getInt(KEY_BATTERY_LIMIT, 15);
    }

    public void setBatteryLimit(int i) {
        editor.putInt(KEY_BATTERY_LIMIT, i).apply();
    }

    public boolean getBatteryLimitEnabled() {
        return pref.getBoolean(KEY_BATTERY_LIMIT_ENABLED, false);
    }

    public void setBatteryLimitEnabled(boolean b) {
        editor.putBoolean(KEY_BATTERY_LIMIT_ENABLED, b).apply();
    }

    public boolean getKeepScreenOn() {
        return pref.getBoolean(KEY_KEEP_SCREEN_ON, true);
    }

    public void setKeepScreenOn(boolean b) {
        editor.putBoolean(KEY_KEEP_SCREEN_ON, b).apply();
    }

    public boolean getMaxBrightness() {
        return pref.getBoolean(KEY_MAX_BRIGHTNESS, false);
    }

    public void setMaxBrightness(boolean b) {
        editor.putBoolean(KEY_MAX_BRIGHTNESS, b).apply();
    }

    private static class LazyHolder {
        static final PrefManager INSTANCE = new PrefManager();
    }
}
