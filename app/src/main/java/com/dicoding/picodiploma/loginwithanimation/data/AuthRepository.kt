package com.dicoding.picodiploma.loginwithanimation.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){

    private val resultRegister = MutableLiveData<Result<RegisterResponse>>()

    private var _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    private suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun logout() {
        userPreference.logout()
    }


    // without coroutine

    suspend fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> {
        resultRegister.value = Result.Loading
        try {
            val response = apiService.register(name, email, password)
            resultRegister.value = Result.Success(response)
            _error.value = false
            resultRegister.postValue(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            val errorMessage = errorBody.message
            resultRegister.value = Result.Error(errorMessage)
            _error.value = true
        }
        return resultRegister
    }

    private val resultLogin = MutableLiveData<Result<LoginResponse>>()

    suspend fun login(email: String, password: String) : LiveData<Result<LoginResponse>> {
        resultLogin.value = Result.Loading
        try {
            resultLogin.value = Result.Loading
            val response = apiService.login(email, password)
            resultLogin.value = Result.Success(response)
            _error.value = false
            resultLogin.postValue(Result.Success(response))
            val user = UserModel(
                email,
                response.loginResult.token ?: "",
                response.loginResult.name
            )
            saveSession(user)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
            val errorMessage = errorBody.message
            resultLogin.value = Result.Error(errorMessage)
            _error.value = true
        }

        return resultLogin
    }


    // with coroutine

    fun loginUser(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
            val user = UserModel(
                email,
                response.loginResult.token,
                response.loginResult.name
            )
            saveSession(user)
        } catch (e: HttpException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: SocketTimeoutException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: IOException) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun registerUser(name: String, email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: SocketTimeoutException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: IOException) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): AuthRepository = AuthRepository(apiService, userPreference)
    }

}