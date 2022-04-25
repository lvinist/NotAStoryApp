package com.alph.storyapp.repository.user

import com.alph.storyapp.api.ApiService
import com.alph.storyapp.data.*
import com.alph.storyapp.data.datastore.DatastoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DatastoreManager
) : UserRepository {

    private suspend fun addTokenToPreferences(loginToken: String) {
        dataStoreManager.updateLoginToken(loginToken)
    }

    override suspend fun registerUser(registerBody: Register): FileUploadResponse =
        try {
            val (name, email, password) = registerBody
            if (name!!.isEmpty() or email!!.isEmpty() or password!!.isEmpty())
                throw Exception()
            val response = apiService.registerUser(registerBody)
            response
        } catch (e: Exception) {
            FileUploadResponse(error = true, e.message.toString())
        }

    override suspend fun logoutUser() {
        dataStoreManager.clearUserToken()
    }

    override suspend fun loginUser(loginBody: Login): LoginResponse = try {
        val (email, password) = loginBody
        if (email!!.isEmpty() or password!!.isEmpty())
            throw Exception()
        val response = apiService.loginUser(loginBody)
        response.loginResult!!.token.let { addTokenToPreferences(it.toString()) }
        response
    } catch (e: Exception) {
        LoginResponse(error = true, message = e.localizedMessage as String, loginResult = User("","",""))
    }

    override val userToken: Flow<String> get() = dataStoreManager.preferenceLoginToken

    override suspend fun getUserToken(): String {
        val flowOfToken = dataStoreManager.preferenceLoginToken
        return flowOfToken.first()
    }

    override suspend fun setUserToken(token: String) {
        dataStoreManager.updateLoginToken(token)
    }
}