package kyklab.burner2.fm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
    private static final int REQ_CODE_ACTIVITY_DOCUMENTS_UI = 100;
    private String mCurrentPath;
    private List<File> mFileList;
    private TextView mToolbarText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
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

        ActionBar mActionBar = getSupportActionBar();
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
            case R.id.fm_action_gallery:
                launchGallery();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initFM() {
        mToolbarText = findViewById(R.id.fmToolbarText);
        RecyclerView mRecyclerView = findViewById(R.id.fmRecyclerView);
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

    private void refreshFileList() {
        mRefreshTask = new RefreshTask(this);
        mRefreshTask.execute();
    }

    @Override
    public void gotoUpperDirectory() {
        String path;
        if (mCurrentDepth == 0) {
            // Root
            return;
        } else if (mCurrentDepth == 1) {
            // Right under root
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

    @Override
    public void customPictureSelected(Object obj) {
        Intent intent = new Intent();
        if (obj instanceof String) {
            intent.putExtra("picture", (String) obj);
        } else if (obj instanceof Uri) {
            intent.putExtra("picture", (Uri) obj);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /*
    @Override
    public void customPictureSelected(String path) {
        Intent intent = new Intent();
        intent.putExtra("path", path);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }*/

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, REQ_CODE_ACTIVITY_DOCUMENTS_UI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQ_CODE_ACTIVITY_DOCUMENTS_UI:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    customPictureSelected(uri);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
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
