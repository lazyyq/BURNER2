package kyklab.burner2.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import kyklab.burner2.R
import kyklab.burner2.picture.PictureManager
import kyklab.burner2.utils.PrefManager

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var toolbarBackground: ImageView? = null
    private var mNeedsRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        setSupportActionBar(toolbar)

        toolbarBackground = findViewById(R.id.settings_toolbar_background_image)
        loadPicture()

        PrefManager.registerPrefChangeListener(this)
    }

    override fun onResume() {
        super.onResume()

        if (mNeedsRefresh) {
            loadPicture()
            mNeedsRefresh = false
        }
    }

    override fun onDestroy() {
        PrefManager.unregisterPrefChangeListener(this)
        super.onDestroy()
    }

    private fun loadPicture() {
        val pictureItem = PictureManager.instance.pictureList
                .get(PrefManager.instance.selectedPictureIndex)
        val picture = pictureItem.picture
        val key = ObjectKey(pictureItem.getVersionMetadata())
        Glide.with(this)
                .load(picture)
                .centerCrop()
                .signature(key)
                .into(toolbarBackground!!)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        when (s) {
            PrefManager.Key.SELECTED_PICTURE_INDEX -> mNeedsRefresh = true
        }
    }
}
