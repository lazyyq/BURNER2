package kyklab.burner2.settings.selectpicture;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kyklab.burner2.R;

public class SelectPictureActivity extends AppCompatActivity {
    private static final int PICTURE_LIST_SPAN_COUNT = 2;
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
    }

    class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {
        private final int margin;

        GridLayoutItemDecoration(int margin) {
            this.margin = margin;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view);
            outRect.left = margin;
            outRect.right = margin;
            outRect.bottom = margin;
            outRect.top = position >= PICTURE_LIST_SPAN_COUNT ? margin : 0;
        }
    }
}
