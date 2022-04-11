package com.alph.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.alph.storyapp.api.ApiConfig
import com.alph.storyapp.data.Login
import com.alph.storyapp.data.LoginResponse
import com.alph.storyapp.data.User
import com.alph.storyapp.storage.UserPreference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    fun saveUser(user: User) {
        viewModelScope.launch {
            pref.saveUser(User(user.userId, user.name, user.token, user.isLogin))
        }
    }

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }
}