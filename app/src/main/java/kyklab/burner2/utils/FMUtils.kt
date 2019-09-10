package kyklab.burner2.utils

import android.content.Context
import android.net.Uri
import java.io.*

object FMUtils {
    private val TAG = "FMUtils"

    @Throws(IOException::class)
    fun copy(inputStream: InputStream, outputStream: OutputStream) {
        val buf = ByteArray(1024)
        var len: Int
        while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len)
        }
    }

    @Throws(IOException::class)
    fun copy(srcPath: String, destPath: String) {
        try {
            FileInputStream(srcPath).use { `is` -> FileOutputStream(destPath).use { os -> copy(`is`, os) } }
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }

    }

    @Throws(IOException::class)
    fun copy(context: Context, srcUri: Uri, destPath: String) {
        try {
            context.contentResolver.openInputStream(srcUri)!!.use { `is` ->
                FileOutputStream(destPath).use { os ->
                    if (`is` != null) {
                        copy(`is`, os)
                    } else {
                        throw IOException("InputStream is null")
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }

    }
}
