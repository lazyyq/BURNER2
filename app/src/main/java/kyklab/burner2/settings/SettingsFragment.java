package kyklab.burner2.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import kyklab.burner2.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
    }
}
