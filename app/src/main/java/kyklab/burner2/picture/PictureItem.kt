package kyklab.burner2.picture

import androidx.annotation.StringRes
import kyklab.burner2.BuildConfig
import java.util.*

class PictureItem<T, S>(@param:StringRes var name: Int, var picture: T?, var thumbnail: S?, private var versionMetadata: Any?) {

    fun getVersionMetadata(): Any {
        return if (versionMetadata != null) versionMetadata else BuildConfig.VERSION_CODE
    }

    fun setVersionMetadata(versionMetadata: Any) {
        this.versionMetadata = versionMetadata
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as PictureItem<*, *>?
        return name == that!!.name &&
                picture == that.picture &&
                thumbnail == that.thumbnail &&
                versionMetadata == that.versionMetadata
    }

    override fun hashCode(): Int {
        return Objects.hash(name, picture, thumbnail, versionMetadata)
    }
}
