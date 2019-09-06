package kyklab.burner2.picture;

import androidx.annotation.Nullable;

import java.util.Objects;

public class PictureItem<T, S, M> {
    private String name;
    private T picture;
    private S thumbnail;
    private M metadata;

    public PictureItem(String name, T picture, @Nullable S thumbnail, @Nullable M metadata) {
        this.name = name;
        this.picture = picture;
        this.thumbnail = thumbnail;
        this.metadata = metadata;
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

    public S getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(S thumbnail) {
        this.thumbnail = thumbnail;
    }

    public M getMetadata() {
        return metadata;
    }

    public void setMetadata(M metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PictureItem<?, ?, ?> that = (PictureItem<?, ?, ?>) o;
        return name.equals(that.name) &&
                picture.equals(that.picture) &&
                Objects.equals(thumbnail, that.thumbnail) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, picture, thumbnail, metadata);
    }
}
