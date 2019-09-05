package kyklab.burner2.selectpicture;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kyklab.burner2.App;
import kyklab.burner2.R;
import kyklab.burner2.fm.FMActivity;
import kyklab.burner2.utils.FMUtils;
import kyklab.burner2.utils.PrefManager;

public class SelectPictureActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "SelectPictureActivity";
    private static final int PICTURE_LIST_SPAN_COUNT = 2;
    private static final int REQ_CODE_PERM = 100;
    private static final int REQ_CODE_ACTIVITY_FM = 100;
    private List<PictureItem> mThumbnailList;
    private PicturePreviewListAdapter mAdapter;
    private boolean mNeedsRefresh = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);

        setupRecyclerView();
        updateThumbnailList();

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
            updateThumbnailList();
            mNeedsRefresh = false;
        }
    }

    private void setupRecyclerView() {
        mThumbnailList = new ArrayList<>();
        mAdapter = new PicturePreviewListAdapter(this, mThumbnailList);
        RecyclerView mRecyclerView = findViewById(R.id.picturesListView);
        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(
                this, PICTURE_LIST_SPAN_COUNT);
        mRecyclerView.setLayoutManager(layoutManager);
        int margin = getResources().getDimensionPixelSize(R.dimen.picture_list_margin);
        GridLayoutItemDecoration decoration =
                new GridLayoutItemDecoration(margin, margin, margin, margin * 4);
        mRecyclerView.addItemDecoration(decoration);
    }

    private void updateThumbnailList() {
        List<PictureItem> tempList = new ArrayList<>();
        if (App.customPictureExists()) {
            tempList.add(App.getPictureList().get(0));
        }
        tempList.addAll(Arrays.asList(
                new PictureItem<>("Picture 1", R.drawable.pic1_thumbnail),
                new PictureItem<>("Picture 2", R.drawable.pic2_thumbnail),
                new PictureItem<>("Picture 3", R.drawable.pic3_thumbnail),
                new PictureItem<>("Picture 4", R.drawable.pic4_thumbnail),
                new PictureItem<>("Picture 5", R.drawable.pic5_thumbnail),
                new PictureItem<>("Picture 6", R.drawable.pic6_thumbnail)
        ));
        mThumbnailList.clear();
        mThumbnailList.addAll(tempList);
        mAdapter.notifyDataSetChanged();
    }

    private void launchFM() {
        Intent intent = new Intent(SelectPictureActivity.this, FMActivity.class);
        startActivityForResult(intent, REQ_CODE_ACTIVITY_FM);
    }

    private void setCustomPicture(Object pic) {
        try {
            FMUtils.copy(this, pic, App.getCustomPicturePath());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error while setting picture", Toast.LENGTH_SHORT).show();
            return;
        }

        PrefManager.getInstance().setPicLastUpdatedTime(System.currentTimeMillis());
        // Always trigger onSharedPreferenceChangeListener
        PrefManager.getInstance().removePref(PrefManager.KEY_SELECTED_PICTURE_INDEX);
        PrefManager.getInstance().setSelectedPictureIndex(0);

        App.updatePictureList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQ_CODE_ACTIVITY_FM:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Bundle b = data.getExtras();
                    if (b != null) {
                        setCustomPicture(b.get("picture"));
                    } else {
                        return;
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
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
