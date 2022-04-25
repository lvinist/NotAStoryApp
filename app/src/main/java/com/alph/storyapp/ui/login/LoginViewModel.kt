package com.alph.storyapp.ui.login


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alph.storyapp.data.Login
import com.alph.storyapp.data.LoginResponse
import com.alph.storyapp.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val loginResponse: LiveData<LoginResponse> get() = _loginResponse
    private val _loginResponse = MutableLiveData<LoginResponse>()

    val loginToken = userRepository.userToken

    fun login(loginBody: Login) {
        Log.d("login", loginBody.toString())
        viewModelScope.launch {
            _loginResponse.postValue(userRepository.loginUser(loginBody))
        }
    }

}