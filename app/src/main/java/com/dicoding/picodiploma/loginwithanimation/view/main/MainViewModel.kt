package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return repository.getAllStories().cachedIn(viewModelScope)
    }

//    val stories: LiveData<PagingData<ListStoryItem>> = repository.getAllStories().cachedIn(viewModelScope)

    suspend fun getDetailStorybyId(id: String) = repository.getDetailStories(id)

}