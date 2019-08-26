package kyklab.burner2.fm;

import android.graphics.drawable.Drawable;

public class FileItem {
    private Drawable icon;
    private String path;
    private String name;
    private boolean isDirectory;

    public FileItem(Drawable icon, String path, String name, boolean isDirectory) {
        this.icon = icon;
        this.path = path;
        this.name = name;
        this.isDirectory = isDirectory;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }
}
