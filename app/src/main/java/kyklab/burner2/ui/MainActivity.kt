package kyklab.burner2.ui

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.leinardi.android.speeddial.FabWithLabelView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import kyklab.burner2.R
import kyklab.burner2.batterylimit.BatteryLimiter
import kyklab.burner2.picture.PictureManager
import kyklab.burner2.settings.SettingsActivity
import kyklab.burner2.utils.PrefManager
import kyklab.burner2.utils.ScreenUtils

class MainActivity : AppCompatActivity(), View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private val mHandler = Handler()
    private val mMaxBrightnessRunnable = Runnable {
        if (PrefManager.instance.maxBrightness) {
            ScreenUtils.setMaxBrightness(this@MainActivity)
        }
    }
    private var mRotateLayout: RotateLayout? = null
    private var mImageView: ImageView? = null
    private var mFab: SpeedDialView? = null
    private var mFullscreen = false
    private val mHideUiRunnable = Runnable { toggleFullscreen(true) }
    private var mNeedsRefresh = false
    private var batteryLimiter: BatteryLimiter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mImageView = findViewById(R.id.mainImage)
        mImageView!!.setOnClickListener(this)
        mRotateLayout = findViewById(R.id.rotateLayout)
        setupFab()
        PictureManager.instance.updatePictureList()
        setupImageView()
        loadPicture()

        batteryLimiter = BatteryLimiter(this, findViewById(R.id.coordinatorLayout))
        PrefManager.registerPrefChangeListener(this)
    }

    override fun onResume() {
        super.onResume()

        if (mNeedsRefresh) {
            setupImageView()
            loadPicture()
            mNeedsRefresh = false
        }

        if (!mFullscreen) {
            delayedHideUi()
            delayedMaxBrightness()
        }

        if (PrefManager.instance.keepScreenOn) {
            ScreenUtils.setKeepScreenOn(this)
        }

        if (PrefManager.instance.batteryLimitEnabled) {
            batteryLimiter!!.start()
        }
    }

    override fun onPause() {
        super.onPause()

        batteryLimiter!!.stop()
        ScreenUtils.unsetKeepScreenOn(this)
        ScreenUtils.resetBrightness(this)
    }

    override fun onDestroy() {
        PrefManager.unregisterPrefChangeListener(this)

        super.onDestroy()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mainImage -> {
                toggleFullscreen()
                toggleBrightness()
                if (!mFullscreen) {
                    delayedHideUi()
                    delayedMaxBrightness()
                }
            }
        }
    }

    private fun setupImageView() {
        val rotateAngle = Integer.parseInt(PrefManager.instance.rotateAngle)
        mRotateLayout!!.angle = rotateAngle
        val scaleType: ImageView.ScaleType
        when (PrefManager.instance.scaleType) {
            PrefManager.ImageScaleType.DEFAULT -> scaleType = ImageView.ScaleType.FIT_CENTER
            PrefManager.ImageScaleType.CENTER -> scaleType = ImageView.ScaleType.CENTER
            PrefManager.ImageScaleType.CENTER_CROP -> scaleType = ImageView.ScaleType.CENTER_CROP
            PrefManager.ImageScaleType.FIT_XY -> scaleType = ImageView.ScaleType.FIT_XY
            else -> scaleType = ImageView.ScaleType.FIT_CENTER
        }
        mImageView!!.scaleType = scaleType
    }

    private fun loadPicture() {
        val pictureItem = PictureManager.instance.pictureList
                .get(PrefManager.instance.selectedPictureIndex)
        val picture = pictureItem.picture
        val key = ObjectKey(pictureItem.getVersionMetadata())
        Glide.with(this)
                .load(picture)
                .signature(key)
                .into(mImageView!!)
    }

    private fun setupFab() {
        mFab = findViewById(R.id.fab)
        mFab!!.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                return false
            }

            override fun onToggleChanged(isOpen: Boolean) {
                // Reset hide delay on every click
                delayedHideUi()
                delayedMaxBrightness()
            }
        })

        val fabWithLabelView = arrayOfNulls<FabWithLabelView>(2)

        fabWithLabelView[0] = mFab!!.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_settings, AppCompatResources.getDrawable(this,
                        R.drawable.ic_settings_white_24dp))
                        .setFabBackgroundColor(Color.WHITE)
                        .setFabImageTintColor(Color.BLACK)
                        .setLabel("Settings")
                        .create())
        if (fabWithLabelView[0] != null) {
            fabWithLabelView[0].setSpeedDialActionItem(
                    fabWithLabelView[0].getSpeedDialActionItemBuilder().create())
        }

        fabWithLabelView[1] = mFab!!.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_temp, AppCompatResources.getDrawable(this,
                        R.drawable.ic_settings_white_24dp))
                        .setFabBackgroundColor(Color.WHITE)
                        .setFabImageTintColor(Color.BLACK)
                        .setLabel("temp")
                        .create())
        if (fabWithLabelView[1] != null) {
            fabWithLabelView[1].setSpeedDialActionItem(
                    fabWithLabelView[1].getSpeedDialActionItemBuilder().create())
        }

        mFab!!.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_settings -> {
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(intent)
                }
                R.id.fab_temp -> Toast.makeText(this@MainActivity, "fab_temp", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun toggleFullscreen(b: Boolean) {
        mFullscreen = !b
        toggleFullscreen()
    }

    private fun toggleFullscreen() {
        if (!mFullscreen) {
            hideUi()
        } else {
            showUi()
        }
    }

    private fun toggleBrightness() {
        if (mFullscreen) {
            if (PrefManager.instance.maxBrightness) {
                ScreenUtils.setMaxBrightness(this)
            }
        } else {
            ScreenUtils.resetBrightness(this)
        }
    }

    private fun delayedMaxBrightness() {
        mHandler.removeCallbacks(mMaxBrightnessRunnable)
        mHandler.postDelayed(mMaxBrightnessRunnable, HIDE_UI_DELAY.toLong())
    }

    private fun hideUi() {
        ScreenUtils.hideSystemUi(this)
        mFab!!.hide()

        mFullscreen = true
    }

    private fun showUi() {
        ScreenUtils.showSystemUi(this)
        mFab!!.show()

        mFullscreen = false
    }

    private fun delayedHideUi() {
        mHandler.removeCallbacks(mHideUiRunnable)
        mHandler.postDelayed(mHideUiRunnable, HIDE_UI_DELAY.toLong())
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        when (s) {
            PrefManager.Key.MAX_BRIGHTNESS -> if (mFullscreen) {
                if (PrefManager.instance.maxBrightness) {
                    ScreenUtils.setMaxBrightness(this)
                } else {
                    ScreenUtils.resetBrightness(this)
                }
            }
            PrefManager.Key.SELECTED_PICTURE_INDEX, PrefManager.Key.ROTATE_ANGLE, PrefManager.Key.SCALE_TYPE -> mNeedsRefresh = true
        }
    }

    companion object {
        private val HIDE_UI_DELAY = 3000
    }
}
