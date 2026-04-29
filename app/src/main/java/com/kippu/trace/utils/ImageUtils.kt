package com.kippu.trace.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.OutputStream

object ImageUtils {
    /**
     * 将 ImageBitmap 保存到系统相册
     */
    fun saveBitmapToGallery(context: Context, imageBitmap: ImageBitmap, fileName: String): Boolean {
        val bitmap = imageBitmap.asAndroidBitmap()
        val contentResolver = context.contentResolver
        
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TimeTrace")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val uri = contentResolver.insert(contentUri, imageDetails) ?: return false

        return try {
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageDetails.clear()
                imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, imageDetails, null, null)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            contentResolver.delete(uri, null, null)
            false
        }
    }
}
