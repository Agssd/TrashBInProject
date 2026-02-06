package com.example.zteam.trash

import com.example.trashbinproject.data.storage.TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashbinproject.data.network.ApiService
import com.example.trashbinproject.domain.PointsUpdateRequest
import com.example.trashbinproject.domain.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserResponse) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val api: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val user = api.me()
                _uiState.value = ProfileUiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.localizedMessage ?: "Неизвестная ошибка")
            }
        }
    }

    fun addPoints(value: Int) {
        viewModelScope.launch {
            try {
                api.updatePoints(PointsUpdateRequest(value))
                val current = _uiState.value
                if (current is ProfileUiState.Success) {
                    val updatedUser = current.user.copy(points = current.user.points + value)
                    _uiState.value = ProfileUiState.Success(updatedUser)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Ошибка добавления очков")
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearTokens()
            onComplete()
        }
    }
}


