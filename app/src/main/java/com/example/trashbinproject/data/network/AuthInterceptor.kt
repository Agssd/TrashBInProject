package com.example.trashbinproject.data.network

import com.example.trashbinproject.data.storage.TokenManager
import android.util.Log
import com.example.trashbinproject.data.storage.AuthStorage
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = tokenManager.currentToken ?: AuthStorage.token
        Log.d("AuthInterceptor", "Token used for request: $token")

        val requestWithAuth = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build().also {
                    Log.d("AuthInterceptor", "Added Authorization header for ${originalRequest.url}")
                }
        } else {
            Log.d("AuthInterceptor", "No auth token available for request: ${originalRequest.url}")
            originalRequest
        }

        return chain.proceed(requestWithAuth)
    }
}
