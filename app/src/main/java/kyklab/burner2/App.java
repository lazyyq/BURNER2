package kyklab.burner2;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import kyklab.burner2.settings.selectpicture.PictureItem;

public class App extends Application {
    private static Application application;
    private static List<PictureItem> pictureList;

    public static List<PictureItem> getPictureList() {
        return pictureList;
    }

    public static Context getContext() {
        return application.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        pictureList = new ArrayList<>();
    }
}
