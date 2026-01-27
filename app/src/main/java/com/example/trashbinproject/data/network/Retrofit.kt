package com.example.trashbinproject.data.network

import org.koin.core.context.GlobalContext


object RetrofitClient {
    val apiService: ApiService
        get() = GlobalContext.get().get()
}