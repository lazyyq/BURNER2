package kyklab.burner2.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

import kyklab.burner2.R
import kyklab.burner2.picture.PictureManager
import kyklab.burner2.selectpicture.SelectPictureActivity
import kyklab.burner2.utils.PrefManager

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private var selectPicture: Preference? = null
    private var clearImageCache: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        if (activity == null) {
            return
        }

        setPreferencesFromResource(R.xml.preference, rootKey)

        selectPicture = findPreference(PrefManager.Key.SELECT_PICTURE)
        if (selectPicture != null) {
            selectPicture!!.onPreferenceClickListener = this
        }
        clearImageCache = findPreference(PrefManager.Key.CLEAR_IMAGE_CACHE)
        if (clearImageCache != null) {
            clearImageCache!!.onPreferenceClickListener = this
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {

    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        if (preference === selectPicture) {
            val intent = Intent(context, SelectPictureActivity::class.java)
            startActivity(intent)
        } else if (preference === clearImageCache) {
            PictureManager.clearImageCache()
            PictureManager.forceImageReload()
        }
        return false
    }
}
