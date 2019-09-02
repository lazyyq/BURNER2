package kyklab.burner2;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kyklab.burner2.selectpicture.PictureItem;

public class App extends Application {
    private static final String TAG = "App";
    private static final String CUSTOM_PICTURE_FILENAME = "custom.jpg";
    private static Application application;
    private static String customPicturePath;
    private static List<PictureItem> pictureList;

    public static Context getContext() {
        return application.getApplicationContext();
    }

    public static List<PictureItem> getPictureList() {
        return pictureList;
    }

    public static String getCustomPicturePath() {
        return customPicturePath;
    }

    public static boolean customPictureExists() {
        return new File(customPicturePath).exists();
    }

    public static void updatePictureList() {
        pictureList.clear();

        if (customPictureExists()) {
            // Custom picture exists, add to list
            Log.e(TAG, "exists");
            pictureList.add(new PictureItem<>("User picture", customPicturePath, System.currentTimeMillis()));
        }
        pictureList.addAll(Arrays.asList(
                new PictureItem<>("Picture 1", R.drawable.pic1),
                new PictureItem<>("Picture 2", R.drawable.pic2),
                new PictureItem<>("Picture 3", R.drawable.pic3),
                new PictureItem<>("Picture 4", R.drawable.pic4),
                new PictureItem<>("Picture 5", R.drawable.pic5),
                new PictureItem<>("Picture 6", R.drawable.pic6)));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        pictureList = new ArrayList<>();
        customPicturePath = getContext().getFilesDir().getPath() + File.separator + CUSTOM_PICTURE_FILENAME;
    }
}
