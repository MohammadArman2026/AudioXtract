package com.example.audioxtract.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlayGradientCircle() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFE8F4FF), Color(0xFF0096FF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
}
