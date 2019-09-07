package kyklab.burner2.selectpicture;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import kyklab.burner2.R;
import kyklab.burner2.fm.FMActivity;
import kyklab.burner2.picture.PictureManager;
import kyklab.burner2.utils.PrefManager;

public class SelectPictureActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "SelectPictureActivity";
    private static final int PICTURE_LIST_SPAN_COUNT = 2;
    private static final int REQ_CODE_PERM = 100;
    private static final int REQ_CODE_ACTIVITY_FM = 100;
    private PicturePreviewListAdapter mAdapter;
    private boolean mNeedsRefresh = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);

        setupRecyclerView();
        updateThumbnails();

        FloatingActionButton mFab = findViewById(R.id.fab_search);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        SelectPictureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    launchFM();
                } else {
                    ActivityCompat.requestPermissions(SelectPictureActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQ_CODE_PERM);
                }
            }
        });

        PrefManager.registerPrefChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mNeedsRefresh) {
            updateThumbnails();
            mNeedsRefresh = false;
        }
    }

    private void setupRecyclerView() {
        mAdapter = new PicturePreviewListAdapter(this,
                PictureManager.getInstance().getPictureList());
        RecyclerView mRecyclerView = findViewById(R.id.picturesListView);
        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(
                this, PICTURE_LIST_SPAN_COUNT);
        mRecyclerView.setLayoutManager(layoutManager);
        int margin = getResources().getDimensionPixelSize(R.dimen.picture_preview_list_margin);
        GridLayoutItemDecoration decoration =
                new GridLayoutItemDecoration(margin, margin, margin, margin * 4);
        mRecyclerView.addItemDecoration(decoration);
    }

    private void updateThumbnails() {
        PictureManager.getInstance().updatePictureList();
        mAdapter.notifyDataSetChanged();
    }

    private void launchFM() {
        Intent intent = new Intent(SelectPictureActivity.this, FMActivity.class);
        startActivityForResult(intent, REQ_CODE_ACTIVITY_FM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_PERM:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchFM();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case PrefManager.KEY_SELECTED_PICTURE_INDEX:
                mNeedsRefresh = true;
                break;
        }
    }

    class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {
        private final int leftMargin;
        private final int topMargin;
        private final int rightMargin;
        private final int bottomMargin;

        GridLayoutItemDecoration(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
            this.leftMargin = leftMargin;
            this.topMargin = topMargin;
            this.rightMargin = rightMargin;
            this.bottomMargin = bottomMargin;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = leftMargin;
            outRect.right = rightMargin;
            outRect.bottom = bottomMargin;
            outRect.top = topMargin;
        }
    }
}
