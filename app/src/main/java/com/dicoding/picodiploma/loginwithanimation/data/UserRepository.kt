package com.dicoding.picodiploma.loginwithanimation.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.AddNewStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.GetAllStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.GetDetailStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    private var _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    private val resultAllStories = MutableLiveData<Result<GetAllStoriesResponse>>()

    suspend fun getAllStories() : LiveData<Result<GetAllStoriesResponse>> {
        resultAllStories.value = Result.Loading
        try {
            resultAllStories.value = Result.Loading
            val response = apiService.getStories()
            resultAllStories.value = Result.Success(response)
            _error.value = false
            resultAllStories.postValue(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, GetAllStoriesResponse::class.java)
            val errorMessage = errorBody.message
            resultAllStories.value = Result.Error(errorMessage)
            _error.value = true
        }

        return resultAllStories
    }

    private val resultDetail = MutableLiveData<Result<GetDetailStoriesResponse>>()

    suspend fun getDetailStories(id: String) : LiveData<Result<GetDetailStoriesResponse>> {
        resultDetail.value = Result.Loading
        try {
            val response = apiService.getStorybyId(id)
            resultDetail.value = Result.Success(response)
            _error.value = true
            resultDetail.postValue(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, GetDetailStoriesResponse::class.java)
            val errorMessage = errorBody.message
            resultDetail.value = Result.Error(errorMessage)
            _error.value = true
        }

        return resultDetail
    }

    private val resultAddStory = MutableLiveData<Result<AddNewStoryResponse>>()

    suspend fun addStory(imageFile: File, description: String) : LiveData<Result<AddNewStoryResponse>> {
        resultAddStory.value = Result.Loading

        try {
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            val successResponse = apiService.addStory(multipartBody, requestBody)
            _error.value = false
            resultAddStory.postValue(Result.Success(successResponse))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, AddNewStoryResponse::class.java)
            val errorMessage = errorBody.message
            resultDetail.value = Result.Error(errorMessage)
            _error.value = true
        }

        return resultAddStory
    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository = UserRepository(apiService, userPreference)
    }
}