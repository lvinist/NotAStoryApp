package com.alph.storyapp.ui.postimage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alph.storyapp.data.FileUploadResponse
import com.alph.storyapp.repository.story.StoryRepository
import com.alph.storyapp.repository.user.UserRepository
import com.alph.storyapp.utils.utils.toMultipart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class PostImageViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _image = MutableLiveData<File>()
    val image: LiveData<File> get() = _image

    private val _response = MutableLiveData<FileUploadResponse>()
    val addStoryResponse: LiveData<FileUploadResponse> get() = _response

    fun saveImage(image: File) {
        viewModelScope.launch {
            _image.postValue(image)
        }
    }

    fun addNewStoryWithToken(description: String, image: File) {
        viewModelScope.launch {
            val imageMultiPart = image.toMultipart()
            val descriptionMultipart = description.toMultipart()
            val token = userRepository.getUserToken()
            _response.value = storyRepository.addNewStoryWithToken(
                descriptionMultipart,
                imageMultiPart,
                "Bearer $token"
            )
            Log.d("post", _response.value.toString())
        }

    }
}