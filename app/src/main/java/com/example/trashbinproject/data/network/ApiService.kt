package com.example.trashbinproject.data.network

import com.example.trashbinproject.domain.ClassificationResult
import com.example.trashbinproject.domain.PointsUpdateRequest
import com.example.trashbinproject.domain.TokenResponse
import com.example.trashbinproject.domain.UserCreateRequest
import com.example.trashbinproject.domain.UserLoginRequest
import com.example.trashbinproject.domain.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("classify")
    suspend fun predictImage(
        @Part image: MultipartBody.Part
    ): Response<List<ClassificationResult>>

    @POST("register")
    suspend fun register(@Body body: UserCreateRequest): TokenResponse

    @POST("login")
    suspend fun login(@Body body: UserLoginRequest): TokenResponse

    @GET("me")
    suspend fun me(): UserResponse

    @PATCH("me/points")
    suspend fun updatePoints(@Body body: PointsUpdateRequest): UserResponse
}