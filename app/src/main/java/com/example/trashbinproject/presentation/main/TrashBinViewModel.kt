package com.example.trashbinproject.presentation.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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

    // Новый метод с гарантированным Челябинском
    fun getCurrentLocationChelyabinsk(context: Context, onResult: (Double, Double) -> Unit) {
        initializeLocationClient(context)

        // ✅ ПРЯМАЯ ПРОВЕРКА РАЗРЕШЕНИЯ
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Нет разрешения → Челябинск
            onResult(55.1644, 61.4368)
            return
        }

        // Есть разрешение → пытаемся получить локацию
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // ✅ Реальные координаты
                    onResult(location.latitude, location.longitude)
                } else {
                    // ❌ Нет свежих данных → Челябинск
                    onResult(55.1644, 61.4368)
                }
            }
            .addOnFailureListener {
                // ❌ Ошибка → Челябинск
                onResult(55.1644, 61.4368)
            }
            .addOnCanceledListener {
                // ❌ Отменено → Челябинск
                onResult(55.1644, 61.4368)
            }
    }


    fun setScanResultMessage(message: String) {
        _scanMessage.value = message
    }

    fun clearScanMessage() {
        _scanMessage.value = null
    }
}

