package kyklab.burner2.fm;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kyklab.burner2.R;
import kyklab.burner2.utils.FMUtils;

public class FMActivity extends AppCompatActivity {
    private String mCurrentPath;
    private List<FileItem> mFileList;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private FMAdapter mAdapter;
    private RefreshTask mRefreshTask;

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

    private void initFM() {
        mRecyclerView = findViewById(R.id.fmRecyclerView);
        mProgressBar = findViewById(R.id.fmProgressBar);
        mFileList = new ArrayList<>();
        mCurrentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new FMAdapter(this, mFileList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation()));
    }

    public void refreshFileList() {
        mRefreshTask = new RefreshTask();
        mRefreshTask.execute();
    }

    class RefreshTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<FileItem> tempFileList = new ArrayList<>();
            List<FileItem> tempDirList = new ArrayList<>();

            File current = new File(mCurrentPath);
            String[] files = current.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    // Search only for images and directories
                    return name.toLowerCase().endsWith(".jpg")
                            || name.toLowerCase().endsWith(".jpeg")
                            || name.toLowerCase().endsWith(".png")
                            || name.toLowerCase().endsWith(".gif")
                            || name.toLowerCase().endsWith(".webp")
                            || (new File(dir + File.separator + name)).isDirectory();
                }
            });

            if (files != null) {
                String path;

                Arrays.sort(files);
                for (String name : files) {
                    path = mCurrentPath + File.separator + name;
                    if (FMUtils.isDirectory(path)) {
                        tempDirList.add(new FileItem(
                                getResources().getDrawable(R.drawable.ic_folder_36dp,
                                        getTheme()), path, name, true));
                    } else {
                        tempFileList.add(new FileItem(
                                Drawable.createFromPath(path), path, name, false));
                    }
                }
            }

            mFileList.clear();
            mFileList.addAll(tempDirList);
            mFileList.addAll(tempFileList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
