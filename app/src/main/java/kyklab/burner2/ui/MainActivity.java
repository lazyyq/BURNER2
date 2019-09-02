package kyklab.burner2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.List;

import kyklab.burner2.App;
import kyklab.burner2.R;
import kyklab.burner2.batterylimit.BatteryLimiter;
import kyklab.burner2.selectpicture.PictureItem;
import kyklab.burner2.settings.SettingsActivity;
import kyklab.burner2.utils.PrefManager;
import kyklab.burner2.utils.ScreenUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int HIDE_UI_DELAY = 3000;
    private final Handler mHandler = new Handler();
    private RotateLayout mRotateLayout;
    private ImageView mImageView;
    private SpeedDialView mFab;
    private boolean mFullscreen = false;
    private boolean mNeedsRefresh = false;
    private final Runnable mHideUiRunnable = new Runnable() {
        @Override
        public void run() {
            toggleFullscreen(true);
        }
    };
    private final Runnable mMaxBrightnessRunnable = new Runnable() {
        @Override
        public void run() {
            if (PrefManager.getInstance().getMaxBrightness()) {
                ScreenUtils.setMaxBrightness(MainActivity.this);
            }
        }
    };
    private BatteryLimiter batteryLimiter;

    private List<PictureItem> mPictureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.mainImage);
        mImageView.setOnClickListener(this);
        mRotateLayout = findViewById(R.id.rotateLayout);
        mPictureList = App.getPictureList();
        App.updatePictureList();
        loadPicture();

        setupFab();
        batteryLimiter = new BatteryLimiter(this, findViewById(R.id.coordinatorLayout));

        PrefManager.registerPrefChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mNeedsRefresh) {
            loadPicture();
            mNeedsRefresh = false;
        }

        if (!mFullscreen) {
            delayedHideUi();
            delayedMaxBrightness();
        }

        if (PrefManager.getInstance().getKeepScreenOn()) {
            ScreenUtils.setKeepScreenOn(this);
        }

        if (PrefManager.getInstance().getBatteryLimitEnabled()) {
            batteryLimiter.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        batteryLimiter.stop();
        ScreenUtils.unsetKeepScreenOn(this);
        ScreenUtils.resetBrightness(this);
    }

    @Override
    protected void onDestroy() {
        PrefManager.unregisterPrefChangeListener(this);

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mainImage:
                toggleFullscreen();
                toggleBrightness();
                if (!mFullscreen) {
                    delayedHideUi();
                    delayedMaxBrightness();
                }
                break;
        }
    }

    private void loadPicture() {
        int rotateAngle = Integer.parseInt(PrefManager.getInstance().getRotateAngle());
        mRotateLayout.setAngle(rotateAngle);
        PictureItem pictureItem = mPictureList.get(PrefManager.getInstance().getSelectedPictureIndex());
        Object picture = pictureItem.getPicture();
        ObjectKey key = new ObjectKey(pictureItem.getMetadata() != null ? pictureItem.getMetadata() : picture);
        Glide.with(this)
                .load(picture)
                .signature(key)
                .into(mImageView);
    }

    private void setupFab() {
        mFab = findViewById(R.id.fab);
        mFab.getMainFab().setImageTintList(ColorStateList.valueOf(Color.WHITE));
        mFab.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                return false;
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
                // Reset hide delay on every click
                delayedHideUi();
                delayedMaxBrightness();
            }
        });

        mFab.addActionItem(
                new SpeedDialActionItem
                        .Builder(R.id.fab_settings, R.drawable.ic_settings_white_24dp)
                        .setTheme(android.R.style.Theme_Material_Light)
                        .setLabel("Settings")
                        .create());
        mFab.addActionItem(
                new SpeedDialActionItem
                        .Builder(R.id.fab_temp, R.drawable.ic_settings_white_24dp)
                        .setLabel("temp")
                        .setTheme(android.R.style.Theme_Material_Light)
                        .create());

        mFab.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_settings:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.fab_temp:
                        Toast.makeText(MainActivity.this, "fab_temp", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    private void toggleFullscreen(boolean b) {
        mFullscreen = !b;
        toggleFullscreen();
    }

    private void toggleFullscreen() {
        if (!mFullscreen) {
            hideUi();
        } else {
            showUi();
        }
    }

    private void toggleBrightness() {
        if (mFullscreen) {
            if (PrefManager.getInstance().getMaxBrightness()) {
                ScreenUtils.setMaxBrightness(this);
            }
        } else {
            ScreenUtils.resetBrightness(this);
        }
    }

    private void delayedMaxBrightness() {
        mHandler.removeCallbacks(mMaxBrightnessRunnable);
        mHandler.postDelayed(mMaxBrightnessRunnable, HIDE_UI_DELAY);
    }

    private void hideUi() {
        ScreenUtils.hideSystemUi(this);
        mFab.hide();

        mFullscreen = true;
    }

    private void showUi() {
        ScreenUtils.showSystemUi(this);
        mFab.show();

        mFullscreen = false;
    }

    private void delayedHideUi() {
        mHandler.removeCallbacks(mHideUiRunnable);
        mHandler.postDelayed(mHideUiRunnable, HIDE_UI_DELAY);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case PrefManager.KEY_MAX_BRIGHTNESS:
                if (mFullscreen) {
                    if (PrefManager.getInstance().getMaxBrightness()) {
                        ScreenUtils.setMaxBrightness(this);
                    } else {
                        ScreenUtils.resetBrightness(this);
                    }
                }
                break;
            case PrefManager.KEY_SELECTED_PICTURE_INDEX:
            case PrefManager.KEY_ROTATE_ANGLE:
                mNeedsRefresh = true;
                break;
        }
    }
}
