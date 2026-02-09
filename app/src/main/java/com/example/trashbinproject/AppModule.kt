package com.example.trashbinproject

import com.example.trashbinproject.data.storage.TokenManager
import com.example.trashbinproject.presentation.auth.AuthViewModel
import com.example.trashbinproject.presentation.scanner.ScannerViewModel
import com.example.trashbinproject.data.network.ApiService
import com.example.trashbinproject.data.network.AuthInterceptor
import com.example.trashbinproject.data.storage.TokenAuthenticator
import com.example.zteam.trash.ProfileViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single { TokenManager(androidContext()) }
    single { AuthInterceptor(get()) }

    // ✅ OkHttpClient БЕЗ TokenAuthenticator в Koin!
    single {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(get<AuthInterceptor>())
            .authenticator(get<AuthInterceptor>().tokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build() // ← БЕЗ .authenticator()!
    }

    // ✅ ApiService
    single<ApiService> {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // ✅ TokenAuthenticator создаём ЛОКАЛЬНО в AuthInterceptor!
    viewModelOf(::AuthViewModel)
    viewModelOf(::ProfileViewModel)
    viewModel { ScannerViewModel(get()) }
}

