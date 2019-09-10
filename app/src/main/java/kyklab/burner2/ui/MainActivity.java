package kyklab.burner2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.leinardi.android.speeddial.FabWithLabelView;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import kyklab.burner2.R;
import kyklab.burner2.batterylimit.BatteryLimiter;
import kyklab.burner2.picture.PictureItem;
import kyklab.burner2.picture.PictureManager;
import kyklab.burner2.settings.SettingsActivity;
import kyklab.burner2.utils.PrefManager;
import kyklab.burner2.utils.ScreenUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int AUTO_FULLSCREEN_DELAY = 3000;
    private final Handler mHandler = new Handler();
    /**
     * Whether current activity is in fullscreen mode, where System UI is hidden
     * and screen brightness is set to max (if enabled in settings).
     */
    private boolean mFullscreen = false;
    private RotateLayout mRotateLayout;
    private ImageView mImageView;
    private SpeedDialView mFab;
    private final Runnable mFullscreenRunnable = new Runnable() {
        @Override
        public void run() {
            setFullscreen();
        }
    };

    private boolean mNeedsRefresh = false;
    private BatteryLimiter batteryLimiter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.mainImage);
        mImageView.setOnClickListener(this);
        mRotateLayout = findViewById(R.id.rotateLayout);
        setupFab();
        PictureManager.getInstance().updatePictureList();
        setupImageView();
        loadPicture();

        batteryLimiter = new BatteryLimiter(this, findViewById(R.id.coordinatorLayout));
        PrefManager.registerPrefChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mNeedsRefresh) {
            setupImageView();
            loadPicture();
            mNeedsRefresh = false;
        }

        delayedSetFullscreen();
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
        unsetFullscreen();
        ScreenUtils.unsetKeepScreenOn(this);
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
                if (!mFullscreen) {
                    delayedSetFullscreen();
                }
                break;
        }
    }

    private void setupImageView() {
        int rotateAngle = Integer.parseInt(PrefManager.getInstance().getRotateAngle());
        mRotateLayout.setAngle(rotateAngle);
        ImageView.ScaleType scaleType;
        switch (PrefManager.getInstance().getScaleType()) {
            default:
            case PrefManager.ImageScaleType.DEFAULT:
                scaleType = ImageView.ScaleType.FIT_CENTER;
                break;
            case PrefManager.ImageScaleType.CENTER:
                scaleType = ImageView.ScaleType.CENTER;
                break;
            case PrefManager.ImageScaleType.CENTER_CROP:
                scaleType = ImageView.ScaleType.CENTER_CROP;
                break;
            case PrefManager.ImageScaleType.FIT_XY:
                scaleType = ImageView.ScaleType.FIT_XY;
                break;
        }
        mImageView.setScaleType(scaleType);
    }

    private void loadPicture() {
        PictureItem pictureItem = PictureManager.getInstance().getPictureList()
                .get(PrefManager.getInstance().getSelectedPictureIndex());
        Object picture = pictureItem.getPicture();
        ObjectKey key = new ObjectKey(pictureItem.getVersionMetadata());
        Glide.with(this)
                .load(picture)
                .signature(key)
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
                delayedSetFullscreen();
            }
        });

        FabWithLabelView[] fabWithLabelView = new FabWithLabelView[2];

        fabWithLabelView[0] = mFab.addActionItem(
                new SpeedDialActionItem
                        .Builder(R.id.fab_settings, AppCompatResources.getDrawable(this,
                        R.drawable.ic_settings_white_24dp))
                        .setFabBackgroundColor(Color.WHITE)
                        .setFabImageTintColor(Color.BLACK)
                        .setLabel("Settings")
                        .create());
        if (fabWithLabelView[0] != null) {
            fabWithLabelView[0].setSpeedDialActionItem(
                    fabWithLabelView[0].getSpeedDialActionItemBuilder().create());
        }

        fabWithLabelView[1] = mFab.addActionItem(
                new SpeedDialActionItem
                        .Builder(R.id.fab_temp, AppCompatResources.getDrawable(this,
                        R.drawable.ic_settings_white_24dp))
                        .setFabBackgroundColor(Color.WHITE)
                        .setFabImageTintColor(Color.BLACK)
                        .setLabel("temp")
                        .create());
        if (fabWithLabelView[1] != null) {
            fabWithLabelView[1].setSpeedDialActionItem(
                    fabWithLabelView[1].getSpeedDialActionItemBuilder().create());
        }

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

    private void toggleFullscreen() {
        toggleFullscreen(!mFullscreen);
    }

    private void toggleFullscreen(boolean b) {
        if (b) {
            setFullscreen();
        } else {
            unsetFullscreen();
        }
    }

    private void setFullscreen() {
        hideUi();
        if (PrefManager.getInstance().getMaxBrightness()) {
            ScreenUtils.setMaxBrightness(this);
        }

        mFullscreen = true;
    }

    private void unsetFullscreen() {
        showUi();
        ScreenUtils.resetBrightness(this);
        mHandler.removeCallbacks(mFullscreenRunnable);

        mFullscreen = false;
    }

    private void delayedSetFullscreen() {
        mHandler.removeCallbacks(mFullscreenRunnable);
        mHandler.postDelayed(mFullscreenRunnable, AUTO_FULLSCREEN_DELAY);
    }

    private void hideUi() {
        ScreenUtils.hideSystemUi(this);
        mFab.hide();
    }

    private void showUi() {
        ScreenUtils.showSystemUi(this);
        mFab.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case PrefManager.Key.MAX_BRIGHTNESS:
                if (mFullscreen) {
                    if (PrefManager.getInstance().getMaxBrightness()) {
                        ScreenUtils.setMaxBrightness(this);
                    } else {
                        ScreenUtils.resetBrightness(this);
                    }
                }
                break;
            case PrefManager.Key.SELECTED_PICTURE_INDEX:
            case PrefManager.Key.ROTATE_ANGLE:
            case PrefManager.Key.SCALE_TYPE:
                mNeedsRefresh = true;
                break;
        }
    }
}
