package com.alph.storyapp.data

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class FileUploadResponse(
    @Json(name = "error")
    val error: Boolean? = false,
    @Json(name = "message")
    val message: String? = ""
) : Parcelable
