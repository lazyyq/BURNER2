package kyklab.burner2;

import android.app.Application;
import android.content.Context;
import android.util.Log;

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


    private static long s1;

    public static void start() {
        s1 = System.currentTimeMillis();
    }

    public static void end(String msg) {
        long s2 = System.currentTimeMillis();
        Log.e(msg, "Took: " + (s2 - s1));
    }
}
