package com.example.trashbinproject.presentation.scanner

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashbinproject.data.network.ApiService
import com.example.trashbinproject.domain.Prediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ScannerViewModel(private val apiService: ApiService) : ViewModel() {

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap: StateFlow<Bitmap?> = _capturedBitmap

    private val _outputImageBase64 = MutableStateFlow<String?>(null)
    val outputImageBase64: StateFlow<String?> = _outputImageBase64

    private val _predictions = MutableStateFlow<List<Prediction>>(emptyList())
    val predictions: StateFlow<List<Prediction>> = _predictions

    private val _scanMessage = MutableStateFlow<String?>(null)
    val scanMessage: StateFlow<String?> = _scanMessage

    var imageCapture: ImageCapture? = null

    fun setCapturedBitmap(bitmap: Bitmap) {
        _capturedBitmap.value = bitmap
        _outputImageBase64.value = null
        _predictions.value = emptyList()
        _scanMessage.value = null
    }

    fun analyzeBitmap(context: Context) {
        val bitmap = _capturedBitmap.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.cacheDir, "temp.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val response = apiService.predictImage(body)

                if (response.isSuccessful) {
                    val result = response.body()?.firstOrNull()
                    _outputImageBase64.value = result?.outputImage
                    _predictions.value = result?.predictions?.predictions ?: emptyList()
                    _scanMessage.value = null
                } else {
                    _scanMessage.value = "Ошибка сервера: ${response.code()}"
                }
            } catch (e: Exception) {
                _scanMessage.value = "Сбой запроса: ${e.message}"
            }
        }
    }
}


