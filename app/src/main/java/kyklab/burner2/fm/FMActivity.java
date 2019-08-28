package kyklab.burner2.fm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomappbar.BottomAppBar;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kyklab.burner2.R;

public class FMActivity extends AppCompatActivity implements FMAdapterCallback {
    private String mCurrentPath;
    private List<File> mFileList;
    private ActionBar mActionBar;
    private TextView mToolbarText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private FMAdapter mAdapter;
    private RefreshTask mRefreshTask;
    private int mCurrentDepth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm);
        BottomAppBar mBottomAppBar = findViewById(R.id.fmBottomAppBar);
        setSupportActionBar(mBottomAppBar);

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.fm_action_home:
                gotoHomeDirectory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initFM() {
        mToolbarText = findViewById(R.id.fmToolbarText);
        mRecyclerView = findViewById(R.id.fmRecyclerView);
        mProgressBar = findViewById(R.id.fmProgressBar);
        mFileList = new ArrayList<>();
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
        mToolbarText.setText(path);
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

            activity.mAdapter.notifyDataSetChanged();
            activity.mProgressBar.setVisibility(View.GONE);
        }
    }
}
