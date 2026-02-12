package com.example.trashbinproject.data.network

import com.example.trashbinproject.domain.CanScanRequest
import com.example.trashbinproject.domain.CanScanResponse
import com.example.trashbinproject.domain.ClassificationResult
import com.example.trashbinproject.domain.MarkScannedRequest
import com.example.trashbinproject.domain.PointsUpdateRequest
import com.example.trashbinproject.domain.RefreshRequest
import com.example.trashbinproject.domain.TokenResponse
import com.example.trashbinproject.domain.TrashBinResponse
import com.example.trashbinproject.domain.UserCreateRequest
import com.example.trashbinproject.domain.UserLoginRequest
import com.example.trashbinproject.domain.UserResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

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

    @POST("refresh")
    fun refresh(@Body body: RefreshRequest): Call<TokenResponse>

    @GET("me")
    suspend fun me(): UserResponse

    @PATCH("me/points")
    suspend fun updatePoints(@Body body: PointsUpdateRequest): UserResponse

    @GET("trash-bins/nearby-trash-bins")
    suspend fun getNearbyTrashBins(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Int = 1000
    ): List<TrashBinResponse>

    @POST("trash-bins/bins/can-scan")
    suspend fun canScanBin(@Body request: CanScanRequest): CanScanResponse

    @POST("trash-bins/bins/mark-scanned")
    suspend fun markBinScanned(@Body request: MarkScannedRequest): Response<Unit>
}