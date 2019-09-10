package kyklab.burner2.picture

import android.net.Uri
import android.widget.Toast
import com.bumptech.glide.Glide
import kyklab.burner2.App
import kyklab.burner2.R
import kyklab.burner2.utils.FMUtils
import kyklab.burner2.utils.PrefManager
import java.io.File
import java.io.IOException
import java.util.*

class PictureManager private constructor() {

    val pictureList: List<PictureItem<*, *>>
        get() = pictures

    init {
        pictures = ArrayList<PictureItem>()
    }

    fun customPictureExists(): Boolean {
        return File(CUSTOM_PICTURE_PATH).exists()
    }

    fun updatePictureList() {
        val temp = ArrayList<PictureItem<*, *>>()
        if (customPictureExists()) {
            temp.add(PictureItem<String, Any>(R.string.picture_custom, CUSTOM_PICTURE_PATH, null,
                    PrefManager.instance.picLastUpdatedTime))
        }
        temp.addAll(builtInPictures)

        pictures.clear()
        pictures.addAll(temp)
    }

    fun setAsCustomPicture(picture: Any) {
        try {
            if (picture is String) {
                FMUtils.copy(picture, CUSTOM_PICTURE_PATH)
            } else if (picture is Uri) {
                FMUtils.copy(App.context, picture, CUSTOM_PICTURE_PATH)
            } else {
                Toast.makeText(App.context, "Unsupported picture parameter type", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(App.context, "IO error while setting picture", Toast.LENGTH_SHORT).show()
            return
        }

        PrefManager.instance.picLastUpdatedTime = System.currentTimeMillis()
        forceImageReload(0)

        updatePictureList()
    }

    private object LazyHolder {
        internal val INSTANCE = PictureManager()
    }

    companion object {
        val CUSTOM_PICTURE_FILENAME = "custom.jpg"
        val CUSTOM_PICTURE_PATH =
                App.context.getFilesDir().getPath() + File.separator + CUSTOM_PICTURE_FILENAME
        private val TAG = "PictureManager"
        private val builtInPictures = Arrays.asList<PictureItem<*, *>>(
                PictureItem(R.string.picture_1, R.drawable.pic_1, R.drawable.pic_1_thumbnail, null),
                PictureItem(R.string.picture_2, R.drawable.pic_2, R.drawable.pic_2_thumbnail, null),
                PictureItem(R.string.picture_3, R.drawable.pic_3, R.drawable.pic_3_thumbnail, null),
                PictureItem(R.string.picture_4, R.drawable.pic_4, R.drawable.pic_4_thumbnail, null),
                PictureItem(R.string.picture_5, R.drawable.pic_5, R.drawable.pic_5_thumbnail, null),
                PictureItem(R.string.picture_6, R.drawable.pic_6, R.drawable.pic_6_thumbnail, null))
        private val clearDiskCacheRunnable = Runnable { Glide.get(App.context).clearDiskCache() }
        private var pictures: MutableList<PictureItem<*, *>>

        val instance: PictureManager
            get() = LazyHolder.INSTANCE

        fun forceImageReload() {
            val curIndex = PrefManager.instance.selectedPictureIndex
            forceImageReload(curIndex)
        }

        fun forceImageReload(pictureIndex: Int) {
            PrefManager.instance.removePref(PrefManager.Key.SELECTED_PICTURE_INDEX)
            PrefManager.instance.selectedPictureIndex = pictureIndex
        }

        fun clearImageCache() {
            Glide.get(App.context).clearMemory()
            Thread(clearDiskCacheRunnable).start()
        }
    }
}
