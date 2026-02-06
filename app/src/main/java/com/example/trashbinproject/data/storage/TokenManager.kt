package com.example.trashbinproject.data.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    @Volatile
    var currentAccessToken: String? = null

    val accessTokenFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[ACCESS_TOKEN_KEY] }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[REFRESH_TOKEN_KEY] }

    init {
        runBlocking {
            val prefs = context.dataStore.data.firstOrNull()
            currentAccessToken = prefs?.get(ACCESS_TOKEN_KEY)
        }
    }

    suspend fun saveTokens(access: String, refresh: String) {
        currentAccessToken = access
        AuthStorage.token = access

        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = access
            prefs[REFRESH_TOKEN_KEY] = refresh
        }
    }

    suspend fun saveAccessToken(access: String) {
        currentAccessToken = access
        AuthStorage.token = access

        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = access
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
        }
        currentAccessToken = null
        AuthStorage.token = null
    }

    suspend fun getAccessToken(): String? {
        return currentAccessToken ?: accessTokenFlow.firstOrNull()
    }

    suspend fun getRefreshToken(): String? {
        return refreshTokenFlow.firstOrNull()
    }
}