package kyklab.burner2;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import kyklab.burner2.utils.ScreenUtils;
import kyklab.burner2.utils.ViewUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int HIDE_DELAY = 3000;
    private static final long MINI_FAB_ANIM_LENGTH = 300L;
    private static final long MINI_FAB_ANIM_DELAY = 100L;
    private final Handler mHideHandler = new Handler();
    private View fabBackground;
    private ImageView imageView;
    private boolean mFullscreen = false;
    private FloatingActionButton fab;
    private float miniFabTransitionDistance;
    private View[] miniFabs;
    private View[] miniFabTexts;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideUi();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.mainImage);
        imageView.setOnClickListener(this);

        setupFab();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mFullscreen) {
            delayedHide(HIDE_DELAY);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        /*if (view == miniFabItems[0].findViewById(R.id.miniFab)) {
            Toast.makeText(this, "0", Toast.LENGTH_SHORT).show();
        } else if (view == miniFabItems[1].findViewById(R.id.miniFab)) {
            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
        } else if (view == miniFabItems[2].findViewById(R.id.miniFab)) {
            Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
        } else */
        if (id == R.id.mainImage) {
            toggleFullscreen();
            if (!mFullscreen) {
                delayedHide(HIDE_DELAY);
            }
        } else if (id == R.id.fab) {
            if (fab.isExpanded()) {
                collapseFab();
            } else {
                expandFab();
            }
            delayedHide(HIDE_DELAY);
        }
    }

    private void setupFab() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

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

    private void toggleFullscreen() {
        if (!mFullscreen) {
            hideUi();
        } else {
            showUi();
        }
    }

    private void hideUi() {
        ScreenUtils.hideSystemUi(this);
        collapseFab();
        fab.hide();

        mFullscreen = true;
    }

    private void showUi() {
        ScreenUtils.showSystemUi(this);
        fab.show();

        mFullscreen = false;
    }

    private void collapseFab() {
        fabBackground.setVisibility(View.GONE);
        ViewUtils.animateHideInOrder(
                miniFabs, 0, miniFabTransitionDistance, MINI_FAB_ANIM_LENGTH, MINI_FAB_ANIM_DELAY);
        ViewUtils.animateHideInOrder(
                miniFabTexts, 0, miniFabTransitionDistance, MINI_FAB_ANIM_LENGTH, MINI_FAB_ANIM_DELAY);
        fab.setExpanded(false);
    }

    private void expandFab() {
        fabBackground.setVisibility(View.VISIBLE);
        ViewUtils.animateShowInOrder(
                miniFabs, 0, -miniFabTransitionDistance, MINI_FAB_ANIM_LENGTH, MINI_FAB_ANIM_DELAY);
        ViewUtils.animateShowInOrder(
                miniFabTexts, 0, -miniFabTransitionDistance, MINI_FAB_ANIM_LENGTH, MINI_FAB_ANIM_DELAY);
        fab.setExpanded(true);
    }

    private void delayedHide(int delay) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delay);
    }
}
