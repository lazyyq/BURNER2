package kyklab.burner2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
    private final Runnable mHideUiRunnable = new Runnable() {
        @Override
        public void run() {
            setFullscreen(true);
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
        setupFab();
        App.updatePictureList();

        batteryLimiter = new BatteryLimiter(this, findViewById(R.id.coordinatorLayout));

        PrefManager.registerPrefChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mFullscreen) {
            delayedHideUi(HIDE_UI_DELAY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPicture();

        if (PrefManager.getInstance().getKeepScreenOn()) {
            ScreenUtils.setKeepScreenOn(this);
        } else {
            ScreenUtils.unsetKeepScreenOn(this);
        }

        if (PrefManager.getInstance().getBatteryLimitEnabled()) {
            batteryLimiter.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        batteryLimiter.stop();
        ScreenUtils.resetBrightness(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mainImage:
                toggleFullscreen();
                if (!mFullscreen) {
                    delayedHideUi(HIDE_UI_DELAY);
                }
                break;
        }
    }

    private void loadPicture() {
        int rotateAngle = Integer.parseInt(PrefManager.getInstance().getRotateAngle());
        mRotateLayout.setAngle(rotateAngle);
        Glide.with(this)
                .load(mPictureList.get(PrefManager.getInstance().getSelectedPictureIndex()).getPicture())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(mImageView);
    }

    private void setupFab() {
        mFab = findViewById(R.id.fab);

        mFab.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                return false;
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
                // Reset hide delay on every click
                delayedHideUi(HIDE_UI_DELAY);
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

    private void setFullscreen(boolean b) {
        mFullscreen = !b;
        toggleFullscreen();
    }

    private void toggleFullscreen() {
        if (!mFullscreen) {
            hideUi();
            if (PrefManager.getInstance().getMaxBrightness()) {
                ScreenUtils.setMaxBrightness(this);
            }
        } else {
            showUi();
            ScreenUtils.resetBrightness(this);
        }
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

    private void delayedHideUi(int delay) {
        mHandler.removeCallbacks(mHideUiRunnable);
        mHandler.postDelayed(mHideUiRunnable, delay);
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
        }
    }
}
