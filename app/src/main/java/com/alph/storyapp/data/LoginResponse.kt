package com.alph.storyapp.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "error")
    val error: Boolean? = true,
    @Json(name = "message")
    val message: String? = "",
    @Json(name = "loginResult")
    val loginResult: User? = User())
