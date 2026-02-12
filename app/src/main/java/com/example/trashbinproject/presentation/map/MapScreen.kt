import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.trashbinproject.domain.TrashBin
import com.example.trashbinproject.presentation.map.MapScreenViewModel
import com.example.zteam.trash.MapBottomSheet
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    userLat: Double,
    userLng: Double,
    onBack: () -> Unit,
    onBackToMain: () -> Unit,
    onNavigateToScanner: (Int) -> Unit,
    mapScreenViewModel: MapScreenViewModel
) {
    val uiState by mapScreenViewModel.uiState.collectAsState()
    var mapView: MapView? by remember { mutableStateOf(null) }
    var selectedBin by remember { mutableStateOf<TrashBin?>(null) }
    val canScanStatus by mapScreenViewModel.canScanResult.collectAsState()

    val mapTapListener by remember {
        mutableStateOf(object : com.yandex.mapkit.map.InputListener {
            override fun onMapTap(map: com.yandex.mapkit.map.Map, point: com.yandex.mapkit.geometry.Point) {
                val clickedBin = uiState.allBins.minByOrNull { bin ->
                    calculateDistance(point.latitude, point.longitude, bin.latitude, bin.longitude)
                }?.takeIf { bin ->
                    calculateDistance(point.latitude, point.longitude, bin.latitude, bin.longitude) < 50
                }

                clickedBin?.let { bin ->
                    mapScreenViewModel.checkCanScanBin(bin.id)  // ✅ ФИКС!
                    selectedBin = bin
                }
            }
            override fun onMapLongTap(map: com.yandex.mapkit.map.Map, point: com.yandex.mapkit.geometry.Point) {}
        })
    }

    LaunchedEffect(Unit) {
        mapScreenViewModel.loadTrashBins(userLat, userLng)
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView?.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Мусорки (${uiState.allBins.size})") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            }
        )

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        mapView = this
                        MapKitFactory.getInstance().onStart()
                        mapWindow.map.addInputListener(mapTapListener)
                    }
                },
                update = { view -> MapUpdate(view, userLat, userLng, uiState.allBins,  canScanStatus ) }
            )
        }

        selectedBin?.let { bin ->
            MapBottomSheet(
                trashBin = bin,
                userLat = userLat,
                userLng = userLng,
                onBackToMain = onBackToMain,
                onPhotoClick = {
                    selectedBin = null
                    onNavigateToScanner(bin.id)
                },
                onClose = { selectedBin = null },
                mapScreenViewModel = mapScreenViewModel
            )
        }
    }
}

fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadius = 6371000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1.toDouble())) * cos(Math.toRadians(lat2)) *
            sin(dLng / 2) * sin(dLng / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}

