package com.alph.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alph.storyapp.data.Story
import com.alph.storyapp.repository.story.StoryRepository
import com.alph.storyapp.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapStoryViewModel @Inject constructor(private val storyRepository: StoryRepository, private val userRepository: UserRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> get() = _stories

    init {
        viewModelScope.launch {
            val token = userRepository.getUserToken()
            val response = storyRepository.getStoriesWithTokenAndLocation(token)
            _stories.value = response.listStory.orEmpty()
        }
    }
}