package com.example.zteam.trash

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trashbinproject.presentation.auth.OnBoarding.gradientColors
import com.example.trashbinproject.presentation.main.TrashBinViewModel

@SuppressLint("UseOfNonLambdaOffsetOverload")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashBinScreen(
    trashBinViewModel: TrashBinViewModel = viewModel(),
    onNavigateToScanner: () -> Unit,
    profileViewModel: ProfileViewModel,
    onProfileClick: () -> Unit
) {
    val state by profileViewModel.uiState.collectAsState()

    val scanMessage by trashBinViewModel.scanMessage.collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onNavigateToScanner()
        } else {
            Toast.makeText(context, "Камера недоступна без разрешения", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(scanMessage) {
        scanMessage?.let {
            snackbarHostState.showSnackbar(it)
            trashBinViewModel.clearScanMessage()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = gradientColors
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cloud,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                    Column {
                        Text(
                            text = "EcoScan",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Сканнер мусорных баков",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                IconButton(onClick = onProfileClick) {
                    Icon(
                        modifier = Modifier.size(34.dp),
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Профиль",
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(gradientColors),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(6.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            val s = state
                            Text(
                                text = when (s) {
                                    is ProfileUiState.Success -> "${s.user.points}"
                                    is ProfileUiState.Loading -> "Загрузка..."
                                    is ProfileUiState.Error -> "Ошибка"
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Всего баллов",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = gradientColors
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Cloud,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }
//                    Row {
//                        Card { }
//                        Card { }
//                    }
                }
            }
        }
    }
//    Scaffold(
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
//        topBar = {
//            TopAppBar(
//                title = { Text("Мусорный бак") },
//                actions = {
//                    IconButton(onClick = onProfileClick) {
//                        Icon(
//                            imageVector = Icons.Default.AccountCircle,
//                            contentDescription = "Профиль"
//                        )
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(24.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            val s = state
//            Text(
//                text = when (s) {
//                    is ProfileUiState.Success -> "Ваши баллы: ${s.user.points}"
//                    is ProfileUiState.Loading -> "Загрузка..."
//                    is ProfileUiState.Error   -> "Ошибка"
//                },
//                style = MaterialTheme.typography.headlineMedium
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Button(
//                onClick = {
//                    if (ContextCompat.checkSelfPermission(
//                            context,
//                            Manifest.permission.CAMERA
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {
//                        onNavigateToScanner()
//                    } else {
//                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//                    }
//                }
//            ) {
//                Text("Сканировать бак")
//            }
//        }
//    }
}

