package com.alph.storyapp.api

import com.alph.storyapp.data.*
import com.alph.storyapp.utils.Constant.NETWORK_LOAD_SIZE
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("login")
    suspend fun loginUser(@Body loginBody: Login): LoginResponse

    @POST("register")
    suspend fun registerUser(@Body registerBody: Register): FileUploadResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = NETWORK_LOAD_SIZE,
        @Header("Authorization") token: String
    ): StoryResponse<Story>

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = NETWORK_LOAD_SIZE,
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1
    ): StoryResponse<Story>

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Header("Authorization") token: String
    ): FileUploadResponse

}