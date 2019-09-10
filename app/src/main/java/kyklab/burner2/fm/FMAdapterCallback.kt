package kyklab.burner2.fm

internal interface FMAdapterCallback {
    fun gotoUpperDirectory()

    fun enterDirectory(dir: String)

    fun selectAsCustomPicture(obj: Any)
}
