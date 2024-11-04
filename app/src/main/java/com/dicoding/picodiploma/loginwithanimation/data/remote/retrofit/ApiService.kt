package com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit

import com.dicoding.picodiploma.loginwithanimation.data.remote.response.AddNewStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.GetAllStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.GetDetailStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): GetAllStoriesResponse

    @GET("stories/{id}")
    suspend fun getStorybyId(
        @Path("id") id: String
    ) : GetDetailStoriesResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ) : AddNewStoryResponse
}