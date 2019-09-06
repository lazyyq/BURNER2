package kyklab.burner2.utils;

import android.content.Context;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FMUtils {
    private static final String TAG = "FMUtils";

    public static void copy(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
        }
    }

    public static void copy(String srcPath, String destPath) throws IOException {
        try (InputStream is = new FileInputStream(srcPath);
             OutputStream os = new FileOutputStream(destPath)) {
            copy(is, os);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void copy(Context context, Uri srcUri, String destPath) throws IOException {
        try (InputStream is = context.getContentResolver().openInputStream(srcUri);
             OutputStream os = new FileOutputStream(destPath)) {
            if (is != null) {
                copy(is, os);
            } else {
                throw new IOException("InputStream is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
