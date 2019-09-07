package kyklab.burner2.picture;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.Objects;

import kyklab.burner2.BuildConfig;

public class PictureItem<T, S> {
    private int name;
    private T picture;
    private S thumbnail;
    private Object versionMetadata;

    public PictureItem(@StringRes int name, T picture, @Nullable S thumbnail, @Nullable Object versionMetadata) {
        this.name = name;
        this.picture = picture;
        this.thumbnail = thumbnail;
        this.versionMetadata = versionMetadata;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public T getPicture() {
        return picture;
    }

    public void setPicture(T picture) {
        this.picture = picture;
    }

    public S getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(S thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Object getVersionMetadata() {
        return versionMetadata != null ? versionMetadata : BuildConfig.VERSION_CODE;
    }

    public void setVersionMetadata(Object versionMetadata) {
        this.versionMetadata = versionMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PictureItem<?, ?> that = (PictureItem<?, ?>) o;
        return name == that.name &&
                picture.equals(that.picture) &&
                Objects.equals(thumbnail, that.thumbnail) &&
                Objects.equals(versionMetadata, that.versionMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, picture, thumbnail, versionMetadata);
    }
}
