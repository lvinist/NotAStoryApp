package com.alph.storyapp.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.alph.storyapp.api.ApiConfig
import com.alph.storyapp.data.Story
import com.alph.storyapp.data.StoryResponse
import com.alph.storyapp.data.User
import com.alph.storyapp.storage.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {

    val listStory = MutableLiveData<ArrayList<Story>?>()

    fun setStories(tokenAuth: String) {
        Log.d(this@MainViewModel::class.java.simpleName, tokenAuth)
        ApiConfig().getApiService().getStories(token = "Bearer $tokenAuth")
            .enqueue(object : Callback<StoryResponse> {
                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    listStory.postValue(null)
                }
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    if (response.code() == 200) {
                        listStory.postValue(response.body()?.listStory)
                    } else {
                        listStory.postValue(null)
                    }
                }

            })
    }

    fun getStories(): MutableLiveData<ArrayList<Story>?> {
        return listStory
    }

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}