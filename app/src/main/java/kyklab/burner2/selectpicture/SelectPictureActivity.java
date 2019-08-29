package kyklab.burner2.selectpicture;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kyklab.burner2.App;
import kyklab.burner2.R;
import kyklab.burner2.fm.FMActivity;
import kyklab.burner2.utils.FMUtils;
import kyklab.burner2.utils.PrefManager;

public class SelectPictureActivity extends AppCompatActivity {
    private static final String TAG = "SelectPictureActivity";
    private static final int PICTURE_LIST_SPAN_COUNT = 2;
    private static final int REQ_CODE_PERM = 100;
    private static final int REQ_CODE_ACTIVITY_FM = 100;
    private List<PictureItem> mThumbnailList;
    private RecyclerView mRecyclerView;
    private PicturePreviewListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);

        mThumbnailList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.picturesListView);
        mAdapter = new PicturePreviewListAdapter(this, mThumbnailList);
        int margin = getResources().getDimensionPixelSize(R.dimen.picture_list_margin);
        mRecyclerView.addItemDecoration(new GridLayoutItemDecoration(margin));

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateThumbnailList();
    }

    private void updateThumbnailList() {
        mThumbnailList.clear();
        if (App.customPictureExists()) {
            mThumbnailList.add(new PictureItem<>("User picture", App.getCustomPicturePath()));
        }
        mThumbnailList.addAll(Arrays.asList(
                new PictureItem<>("Picture 1", R.drawable.pic1_thumbnail),
                new PictureItem<>("Picture 2", R.drawable.pic2_thumbnail),
                new PictureItem<>("Picture 3", R.drawable.pic3_thumbnail),
                new PictureItem<>("Picture 4", R.drawable.pic4_thumbnail),
                new PictureItem<>("Picture 5", R.drawable.pic5_thumbnail),
                new PictureItem<>("Picture 6", R.drawable.pic6_thumbnail)
        ));
        mAdapter.notifyDataSetChanged();

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, mThumbnailList.size());
        mLayoutManager.setSpanCount(PICTURE_LIST_SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void launchFM() {
        Intent intent = new Intent(SelectPictureActivity.this, FMActivity.class);
        startActivityForResult(intent, REQ_CODE_ACTIVITY_FM);
    }

    private void setCustomPicture(Object pic) {
        FMUtils.copy(this, pic, App.getCustomPicturePath());

        PrefManager.getInstance().setUseCustomPicture(true);
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

    class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {
        private final int margin;

        GridLayoutItemDecoration(int margin) {
            this.margin = margin;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = margin;
            outRect.right = margin;
            outRect.bottom = margin;
            outRect.top = margin;
        }
    }
}
