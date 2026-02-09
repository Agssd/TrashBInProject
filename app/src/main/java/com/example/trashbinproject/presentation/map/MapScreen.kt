package com.example.trashbinproject.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trashbinproject.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    userLat: Double,
    userLng: Double,
    onBack: () -> Unit,
    onBackToMain: () -> Unit,
    viewModel: MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    println("🚩 MapScreen ПОЛУЧИЛ: lat=$userLat, lng=$userLng")
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val nearestBin by viewModel.nearestBin.collectAsState()
    var mapView: MapView? by remember { mutableStateOf(null) }

    // Инициализация MapKit
    LaunchedEffect(Unit) {
        MapKitFactory.getInstance().onStart()
    }
    DisposableEffect(Unit) {
        onDispose {
            MapKitFactory.getInstance().onStop()
            mapView?.onStop()
        }
    }

    // Загрузка данных
    LaunchedEffect(userLat, userLng) {
        viewModel.loadNearestBins(userLat, userLng)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TopAppBar
        CenterAlignedTopAppBar(
            title = { Text("Ближайшая мусорка") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        )

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        mapView = this
                        MapKitFactory.getInstance().onStart()
                        getMapWindow().getMap().setRotateGesturesEnabled(false)
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    println("🚩 MapView update: центрируем на $userLat, $userLng")
                    view.getMapWindow().getMap().move(
                        CameraPosition(Point(userLat, userLng), 15.0f, 0.0f, 0.0f)
                    )
                }
            )

            // Loading overlay
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Bottom Sheet (внизу Column, НЕ в Box!)
        nearestBin?.let { bin ->
            MapBottomSheet(
                nearestBin = bin,
                onBackToMain = onBackToMain,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Error поверх всего (используем LaunchedEffect для показа)
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Показать Snackbar или Dialog с ошибкой
        }
    }
}

@Composable
private fun MapBottomSheet(
    nearestBin: NearestBin,
    onBackToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Заголовок
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = nearestBin.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Расстояние: ${String.format("%.0f", nearestBin.distance)} м",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            InfoRow("Район", nearestBin.district)

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBackToMain,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("На главный экран")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

