package kyklab.burner2.utils;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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

    public static void setKeepScreenOn(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void unsetKeepScreenOn(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void setMaxBrightness(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        window.setAttributes(params);
    }

    public static void resetBrightness(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        window.setAttributes(params);
    }
}
