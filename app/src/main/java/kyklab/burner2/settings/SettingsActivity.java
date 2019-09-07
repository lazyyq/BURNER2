package kyklab.burner2.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import kyklab.burner2.R;
import kyklab.burner2.picture.PictureItem;
import kyklab.burner2.picture.PictureManager;
import kyklab.burner2.utils.PrefManager;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ImageView toolbarBackground;
    private boolean mNeedsRefresh = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        toolbarBackground = findViewById(R.id.settings_toolbar_background_image);
        loadPicture();

        PrefManager.registerPrefChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mNeedsRefresh) {
            loadPicture();
            mNeedsRefresh = false;
        }
    }

    @Override
    protected void onDestroy() {
        PrefManager.unregisterPrefChangeListener(this);
        super.onDestroy();
    }

    private void loadPicture() {
        PictureItem pictureItem = PictureManager.getInstance().getPictureList()
                .get(PrefManager.getInstance().getSelectedPictureIndex());
        Object picture = pictureItem.getPicture();
        ObjectKey key = new ObjectKey(pictureItem.getVersionMetadata());
        Glide.with(this)
                .load(picture)
                .centerCrop()
                .signature(key)
                .into(toolbarBackground);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case PrefManager.Key.SELECTED_PICTURE_INDEX:
                mNeedsRefresh = true;
                break;
        }
    }
}
