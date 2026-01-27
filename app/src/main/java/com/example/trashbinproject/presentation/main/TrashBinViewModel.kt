package com.example.trashbinproject.presentation.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrashBinViewModel : ViewModel() {

    private val _scanMessage = MutableStateFlow<String?>(null)
    val scanMessage: StateFlow<String?> = _scanMessage

    fun setScanResultMessage(message: String) {
        _scanMessage.value = message
    }

    fun clearScanMessage() {
        _scanMessage.value = null
    }
}
