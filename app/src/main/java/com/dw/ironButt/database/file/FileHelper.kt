package com.dw.ironButt.database.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class FileHelper(private val context: Context) {
    val mDirImages: File = context.getFileStreamPath("Images")
    private val mDirImagesTmp: File = context.getFileStreamPath("Tmp")
    private var latestTmpUri: Uri = getTmpFileUri()


    fun getTmpFileUri(): Uri {
        if (!mDirImagesTmp.exists()) mDirImagesTmp.mkdirs()
        val fileName = "tmp.jpg"
        val tmpFile = File(mDirImagesTmp, fileName).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(context, "com.dw.ironButt.provider", tmpFile)
    }

    fun saveImage(name: String, result: (Boolean) -> Unit) {
        uriToBitmap(latestTmpUri) { originalBitmap ->
            val width: Int = originalBitmap.width
            val height: Int = originalBitmap.height
            val halfWidth = width / 3
            val halfHeight = height / 3
            val newBitmap = Bitmap.createScaledBitmap(originalBitmap, halfWidth, halfHeight, false)

            val dateFormat: DateFormat = SimpleDateFormat("yyyy:MM:HH:mm", Locale.getDefault())
            val fileName = name + "-" + dateFormat.format(System.currentTimeMillis()) + ".jpg"
            val file = File(mDirImages, fileName)
            if (file.exists()) file.delete()
            checkDir()
            file.createNewFile()
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(file)
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                result(true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    out?.close()

                } catch (e: Exception) {
                    e.printStackTrace()
                    result(false)
                }
            }

        }
    }

    private fun uriToBitmap(uri: Uri, bitmap: (Bitmap) -> Unit) {
        try {
            bitmap(
                if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteAllImages() {
        checkDir()
        val listFile = mDirImages.list()
        listFile.forEach { name ->
            File(mDirImages, name).delete()
        }

    }

    private fun checkDir() {
        if (!mDirImages.exists()) mDirImages.mkdirs()

    }


}