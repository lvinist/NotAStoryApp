package com.alph.storyapp.repository.user

import com.alph.storyapp.data.FileUploadResponse
import com.alph.storyapp.data.Login
import com.alph.storyapp.data.LoginResponse
import com.alph.storyapp.data.Register
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun registerUser(registerBody: Register): FileUploadResponse

    suspend fun loginUser(loginBody: Login): LoginResponse

    suspend fun logoutUser()

    val userToken: Flow<String>

    suspend fun getUserToken(): String

    suspend fun setUserToken(token: String)
}