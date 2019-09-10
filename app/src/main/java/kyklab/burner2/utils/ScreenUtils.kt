package kyklab.burner2.utils

import android.app.Activity
import android.view.View
import android.view.WindowManager

object ScreenUtils {
    val fullscreenOpts = (
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    fun hideSystemUi(activity: Activity) {
        val window = activity.window
        val decorView = window.decorView

        var uiOptions = decorView.systemUiVisibility
        uiOptions = uiOptions or fullscreenOpts

        decorView.systemUiVisibility = uiOptions
    }

    fun showSystemUi(activity: Activity) {
        val window = activity.window
        val decorView = window.decorView

        var uiOptions = decorView.systemUiVisibility
        uiOptions = uiOptions and fullscreenOpts.inv()

        decorView.systemUiVisibility = uiOptions
    }

    fun setKeepScreenOn(activity: Activity) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun unsetKeepScreenOn(activity: Activity) {
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun setMaxBrightness(activity: Activity) {
        val window = activity.window
        val params = window.attributes
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        window.attributes = params
    }

    fun resetBrightness(activity: Activity) {
        val window = activity.window
        val params = window.attributes
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window.attributes = params
    }
}
