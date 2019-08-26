package kyklab.burner2.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.github.rongi.rotate_layout.layout.RotateLayout;

import java.util.List;

import kyklab.burner2.App;
import kyklab.burner2.R;
import kyklab.burner2.settings.selectpicture.PictureItem;
import kyklab.burner2.utils.PrefManager;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private List<PictureItem> mPictureList;
    private RotateLayout mRotateLayout;
    private ImageView toolbarBackground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        mPictureList = App.getPictureList();

        mRotateLayout = findViewById(R.id.settings_rotate_layout);
        toolbarBackground = findViewById(R.id.settings_toolbar_background_image);

        //rotatePicture();
        loadPicture();

        PrefManager.registerPrefChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        PrefManager.unregisterPrefChangeListener(this);
        super.onDestroy();
    }

    /*
    private void rotatePicture() {
        int rotateAngle = Integer.parseInt(PrefManager.getInstance().getRotateAngle());
        mRotateLayout.setAngle(rotateAngle);
    }
    */

    private void loadPicture() {
        int pictureResId = mPictureList.get(PrefManager.getInstance().getSelectedPicture()).getResId();
        Glide.with(this).load(pictureResId).centerCrop().into(toolbarBackground);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case PrefManager.KEY_SELECTED_PICTURE:
                loadPicture();
                break;
        }
    }
}
