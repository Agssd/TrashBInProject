package com.example.trashbinproject.domain

data class TrashBinResponse(
    val id: Int,
    val name: String,
    val lat: Double,
    val lng: Double,
    val district: String
)

data class TrashBin(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val district: String,
    val distance: Double,
    val canScanToday: Boolean
)

data class CanScanRequest(val bin_id: Int)
data class MarkScannedRequest(val bin_id: Int)
data class CanScanResponse(val can_scan: Boolean, val message: String)

data class MapUiState(
    val isLoading: Boolean = false,
    val allBins: List<TrashBin> = emptyList(),
    val canScanStatus: Map<Int, Boolean> = emptyMap(),
    val error: String? = null
)
