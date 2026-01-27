package com.example.trashbinproject

import AppNavHost
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "onCreate запущен")

        setContent {
            Log.d("MainActivity", "setContent инициализируется")

            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}
