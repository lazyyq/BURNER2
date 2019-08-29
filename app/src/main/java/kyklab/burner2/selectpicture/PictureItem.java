package kyklab.burner2.selectpicture;

import androidx.annotation.Nullable;

public class PictureItem<T> {
    private String name;
    private T picture;

    public PictureItem(String name, T picture) {
        this.name = name;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getPicture() {
        return picture;
    }

    public void setPicture(T picture) {
        this.picture = picture;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof PictureItem) {
            PictureItem target = (PictureItem) obj;
            return this.name.equals(target.name)
                    && this.picture == target.picture;
        } else {
            return false;
        }
    }
}
