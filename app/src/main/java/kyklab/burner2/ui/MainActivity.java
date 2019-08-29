package kyklab.burner2.ui;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int HIDE_UI_DELAY = 3000;
    private final Handler mHandler = new Handler();
    private RotateLayout mRotateLayout;
    private ImageView mImageView;
    private SpeedDialView mFab;
    private boolean mFullscreen = false;
    private final Runnable mHideUiRunnable = new Runnable() {
        @Override
        public void run() {
            hideUi();
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
        setupFab();
        App.updatePictureList();

        batteryLimiter = new BatteryLimiter(this, findViewById(R.id.coordinatorLayout));
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
                if (mFullscreen) {
                    ScreenUtils.setMaxBrightness(this);
                } else {
                    ScreenUtils.resetBrightness(this);
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
/*

    private void setupFab() {
        mFab = findViewById(R.id.mFab);
        mFab.setOnClickListener(this);

        fabBackground = findViewById(R.id.fabBackground);
        fabBackground.setOnClickListener(this);

        View[] miniFabItems = new View[]{findViewById(R.id.miniFabItem1),
                findViewById(R.id.miniFabItem2), findViewById(R.id.miniFabItem3)};

        miniFabs = new View[miniFabItems.length];
        for (int i = 0, len = miniFabs.length; i < len; ++i) {
            miniFabs[i] = miniFabItems[i].findViewById(R.id.miniFab);
        }

        miniFabTexts = new View[miniFabItems.length];
        for (int i = 0, len = miniFabTexts.length; i < len; ++i) {
            miniFabTexts[i] = miniFabItems[i].findViewById(R.id.miniFabTextCard);
        }

        for (View v : miniFabItems) {
            v.findViewById(R.id.miniFab).setOnClickListener(this);
        }

        final View miniFabContainer = findViewById(R.id.miniFabContainer);

        final float fabSizeNormal = getResources().getDimension(R.dimen.fab_size_normal);
        miniFabTransitionDistance = getResources().getDimension(R.dimen.mini_fab_transition_distance);

        final View temp = miniFabs[0];
        temp.post(new Runnable() {
            @Override
            public void run() {
                final int miniFabSize = temp.getMeasuredHeight();
                miniFabContainer.setPadding(0, 0,
                        (int) (fabSizeNormal - miniFabSize) / 2, 0);
            }
        });
    }
*/

    private void toggleFullscreen() {
        if (!mFullscreen) {
            hideUi();
        } else {
            showUi();
        }
    }

    private void hideUi() {
        ScreenUtils.hideSystemUi(this);
        //collapseFab();
        mFab.hide();

        mFullscreen = true;
    }

    private void showUi() {
        ScreenUtils.showSystemUi(this);
        mFab.show();

        mFullscreen = false;
    }

    /*private void collapseFab() {
        fabBackground.setVisibility(View.GONE);
        ViewUtils.animateHideInOrder(
                miniFabs, 0, miniFabTransitionDistance, MINI_FAB_ANIM_LENGTH, MINI_FAB_ANIM_DELAY);
        ViewUtils.animateHideInOrder(
                miniFabTexts, 0, miniFabTransitionDistance, MINI_FAB_ANIM_LENGTH, MINI_FAB_ANIM_DELAY);
        mFab.setExpanded(false);
    }*/

    /*private void expandFab() {
        fabBackground.setVisibility(View.VISIBLE);
        ViewUtils.animateShowInOrder(
                miniFabs, 0, -miniFabTransitionDistance, MINI_FAB_ANIM_LENGTH, MINI_FAB_ANIM_DELAY);
        ViewUtils.animateShowInOrder(
                miniFabTexts, 0, -miniFabTransitionDistance, MINI_FAB_ANIM_LENGTH, MINI_FAB_ANIM_DELAY);
        mFab.setExpanded(true);
    }*/

    private void delayedHideUi(int delay) {
        mHandler.removeCallbacks(mHideUiRunnable);
        mHandler.postDelayed(mHideUiRunnable, delay);
    }
}
