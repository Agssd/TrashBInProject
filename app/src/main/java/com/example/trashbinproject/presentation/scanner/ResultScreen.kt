package com.example.trashbinproject.presentation.scanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trashbinproject.data.network.RetrofitClient.apiService
import com.example.trashbinproject.domain.MarkScannedRequest
import com.example.zteam.trash.ProfileViewModel

@Composable
fun ResultScreen(
    binId: Int,
    scannerViewModel: ScannerViewModel,
    profileViewModel: ProfileViewModel,
    onBackToMain: () -> Unit
) {
    val outputBase64 by scannerViewModel.outputImageBase64.collectAsState()
    val predictions by scannerViewModel.predictions.collectAsState()
    val errorMessage by scannerViewModel.scanMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            errorMessage != null -> {
                Text("Ошибка: $errorMessage", color = Color.Red, fontSize = 18.sp)
            }

            outputBase64 != null -> {
                val bitmap = decodeBase64ToBitmap(outputBase64!!)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp) // ограничиваем картинку, чтобы текст был виден
                        .padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val fullDetected = predictions.any { it.className.equals("full", ignoreCase = true) }
                if (fullDetected) {  // ✅ Мусорка полностью заполнена
                    LaunchedEffect(Unit) {
                        // 1. +10 очков
                        profileViewModel.addPoints(10)

                        // 2. ✅ БЛОКИРУЕМ мусорку!
                        try {
                            apiService.markBinScanned(MarkScannedRequest(binId))
                            println("✅ Мусорка ${binId} заблокирована на 24ч!")
                        } catch (e: Exception) {
                            println("⚠️ Ошибка блокировки: ${e.message}")
                        }
                    }
                } else {
                    Text(
                        text = "Баллы не начислены",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = { onBackToMain() }) {
                    Text("Вернуться в главное меню")
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Анализируем изображение...", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}


fun decodeBase64ToBitmap(base64Str: String): Bitmap {
    val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}