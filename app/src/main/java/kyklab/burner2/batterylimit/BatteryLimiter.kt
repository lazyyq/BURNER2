package kyklab.burner2.batterylimit

import android.app.Activity
import android.content.*
import android.os.BatteryManager
import android.os.Handler
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kyklab.burner2.utils.PrefManager

class BatteryLimiter(private val context: Context, layout: View) : SharedPreferences.OnSharedPreferenceChangeListener {
    //private final View layout;
    private val mHandler: Handler
    private val mBroadcastReceiver: BroadcastReceiver
    private val mExitRunnable: Runnable
    private val filter: IntentFilter
    private val mSnackbar: Snackbar

    private var mTimeoutRunning: Boolean = false
    private var mReceiverRegistered: Boolean = false
    private var mCanceled: Boolean = false

    init {
        //this.layout = layout;
        mHandler = Handler(context.mainLooper)
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                exitIfBatteryLow(intent)
            }
        }
        mExitRunnable = Runnable {
            mTimeoutRunning = false
            (context as Activity).finish()
        }
        filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        mSnackbar = Snackbar.make(layout,
                "Battery is low, exiting in " + EXIT_TIMEOUT / 1000 + " seconds.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.cancel) { cancel() }
        mTimeoutRunning = false
        mReceiverRegistered = false
        PrefManager.registerPrefChangeListener(this)
    }

    fun start() {
        if (!mCanceled && !mReceiverRegistered) {
            context.registerReceiver(mBroadcastReceiver, filter)
            mReceiverRegistered = true
        }
    }

    fun stop() {
        if (mReceiverRegistered) {
            context.unregisterReceiver(mBroadcastReceiver)
            mReceiverRegistered = false
        }
        stopExitTimeout()
    }

    /**
     * Do not restart automatic exit unless the user changes
     * battery limit related settings.
     */
    fun cancel() {
        stop()
        mCanceled = true
    }

    private fun exitIfBatteryLow(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val limit = PrefManager.instance.batteryLimit
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        if (!isCharging && level != -1 && level <= limit) {
            startExitTimeout()
        } else {
            stopExitTimeout()
        }
    }

    private fun startExitTimeout() {
        if (!mTimeoutRunning) {
            mHandler.postDelayed(mExitRunnable, EXIT_TIMEOUT.toLong())
            mSnackbar.show()
            mTimeoutRunning = true
        }
    }

    private fun stopExitTimeout() {
        if (mTimeoutRunning) {
            mHandler.removeCallbacks(mExitRunnable)
            mSnackbar.dismiss()
            mTimeoutRunning = false
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        when (s) {
            PrefManager.Key.BATTERY_LIMIT, PrefManager.Key.BATTERY_LIMIT_ENABLED -> mCanceled = false
        }
    }

    companion object {
        private val EXIT_TIMEOUT = 3000
    }
}
