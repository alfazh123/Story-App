package com.dicoding.picodiploma.loginwithanimation.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.AddNewStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.GetAllStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.GetDetailStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    private var _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun getAllStories() : LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).liveData
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

    // Add story without coroutine

    private val resultLocationsStories = MutableLiveData<Result<GetAllStoriesResponse>>()

    suspend fun getLocationsStories(): LiveData<Result<GetAllStoriesResponse>> {
        resultLocationsStories.value = Result.Loading

        try {
            val response = apiService.getStories()
            resultLocationsStories.value = Result.Success(response)
            _error.value = false
            resultLocationsStories.postValue(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, GetAllStoriesResponse::class.java)
            val errorMessage = errorBody.message
            resultLocationsStories.value = Result.Error(errorMessage)
            _error.value = true
        }

        return resultLocationsStories
    }

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

    // Add Story using coroutine

    fun addNewStory(imageFile: File, description: String, lat: Float?, long: Float?)= liveData(Dispatchers.IO){
        emit(Result.Loading)

        try {
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            val successResponse = apiService.addStory(multipartBody, requestBody, lat, long)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: IOException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: SocketTimeoutException) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository = UserRepository(apiService, userPreference)
    }
}