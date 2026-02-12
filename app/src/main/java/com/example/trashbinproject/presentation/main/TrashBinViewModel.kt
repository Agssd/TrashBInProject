package com.example.trashbinproject.presentation.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrashBinViewModel : ViewModel() {
    private val _scanMessage = MutableStateFlow<String?>(null)
    val scanMessage: StateFlow<String?> = _scanMessage

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun initializeLocationClient(context: Context) {
        if (!this::fusedLocationClient.isInitialized) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }
    }

    fun getCurrentLocationChelyabinsk(context: Context, onResult: (Double, Double) -> Unit) {
        // ✅ ЭМУЛЯТОР = ТОЛЬКО Челябинск!
        if (isEmulator()) {
            println("🧪 ЭМУЛЯТОР: ФОРСИРУЕМ Челябинск!")
            onResult(55.1644, 61.4368)
            return
        }

        // ✅ Телефон = реальный GPS
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            onResult(55.1644, 61.4368)
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // ✅ БЛОКИРУЕМ Сан-Франциско!
            val lat = if (location?.latitude == 37.42 && location.longitude == -122.08) {
                55.1644  // Эмулятор лжет → Челябинск!
            } else {
                location?.latitude ?: 55.1644
            }
            val lng = if (location?.longitude?.toFloat() == -122.08f) {
                61.4368  // Эмулятор лжет → Челябинск!
            } else {
                location?.longitude ?: 61.4368
            }
            println("📍 GPS: lat=$lat, lng=$lng")
            onResult(lat, lng)
        }
    }


    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK") ||
                Build.HARDWARE.contains("goldfish") ||
                Build.HARDWARE.contains("ranchu")
    }




    fun setScanResultMessage(message: String) {
        _scanMessage.value = message
    }

    fun clearScanMessage() {
        _scanMessage.value = null
    }
}

