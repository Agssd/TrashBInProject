package com.example.trashbinproject.data.storage
import com.example.trashbinproject.data.network.ApiService
import com.example.trashbinproject.domain.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val retrofit: Retrofit
) : Authenticator {

    private val api by lazy { retrofit.create(ApiService::class.java) }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val refreshToken = runBlocking { tokenManager.getRefreshToken() } ?: return null

        val refreshResponse = try {
            api.refresh(RefreshRequest(refreshToken)).execute()
        } catch (e: Exception) {
            null
        }

        if (refreshResponse == null || !refreshResponse.isSuccessful) {
            runBlocking { tokenManager.clearTokens() }
            return null
        }

        val body = refreshResponse.body() ?: return null

        runBlocking {
            tokenManager.saveTokens(
                access = body.accessToken,
                refresh = body.refreshToken
            )
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${body.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prev = response.priorResponse
        while (prev != null) {
            result++
            prev = prev.priorResponse
        }
        return result
    }
}
