package com.alph.storyapp.api

import com.alph.storyapp.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("login")
    fun loginUser(@Body login: Login): Call<LoginResponse>

    @POST("register")
    fun registerUser(@Body register: Register): Call<FileUploadResponse>

    @GET("stories")
    fun getStories(@Header("Authorization") token: String): Call<StoryResponse>

    @GET("stories")
    fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1
    ): Call<StoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): Call<FileUploadResponse>

}