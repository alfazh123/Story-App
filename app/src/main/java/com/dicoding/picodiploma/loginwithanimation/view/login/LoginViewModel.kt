package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    val error: LiveData<Boolean> = repository.error

    suspend fun register(name: String, email: String, password: String) = repository.register(name, email, password)

    suspend fun login(email: String, password: String) = repository.login(email, password)
}