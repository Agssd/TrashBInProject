package com.example.trashbinproject.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashbinproject.data.network.ApiService
import com.example.trashbinproject.domain.TrashBinResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class MapUiState(
    val isLoading: Boolean = false,
    val nearestBin: NearestBin? = null,
    val error: String? = null
)

data class NearestBin(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val district: String,
    val distance: Double
)

class MapViewModel : ViewModel(), KoinComponent {
    private val apiService: ApiService by inject()

    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _nearestBin = MutableStateFlow<NearestBin?>(null)
    val nearestBin = _nearestBin.asStateFlow()

    fun loadNearestBins(userLat: Double, userLng: Double) {
        viewModelScope.launch {
            _uiState.value = MapUiState(isLoading = true)
            try {
                // Получаем ближайшие мусорки с сервера
                val trashBinsResponse = apiService.getNearbyTrashBins(userLat, userLng)

                // Находим ближайшую из ответа API
                val nearestResponse = trashBinsResponse.minByOrNull {
                    calculateDistance(userLat, userLng, it.lat, it.lng)
                }

                nearestResponse?.let { response ->
                    val nearest = NearestBin(
                        id = response.id,
                        name = response.name,
                        latitude = response.lat,
                        longitude = response.lng,
                        district = response.district,
                        distance = calculateDistance(userLat, userLng, response.lat, response.lng)
                    )
                    _nearestBin.value = nearest
                    _uiState.value = MapUiState(nearestBin = nearest)
                } ?: run {
                    _uiState.value = MapUiState(error = "Ближайшие мусорки не найдены")
                }
            } catch (e: Exception) {
                _uiState.value = MapUiState(error = e.message ?: "Ошибка загрузки")
            }
        }
    }

    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}
