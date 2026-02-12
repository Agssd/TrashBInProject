package com.example.trashbinproject.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import calculateDistance
import com.example.trashbinproject.data.network.RetrofitClient.apiService
import com.example.trashbinproject.domain.CanScanRequest
import com.example.trashbinproject.domain.MapUiState
import com.example.trashbinproject.domain.TrashBin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class MapScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _canScanResult = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val canScanResult: StateFlow<Map<Int, Boolean>> = _canScanResult.asStateFlow()

    fun loadTrashBins(userLat: Double, userLng: Double) {
        viewModelScope.launch {
            _uiState.value = MapUiState(isLoading = true)

            try {
                val binsResponse = apiService.getNearbyTrashBins(userLat, userLng)

                binsResponse.forEach { bin ->
                    checkCanScanBin(bin.id)
                }

                val trashBins = binsResponse.map { response ->
                    TrashBin(
                        id = response.id,
                        name = response.name,
                        latitude = response.lat,
                        longitude = response.lng,
                        district = response.district,
                        distance = calculateDistance(userLat, userLng, response.lat, response.lng),
                        canScanToday = true
                    )
                }

                _uiState.value = MapUiState(
                    isLoading = false,
                    allBins = trashBins.sortedBy { it.distance }
                )
            } catch (e: Exception) {
                _uiState.value = MapUiState(error = "Ошибка загрузки")
            }
        }
    }

    fun checkCanScanBin(binId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.canScanBin(CanScanRequest(binId))
                _canScanResult.value = _canScanResult.value + (binId to response.can_scan)
            } catch (e: Exception) {
                _canScanResult.value = _canScanResult.value + (binId to true)
            }
        }
    }
}
