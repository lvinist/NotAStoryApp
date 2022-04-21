package com.alph.storyapp.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alph.storyapp.api.ApiConfig
import com.alph.storyapp.data.Story
import com.alph.storyapp.data.StoryResponse
import com.alph.storyapp.data.User
import com.alph.storyapp.storage.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapStoryViewModel(private val pref: UserPreference): ViewModel() {
    val listStory = MutableLiveData<ArrayList<Story>?>()

    fun setStoriesWithLocation(tokenAuth: String) {
        Log.d(this@MapStoryViewModel::class.java.simpleName, tokenAuth)
        ApiConfig().getApiService().getStoriesWithLocation(token = "Bearer $tokenAuth")
            .enqueue(object : Callback<StoryResponse> {
                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    listStory.postValue(null)
                }
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    if (response.code() == 200) {
                        Log.d(this@MapStoryViewModel::class.java.simpleName, response.body().toString())
                        listStory.postValue(response.body()?.listStory)
                    } else {
                        listStory.postValue(null)
                    }
                }

            })
    }

    fun getStoriesWithLocation(): MutableLiveData<ArrayList<Story>?> {
        return listStory
    }

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

}