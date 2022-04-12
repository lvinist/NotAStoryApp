package com.alph.storyapp.data

data class StoryResponse(
    val error: Boolean,
    val message: String,
    val listStory: ArrayList<Story>
)
