package kyklab.burner2.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FMUtils {
    private static final String TAG = "FMUtils";

    public static void copy(Context context, Object src, Object dst) {
        InputStream is = null;
        OutputStream os = null;
        try {
            if (src instanceof Uri) {
                is = context.getContentResolver().openInputStream((Uri) src);
            } else if (src instanceof String) {
                is = new FileInputStream((String) src);
            } else {
                Log.e(TAG, "Wrong parameter type");
            }
            if (is == null) {
                Log.e(TAG, "InputStream is null");
                return;
            }

            if (dst instanceof Uri) {
                os = context.getContentResolver().openOutputStream((Uri) dst);
            } else if (dst instanceof String) {
                os = new FileOutputStream((String) dst);
            } else {
                Log.e(TAG, "Wrong parameter type");
            }
            if (os == null) {
                Log.e(TAG, "OutputStream is null");
                return;
            }

            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while copying picture");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
