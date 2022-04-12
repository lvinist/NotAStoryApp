package com.alph.storyapp.api

import com.alph.storyapp.data.Login
import com.alph.storyapp.data.LoginResponse
import com.alph.storyapp.data.StoryResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("login")
    fun loginUser(@Body login: Login): Call<LoginResponse>

    @GET("stories")
    fun getStories(@Header("Authorization") token: String): Call<StoryResponse>

}