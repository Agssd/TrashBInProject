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
    val platformNumber: String,
    val latitude: Double,
    val longitude: Double,
    val district: String,
    val distance: Double? = null
)