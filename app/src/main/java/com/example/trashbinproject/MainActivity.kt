package com.example.trashbinproject

import AppNavHost
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "onCreate запущен")
        MapKitFactory.setApiKey(BuildConfig.YANDEX_API_KEY)
        MapKitFactory.initialize(this)

        setContent {
            Log.d("MainActivity", "setContent инициализируется")

            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}
