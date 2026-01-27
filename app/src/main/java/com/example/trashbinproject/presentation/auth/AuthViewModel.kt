package com.example.trashbinproject.presentation.auth

import com.example.trashbinproject.data.storage.TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashbinproject.data.network.ApiService
import com.example.trashbinproject.domain.UserCreateRequest
import com.example.trashbinproject.domain.UserLoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val token: String? = null,
    val error: String? = null
)

class AuthViewModel(
    private val api: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun register(username: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val response = api.register(UserCreateRequest(username, password, name))
                val token = response.accessToken
                if (!token.isNullOrEmpty()) {
                    tokenManager.saveToken(token)
                }
                _uiState.value = AuthUiState(isSuccess = !token.isNullOrEmpty(), token = token)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Ошибка регистрации")
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val response = api.login(UserLoginRequest(username, password))
                val token = response.accessToken
                if (!token.isNullOrEmpty()) {
                    tokenManager.saveToken(token)
                }
                _uiState.value = AuthUiState(isSuccess = !token.isNullOrEmpty(), token = token)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Ошибка входа")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            _uiState.value = AuthUiState()
        }
    }
}


