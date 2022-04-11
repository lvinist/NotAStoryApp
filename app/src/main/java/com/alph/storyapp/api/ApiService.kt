package com.alph.storyapp.api

import com.alph.storyapp.data.Login
import com.alph.storyapp.data.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    fun loginUser(@Body login: Login): Call<LoginResponse>
}