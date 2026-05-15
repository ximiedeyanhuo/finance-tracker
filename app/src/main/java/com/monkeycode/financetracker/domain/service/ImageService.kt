package com.monkeycode.financetracker.domain.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val imagesDir: File by lazy {
        File(context.filesDir, "images").also { it.mkdirs() }
    }

    fun saveImage(bitmap: Bitmap, compressQuality: Int = 85): Result<String> {
        return try {
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"
            val file = File(imagesDir, fileName)
            
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, out)
            }
            
            Result.success(file.absolutePath)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    fun loadImage(imagePath: String): Bitmap? {
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                BitmapFactory.decodeFile(imagePath)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun deleteImage(imagePath: String): Boolean {
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getImageFile(imagePath: String): File? {
        val file = File(imagePath)
        return if (file.exists() && file.isFile) file else null
    }
}
