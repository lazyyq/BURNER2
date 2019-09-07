package kyklab.burner2.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import kyklab.burner2.App;

@SuppressWarnings({"WeakerAccess", "unused"})
public class PrefManager {
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
        return pref.getInt(Key.SELECTED_PICTURE_INDEX, 0);
    }

    public void setSelectedPictureIndex(int i) {
        editor.putInt(Key.SELECTED_PICTURE_INDEX, i).apply();
    }

    public String getRotateAngle() {
        return pref.getString(Key.ROTATE_ANGLE, "0");
    }

    public void setRotateAngle(String s) {
        editor.putString(Key.ROTATE_ANGLE, s).apply();
    }

    public String getScaleType() {
        return pref.getString(Key.SCALE_TYPE, ImageScaleType.DEFAULT);
    }

    public void setScaleType(String s) {
        editor.putString(Key.SCALE_TYPE, s).apply();
    }

    public int getBatteryLimit() {
        return pref.getInt(Key.BATTERY_LIMIT, 15);
    }

    public void setBatteryLimit(int i) {
        editor.putInt(Key.BATTERY_LIMIT, i).apply();
    }

    public boolean getBatteryLimitEnabled() {
        return pref.getBoolean(Key.BATTERY_LIMIT_ENABLED, false);
    }

    public void setBatteryLimitEnabled(boolean b) {
        editor.putBoolean(Key.BATTERY_LIMIT_ENABLED, b).apply();
    }

    public boolean getKeepScreenOn() {
        return pref.getBoolean(Key.KEEP_SCREEN_ON, true);
    }

    public void setKeepScreenOn(boolean b) {
        editor.putBoolean(Key.KEEP_SCREEN_ON, b).apply();
    }

    public boolean getMaxBrightness() {
        return pref.getBoolean(Key.MAX_BRIGHTNESS, false);
    }

    public void setMaxBrightness(boolean b) {
        editor.putBoolean(Key.MAX_BRIGHTNESS, b).apply();
    }

    public long getPicLastUpdatedTime() {
        return pref.getLong(Key.PIC_LAST_UPDATED_TIME, -1);
    }

    public void setPicLastUpdatedTime(long l) {
        editor.putLong(Key.PIC_LAST_UPDATED_TIME, l).apply();
    }

    public void removePref(String pref) {
        editor.remove(pref).apply();
    }

    private static class LazyHolder {
        static final PrefManager INSTANCE = new PrefManager();
    }

    public class Key {
        public static final String SELECT_PICTURE = "select_picture";
        public static final String SELECTED_PICTURE_INDEX = "selected_picture_index";
        public static final String ROTATE_ANGLE = "rotation";
        public static final String SCALE_TYPE = "scale_type";
        public static final String BATTERY_LIMIT = "battery_limit";
        public static final String BATTERY_LIMIT_ENABLED = "battery_limit_enabled";
        public static final String KEEP_SCREEN_ON = "keep_screen_on";
        public static final String MAX_BRIGHTNESS = "max_brightness";
        public static final String PIC_LAST_UPDATED_TIME = "pic_last_updated_time";
        public static final String CLEAR_IMAGE_CACHE = "clear_image_cache";
    }

    public class ImageScaleType {
        public static final String DEFAULT = "Default";
        public static final String CENTER = "Center";
        public static final String CENTER_CROP = "CenterCrop";
        public static final String FIT_XY = "FitXY";
    }
}
