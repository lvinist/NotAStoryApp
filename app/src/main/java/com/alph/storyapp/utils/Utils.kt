package com.alph.storyapp.utils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.alph.storyapp.R
import com.alph.storyapp.utils.Constant.FILENAME_FORMAT
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object utils{

    fun String.isValidEmail(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()

    val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    fun createFile(application: Application): File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        val outputDirectory = if (
            mediaDir != null && mediaDir.exists()
        ) mediaDir else application.filesDir

        return File(outputDirectory, "$timeStamp.jpg")
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver : ContentResolver = context.contentResolver
        val myFile = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream : OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len : Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }


    @JvmStatic
    fun File.compressImage(): File {
        val bitmap = BitmapFactory.decodeFile(path)
        var streamLength: Int
        var compressQuality = 100
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))
        return this
    }

    @JvmStatic
    fun File.toRequestBody(): RequestBody =
        this.compressImage().asRequestBody("image/jpeg".toMediaTypeOrNull())

    @JvmStatic
    fun File.toMultipart(): MultipartBody.Part {
        val imageRequestBody = this.toRequestBody()
        return MultipartBody.Part.createFormData(
            "photo",
            this.name,
            imageRequestBody
        )
    }

    @JvmStatic
    fun String.toMultipart(): MultipartBody.Part =
        MultipartBody.Part.createFormData("description", this)

}

@BindingAdapter("load_image")
fun loadImage(view: ImageView, any: Any?) {
    any?.let { Glide.with(view).load(it).into(view) }
}



