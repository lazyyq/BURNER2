package kyklab.burner2.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import kyklab.burner2.App;
import kyklab.burner2.R;
import kyklab.burner2.selectpicture.PictureItem;
import kyklab.burner2.utils.PrefManager;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private List<PictureItem> mPictureList;
    private ImageView toolbarBackground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        mPictureList = App.getPictureList();
        toolbarBackground = findViewById(R.id.settings_toolbar_background_image);

        PrefManager.registerPrefChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPicture();
    }

    @Override
    protected void onDestroy() {
        PrefManager.unregisterPrefChangeListener(this);
        super.onDestroy();
    }

    private void loadPicture() {
        Object picture = mPictureList.get(PrefManager.getInstance().getSelectedPictureIndex()).getPicture();
        Glide.with(this).load(picture).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(toolbarBackground);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case PrefManager.KEY_SELECTED_PICTURE_INDEX:
                loadPicture();
                break;
        }
    }
}
