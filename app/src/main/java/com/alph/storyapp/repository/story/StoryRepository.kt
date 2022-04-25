package com.alph.storyapp.repository.story

import androidx.paging.PagingData
import com.alph.storyapp.data.FileUploadResponse
import com.alph.storyapp.data.Story
import com.alph.storyapp.data.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface StoryRepository {

    suspend fun addNewStoryWithToken(
        description: MultipartBody.Part,
        image: MultipartBody.Part,
        token: String
    ): FileUploadResponse

    suspend fun getStoriesWithTokenAndLocation(token: String): StoryResponse<Story>

    fun getAllStoriesWithToken(token: String): Flow<PagingData<Story>>
}