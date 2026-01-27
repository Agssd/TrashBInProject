package com.example.trashbinproject.presentation.auth.OnBoarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val gradientColors = listOf(
    Color(0xFF7FDBDA), // Светло-голубой
    Color(0xFF4D7CCC)  // Сине-фиолетовый
)

val gradientColorsAuth = listOf(
    Color(0xFF81C784), // Светло-зелёный (начало градиента)
    Color(0xFF2E7D32)  // Насыщенный зелёный (конец градиента)
)

@Composable
fun OnboardingProgressIndicator(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..3) {
            val isCurrentStep = i == currentStep
            val color = if (isCurrentStep) Color(0xFF4D7CCC) else Color(0xFF7FDBDA)
            val size = if (isCurrentStep) 16.dp else 12.dp

            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(color)
            )
            if (i < 3) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

