package com.alph.storyapp.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alph.storyapp.data.FileUploadResponse
import com.alph.storyapp.data.Register
import com.alph.storyapp.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _response = MutableLiveData<FileUploadResponse>()
    val response: LiveData<FileUploadResponse> get() = _response

    suspend fun register(registerBody: Register){
        Log.d("login register", registerBody.toString())
        _response.value = userRepository.registerUser(registerBody)
    }
}