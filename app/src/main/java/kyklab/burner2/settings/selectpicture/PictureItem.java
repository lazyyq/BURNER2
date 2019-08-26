package kyklab.burner2.settings.selectpicture;

import androidx.annotation.Nullable;

public class PictureItem {
    private String name;
    private int resId;

    public PictureItem(String name, int resId) {
        this.name = name;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof PictureItem) {
            PictureItem target = (PictureItem) obj;
            return this.name.equals(target.name)
                    && this.resId == target.resId;
        } else {
            return false;
        }
    }
}
