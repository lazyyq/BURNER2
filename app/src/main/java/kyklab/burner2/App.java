package kyklab.burner2;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static final String TAG = "App";
    private static Application application;

    public static Context getContext() {
        return application.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
