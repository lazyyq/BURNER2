package kyklab.burner2.fm;

interface FMAdapterCallback {
    void gotoUpperDirectory();

    void enterDirectory(String dir);

    void customPictureSelected(Object obj);
}
