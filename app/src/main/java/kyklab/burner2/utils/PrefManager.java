package kyklab.burner2.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import kyklab.burner2.App;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private PrefManager() {
        pref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        editor = pref.edit();
        editor.apply();
    }

    public static PrefManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        public static final PrefManager INSTANCE = new PrefManager();
    }
}
