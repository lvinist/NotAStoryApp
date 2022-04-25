package com.alph.storyapp.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoryResponse<T>(
    @Json(name = "error")
    val error: Boolean? = false,
    @Json(name = "message")
    val message: String? = "",
    @Json(name = "listStory")
    val listStory: List<T>? = listOf()
)
