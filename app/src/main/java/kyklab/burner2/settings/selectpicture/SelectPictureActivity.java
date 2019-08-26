package kyklab.burner2.settings.selectpicture;

import android.Manifest;
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
import java.util.List;

import kyklab.burner2.R;
import kyklab.burner2.fm.FMActivity;

public class SelectPictureActivity extends AppCompatActivity {
    private static final int PICTURE_LIST_SPAN_COUNT = 2;
    private static final int PERM_REQ_CODE = 100;
    private List<PictureItem> mThumbnailList;
    private RecyclerView mRecyclerView;
    private PicturePreviewListAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);

        mThumbnailList = new ArrayList<PictureItem>() {{
            add(new PictureItem("Picture 1", R.drawable.pic1_thumbnail));
            add(new PictureItem("Picture 2", R.drawable.pic2_thumbnail));
            add(new PictureItem("Picture 3", R.drawable.pic3_thumbnail));
            add(new PictureItem("Picture 4", R.drawable.pic4_thumbnail));
            add(new PictureItem("Picture 5", R.drawable.pic5_thumbnail));
            add(new PictureItem("Picture 6", R.drawable.pic6_thumbnail));
        }};

        mRecyclerView = findViewById(R.id.picturesListView);
        mAdapter = new PicturePreviewListAdapter(this, mThumbnailList);
        mLayoutManager = new GridLayoutManager(this, mThumbnailList.size());
        mLayoutManager.setSpanCount(PICTURE_LIST_SPAN_COUNT);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        int margin = getResources().getDimensionPixelSize(R.dimen.picture_list_margin);
        mRecyclerView.addItemDecoration(new GridLayoutItemDecoration(margin));

        FloatingActionButton mFab = findViewById(R.id.fab_search);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        SelectPictureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    openFileManager();
                } else {
                    ActivityCompat.requestPermissions(SelectPictureActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERM_REQ_CODE);
                }
            }
        });
    }

    private void openFileManager() {
        Intent intent = new Intent(SelectPictureActivity.this, FMActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERM_REQ_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFileManager();
                }
                break;
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
