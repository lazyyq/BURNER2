package kyklab.burner2.utils

import android.content.SharedPreferences

import androidx.preference.PreferenceManager

import kyklab.burner2.App

class PrefManager private constructor() {

    var selectedPictureIndex: Int
        get() = pref.getInt(Key.SELECTED_PICTURE_INDEX, 0)
        set(i) = editor.putInt(Key.SELECTED_PICTURE_INDEX, i).apply()

    var rotateAngle: String
        get() = pref.getString(Key.ROTATE_ANGLE, "0")
        set(s) = editor.putString(Key.ROTATE_ANGLE, s).apply()

    var scaleType: String
        get() = pref.getString(Key.SCALE_TYPE, ImageScaleType.DEFAULT)
        set(s) = editor.putString(Key.SCALE_TYPE, s).apply()

    var batteryLimit: Int
        get() = pref.getInt(Key.BATTERY_LIMIT, 15)
        set(i) = editor.putInt(Key.BATTERY_LIMIT, i).apply()

    var batteryLimitEnabled: Boolean
        get() = pref.getBoolean(Key.BATTERY_LIMIT_ENABLED, false)
        set(b) = editor.putBoolean(Key.BATTERY_LIMIT_ENABLED, b).apply()

    var keepScreenOn: Boolean
        get() = pref.getBoolean(Key.KEEP_SCREEN_ON, true)
        set(b) = editor.putBoolean(Key.KEEP_SCREEN_ON, b).apply()

    var maxBrightness: Boolean
        get() = pref.getBoolean(Key.MAX_BRIGHTNESS, false)
        set(b) = editor.putBoolean(Key.MAX_BRIGHTNESS, b).apply()

    var picLastUpdatedTime: Long
        get() = pref.getLong(Key.PIC_LAST_UPDATED_TIME, -1)
        set(l) = editor.putLong(Key.PIC_LAST_UPDATED_TIME, l).apply()

    init {
        pref = PreferenceManager.getDefaultSharedPreferences(App.context)
        editor = pref.edit()
        editor.apply()
    }

    fun removePref(pref: String) {
        editor.remove(pref).apply()
    }

    private object LazyHolder {
        internal val INSTANCE = PrefManager()
    }

    object Key {
        val SELECT_PICTURE = "select_picture"
        val SELECTED_PICTURE_INDEX = "selected_picture_index"
        val ROTATE_ANGLE = "rotation"
        val SCALE_TYPE = "scale_type"
        val BATTERY_LIMIT = "battery_limit"
        val BATTERY_LIMIT_ENABLED = "battery_limit_enabled"
        val KEEP_SCREEN_ON = "keep_screen_on"
        val MAX_BRIGHTNESS = "max_brightness"
        val PIC_LAST_UPDATED_TIME = "pic_last_updated_time"
        val CLEAR_IMAGE_CACHE = "clear_image_cache"
    }

    object ImageScaleType {
        val DEFAULT = "Default"
        val CENTER = "Center"
        val CENTER_CROP = "CenterCrop"
        val FIT_XY = "FitXY"
    }

    companion object {
        private var pref: SharedPreferences
        private var editor: SharedPreferences.Editor

        val instance: PrefManager
            get() = LazyHolder.INSTANCE

        fun registerPrefChangeListener(
                listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            PreferenceManager.getDefaultSharedPreferences(App.context)
                    .registerOnSharedPreferenceChangeListener(listener)
        }

        fun unregisterPrefChangeListener(
                listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            PreferenceManager.getDefaultSharedPreferences(App.context)
                    .unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}
