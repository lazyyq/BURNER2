package kyklab.burner2.utils;

import java.io.File;

public class FMUtils {

    public static boolean isDirectory(String path) {
        return new File(path).isDirectory();
    }

}
