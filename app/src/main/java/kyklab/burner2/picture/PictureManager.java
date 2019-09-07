package kyklab.burner2.picture;

import android.net.Uri;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kyklab.burner2.App;
import kyklab.burner2.R;
import kyklab.burner2.utils.FMUtils;
import kyklab.burner2.utils.PrefManager;

public class PictureManager {
    public static final String CUSTOM_PICTURE_FILENAME = "custom.jpg";
    public static final String CUSTOM_PICTURE_PATH =
            App.getContext().getFilesDir().getPath() + File.separator + CUSTOM_PICTURE_FILENAME;
    private static final List<PictureItem> builtInPictures =
            Arrays.<PictureItem>asList(
                    new PictureItem<>(R.string.picture_1, R.drawable.pic_1, R.drawable.pic_1_thumbnail, null),
                    new PictureItem<>(R.string.picture_2, R.drawable.pic_2, R.drawable.pic_2_thumbnail, null),
                    new PictureItem<>(R.string.picture_3, R.drawable.pic_3, R.drawable.pic_3_thumbnail, null),
                    new PictureItem<>(R.string.picture_4, R.drawable.pic_4, R.drawable.pic_4_thumbnail, null),
                    new PictureItem<>(R.string.picture_5, R.drawable.pic_5, R.drawable.pic_5_thumbnail, null),
                    new PictureItem<>(R.string.picture_6, R.drawable.pic_6, R.drawable.pic_6_thumbnail, null));
    private static final Runnable clearDiskCacheRunnable = new Runnable() {
        @Override
        public void run() {
            Glide.get(App.getContext()).clearDiskCache();
        }
    };
    private static List<PictureItem> pictures;
    private final String TAG = "PictureManager";

    private PictureManager() {
        pictures = new ArrayList<>();
    }

    public static PictureManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static void forceImageReload() {
        int curIndex = PrefManager.getInstance().getSelectedPictureIndex();
        forceImageReload(curIndex);
    }

    public static void forceImageReload(int pictureIndex) {
        PrefManager.getInstance().removePref(PrefManager.KEY_SELECTED_PICTURE_INDEX);
        PrefManager.getInstance().setSelectedPictureIndex(pictureIndex);
    }

    public static void clearImageCache() {
        Glide.get(App.getContext()).clearMemory();
        new Thread(clearDiskCacheRunnable).start();
    }

    public boolean customPictureExists() {
        return new File(CUSTOM_PICTURE_PATH).exists();
    }

    public List<PictureItem> getPictureList() {
        return pictures;
    }

    public void updatePictureList() {
        List<PictureItem> temp = new ArrayList<>();
        if (customPictureExists()) {
            temp.add(new PictureItem<>(R.string.picture_custom, CUSTOM_PICTURE_PATH, null,
                    PrefManager.getInstance().getPicLastUpdatedTime()));
        }
        temp.addAll(builtInPictures);

        pictures.clear();
        pictures.addAll(temp);
    }

    public void setAsCustomPicture(Object picture) {
        try {
            if (picture instanceof String) {
                FMUtils.copy((String) picture, CUSTOM_PICTURE_PATH);
            } else if (picture instanceof Uri) {
                FMUtils.copy(App.getContext(), (Uri) picture, CUSTOM_PICTURE_PATH);
            } else {
                Toast.makeText(App.getContext(), "Unsupported picture parameter type", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(App.getContext(), "IO error while setting picture", Toast.LENGTH_SHORT).show();
            return;
        }

        PrefManager.getInstance().setPicLastUpdatedTime(System.currentTimeMillis());
        forceImageReload(0);

        updatePictureList();
    }

    private static class LazyHolder {
        static final PictureManager INSTANCE = new PictureManager();
    }
}
