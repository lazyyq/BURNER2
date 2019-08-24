package kyklab.burner2.utils;

import android.app.Activity;
import android.view.View;
import android.view.Window;

public class ScreenUtils {
    public static final int fullscreenOpts =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    public static void hideSystemUi(Activity activity) {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();

        int uiOptions = decorView.getSystemUiVisibility();
        uiOptions |= fullscreenOpts;

        decorView.setSystemUiVisibility(uiOptions);
    }

    public static void showSystemUi(Activity activity) {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();

        int uiOptions = decorView.getSystemUiVisibility();
        uiOptions &= ~fullscreenOpts;

        decorView.setSystemUiVisibility(uiOptions);
    }
}
