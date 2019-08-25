package kyklab.burner2.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import kyklab.burner2.App;

public class PrefManager {
    public static final String KEY_SELECT_PICTURE = "select_picture";
    public static final String KEY_SELECTED_PICTURE = "selected_picture";
    public static final String KEY_USE_CUSTOM_PICTURE = "use_custom_picture";
    public static final String KEY_ROTATE_ANGLE = "rotation";
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    private PrefManager() {
        pref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        editor = pref.edit();
        editor.apply();
    }

    public static PrefManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static void registerPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        pref.registerOnSharedPreferenceChangeListener(listener);
    }

    public int getSelectedPicture() {
        return pref.getInt(KEY_SELECTED_PICTURE, 0);
    }

    public void setSelectedPicture(int i) {
        editor.putInt(KEY_SELECTED_PICTURE, i).apply();
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
        editor.putString(KEY_ROTATE_ANGLE, s);
    }

    private static class LazyHolder {
        public static final PrefManager INSTANCE = new PrefManager();
    }
}
