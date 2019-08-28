package kyklab.burner2.batterylimit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Handler;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import kyklab.burner2.utils.PrefManager;

public class BatteryLimiter implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int EXIT_TIMEOUT = 3000;
    private final Context context;
    //private final View layout;
    private Handler mHandler;
    private BroadcastReceiver mBroadcastReceiver;
    private Runnable mExitRunnable;
    private IntentFilter filter;
    private Snackbar mSnackbar;

    private boolean mTimeoutRunning;
    private boolean mReceiverRegistered;
    private boolean mCanceled;

    public BatteryLimiter(final Context context, final View layout) {
        this.context = context;
        //this.layout = layout;
        mHandler = new Handler(context.getMainLooper());
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                exitIfBatteryLow(intent);
            }
        };
        mExitRunnable = new Runnable() {
            @Override
            public void run() {
                mTimeoutRunning = false;
                ((Activity) context).finish();
            }
        };
        filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mSnackbar = Snackbar.make(layout,
                "Battery is low, exiting in " + (EXIT_TIMEOUT / 1000) + " seconds.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancel();
                    }
                });
        mTimeoutRunning = false;
        mReceiverRegistered = false;
        PrefManager.registerPrefChangeListener(this);
    }

    public void start() {
        if (!mCanceled && !mReceiverRegistered) {
            context.registerReceiver(mBroadcastReceiver, filter);
            mReceiverRegistered = true;
        }
    }

    public void stop() {
        if (mReceiverRegistered) {
            context.unregisterReceiver(mBroadcastReceiver);
            mReceiverRegistered = false;
        }
        stopExitTimeout();
    }

    /**
     * Do not restart automatic exit unless the user changes
     * battery limit related settings.
     */
    public void cancel() {
        stop();
        mCanceled = true;
    }

    private void exitIfBatteryLow(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int limit = PrefManager.getInstance().getBatteryLimit();
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        if (!isCharging && level != -1 && level <= limit) {
            startExitTimeout();
        } else {
            stopExitTimeout();
        }
    }

    private void startExitTimeout() {
        if (!mTimeoutRunning) {
            mHandler.postDelayed(mExitRunnable, EXIT_TIMEOUT);
            mSnackbar.show();
            mTimeoutRunning = true;
        }
    }

    private void stopExitTimeout() {
        if (mTimeoutRunning) {
            mHandler.removeCallbacks(mExitRunnable);
            mSnackbar.dismiss();
            mTimeoutRunning = false;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case PrefManager.KEY_BATTERY_LIMIT:
            case PrefManager.KEY_BATTERY_LIMIT_ENABLED:
                mCanceled = false;
                break;
        }
    }
}
