package com.example.trashbinproject.data.network

import com.example.trashbinproject.data.storage.TokenManager
import android.util.Log
import com.example.trashbinproject.data.storage.AuthStorage
import com.example.trashbinproject.data.storage.TokenAuthenticator
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    // ✅ TokenAuthenticator создаём ЗДЕСЬ!
    private val tokenAuthenticator = TokenAuthenticator(
        tokenManager = tokenManager,
        retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080") // простой retrofit!
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    )
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = runBlocking { tokenManager.getAccessToken() }

        return if (token != null) {
            val req = original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(req)
        } else {
            chain.proceed(original)
        }
    }
}