package kyklab.burner2.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import kyklab.burner2.R;
import kyklab.burner2.settings.selectpicture.SelectPictureActivity;
import kyklab.burner2.utils.PrefManager;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private Preference selectPicture;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);

        selectPicture = findPreference(PrefManager.KEY_SELECT_PICTURE);
        selectPicture.setOnPreferenceClickListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == selectPicture) {
            Intent intent = new Intent(getContext(), SelectPictureActivity.class);
            startActivity(intent);
        }
        return false;
    }
}
