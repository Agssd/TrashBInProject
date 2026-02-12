package com.example.zteam.trash

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import calculateDistance
import com.example.trashbinproject.domain.TrashBin
import com.example.trashbinproject.presentation.map.MapScreenViewModel

@Composable
fun MapBottomSheet(
    trashBin: TrashBin,
    onBackToMain: () -> Unit,
    userLat: Double,
    userLng: Double,
    onPhotoClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    mapScreenViewModel: MapScreenViewModel
) {
    val distance = calculateDistance(userLat, userLng, trashBin.latitude, trashBin.longitude)
    val canPhoto = distance <= 300.0

    val canScanStatus by mapScreenViewModel.canScanResult.collectAsState()
    val canScanToday = canScanStatus[trashBin.id] ?: true

    val context = LocalContext.current
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            println("Камера разрешена для ${trashBin.name}")
            onPhotoClick()
        } else {
            println("Камера отклонена")
        }
    }

    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(trashBin.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Расстояние: ${String.format("%.0f", distance)} м", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    val statusText = if (canPhoto) "Доступно для фото!" else "Вне зоны (нужно подойти ближе)"
                    val statusColor = if (canPhoto) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    Text(statusText, style = MaterialTheme.typography.bodyMedium, color = statusColor)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Район: ${trashBin.district}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (canPhoto && canScanToday) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            onPhotoClick()
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                },
                enabled = canPhoto && canScanToday,
                modifier = Modifier.fillMaxWidth()
            ) {
                when {
                    !canScanToday -> Text("🔒 Уже сфоткал сегодня!")
                    !canPhoto -> Text("🚫 Вне зоны 300м")
                    else -> Text("📸 Сфотографировать!")
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(onClick = onBackToMain, modifier = Modifier.fillMaxWidth()) {
                Text("На главный экран")
            }
        }
    }
}
