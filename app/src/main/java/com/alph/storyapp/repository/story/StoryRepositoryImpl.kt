package com.alph.storyapp.repository.story

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.alph.storyapp.api.ApiService
import com.alph.storyapp.data.FileUploadResponse
import com.alph.storyapp.data.Story
import com.alph.storyapp.data.StoryResponse
import com.alph.storyapp.data.paging.StoryPagingSource
import com.alph.storyapp.utils.Constant.NETWORK_LOAD_SIZE
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : StoryRepository {

    override suspend fun addNewStoryWithToken(
        description: MultipartBody.Part,
        image: MultipartBody.Part,
        token: String
    ): FileUploadResponse =
        try {
            val response = apiService.uploadImage(image, description, token)
            response
        } catch (e: Exception) {
            FileUploadResponse(error = true, e.localizedMessage.toString())
        }


    override suspend fun getStoriesWithTokenAndLocation(token: String): StoryResponse<Story> =
        try {
            val response = apiService.getStoriesWithLocation(token = "Bearer $token")
            response
        } catch (e: Exception) {
            StoryResponse(error = true, message = e.localizedMessage.toString(), emptyList())
        }

    override fun getAllStoriesWithToken(token: String): Flow<PagingData<Story>> = Pager(
        config = PagingConfig(pageSize = NETWORK_LOAD_SIZE, enablePlaceholders = false),
        pagingSourceFactory = {
            StoryPagingSource(
                apiService = apiService,
                token = "Bearer $token"
            )
        }
    ).flow
}