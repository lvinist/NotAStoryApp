package com.alph.storyapp.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.alph.storyapp.data.Story
import com.alph.storyapp.repository.story.StoryRepository
import com.alph.storyapp.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _stories = MutableStateFlow<PagingData<Story>>(PagingData.empty())
    val stories = _stories.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            userRepository.userToken.collect { token ->
                storyRepository.getAllStoriesWithToken(token).collect {
                    _stories.value = it
                }
            }
        }
    }

    fun getLatestStories() {
        viewModelScope.launch {
            userRepository.userToken.collect {token ->
                storyRepository.getAllStoriesWithToken(token).collect {
                    _stories.value = it
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logoutUser()
        }
    }
}