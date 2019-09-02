package kyklab.burner2.selectpicture;

import java.util.Objects;

public class PictureItem<T, M> {
    private String name;
    private T picture;
    private M metadata;

    public PictureItem(String name, T picture) {
        this.name = name;
        this.picture = picture;
        this.metadata = null;
    }

    public PictureItem(String name, T picture, M metadata) {
        this.name = name;
        this.picture = picture;
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
        PictureItem<?, ?> that = (PictureItem<?, ?>) o;
        return name.equals(that.name) &&
                picture.equals(that.picture) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, picture, metadata);
    }
}
