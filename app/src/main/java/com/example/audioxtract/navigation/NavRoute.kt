package com.example.audioxtract.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

object NavRoute {
    const val Home_Screen = "home_screen"
    const val Audio_Player_Screen = "audio_player_screen"
    const val Audio_List_Screen = "play_screen"
}

sealed class BottomNavRoutes(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Home : BottomNavRoutes(NavRoute.Home_Screen,
        "Home", Icons.Default.Home)

    object List : BottomNavRoutes(NavRoute.Audio_List_Screen,
        "List", Icons.AutoMirrored.Filled.List
    )
}