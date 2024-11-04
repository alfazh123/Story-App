package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import java.io.File

class AddStoryViewModel(private val repository: UserRepository): ViewModel() {
    suspend fun addStory(imageFile: File, description: String) = repository.addStory(imageFile, description)
}