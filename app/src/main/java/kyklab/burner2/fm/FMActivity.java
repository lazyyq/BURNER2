package kyklab.burner2.fm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kyklab.burner2.R;

public class FMActivity extends AppCompatActivity implements FMAdapterCallback {
    private String mCurrentPath;
    private List<File> mFileList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ImageView mUpperDirIcon;
    private ImageView mHomeDirIcon;
    private TextView mCurrentDirView;
    private FMAdapter mAdapter;
    private RefreshTask mRefreshTask;
    private int mCurrentDepth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm);

        initFM();
        refreshFileList();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRefreshTask.cancel(true);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentDepth == 0) {
            super.onBackPressed();
        } else {
            gotoUpperDirectory();
        }
    }

    private void initFM() {
        mRecyclerView = findViewById(R.id.fmRecyclerView);
        mProgressBar = findViewById(R.id.fmProgressBar);
        mFileList = new ArrayList<>();
        mCurrentDirView = findViewById(R.id.fmCurrentDirectory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new FMAdapter(this, mFileList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation()));
        mSwipeRefreshLayout = findViewById(R.id.fmSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFileList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mUpperDirIcon = findViewById(R.id.fmUpperDirectory);
        mUpperDirIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUpperDirectory();
            }
        });
        mHomeDirIcon = findViewById(R.id.fmHomeDir);
        mHomeDirIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoHomeDirectory();
            }
        });

        gotoHomeDirectory();
    }

    public void refreshFileList() {
        mRefreshTask = new RefreshTask(this);
        mRefreshTask.execute();
    }

    @Override
    public void gotoUpperDirectory() {
        String path;
        if (mCurrentDepth == 0) {
            return;
        } else if (mCurrentDepth == 1) { // Right under root
            path = "/";
        } else {
            path = mCurrentPath.substring(0, mCurrentPath.lastIndexOf(File.separator));
        }
        switchToDirectory(path);
        --mCurrentDepth;
    }

    @Override
    public void enterDirectory(String dir) {
        String path;
        if (mCurrentDepth == 0) {
            path = mCurrentPath + dir;
        } else {
            path = mCurrentPath + File.separator + dir;
        }
        switchToDirectory(path);
        ++mCurrentDepth;
    }

    private void gotoHomeDirectory() {
        String path;
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        mCurrentDepth = path.length() - path.replace("/", "").length();
        switchToDirectory(path);
    }

    private void switchToDirectory(String path) {
        mCurrentPath = path;
        refreshFileList();
    }

    static class RefreshTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<FMActivity> activityWeakReference;

        RefreshTask(FMActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            FMActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FMActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }

            List<File> tempFileList = new ArrayList<>();
            List<File> tempDirList = new ArrayList<>();

            File current = new File(activity.mCurrentPath);
            String[] files = current.list();
            if (files != null) {
                String path;
                File file;

                Arrays.sort(files);
                for (String name : files) {
                    path = activity.mCurrentPath + File.separator + name;
                    file = new File(path);
                    if (file.isDirectory()) {
                        tempDirList.add(file);
                    } else if (name.toLowerCase().endsWith(".jpg")
                            || name.toLowerCase().endsWith(".jpeg")
                            || name.toLowerCase().endsWith(".png")
                            || name.toLowerCase().endsWith(".gif")
                            || name.toLowerCase().endsWith(".webp")) {
                        tempFileList.add(file);
                    }
                }
            }

            activity.mFileList.clear();
            activity.mFileList.addAll(tempDirList);
            activity.mFileList.addAll(tempFileList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            FMActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.mCurrentDirView.setText(activity.mCurrentPath);
            activity.mAdapter.notifyDataSetChanged();
            activity.mProgressBar.setVisibility(View.GONE);
        }
    }
}
