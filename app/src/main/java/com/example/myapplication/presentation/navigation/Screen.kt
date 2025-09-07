package com.example.myapplication.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Event : Screen("event", "Event", Icons.Default.Event)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object RecognizedPeople : Screen("recognized_people", "Recognized", Icons.Default.Face)
}