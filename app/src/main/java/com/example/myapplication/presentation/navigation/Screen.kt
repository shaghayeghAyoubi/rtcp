package com.example.myapplication.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.presentation.localization.strings.Strings

sealed class Screen(val route: String, val title: (Strings) -> String, val icon: ImageVector) {
    object Event : Screen("event",  title = { it.event }, Icons.Default.Event)
    object Dashboard : Screen("dashboard",   title = { it.dashboard } , Icons.Default.Home)
    object RecognizedPeople : Screen("recognized_people", title = { it.recognizedPeople }, Icons.Default.Face)
    object Settings : Screen("settings", title = { it.settings }, Icons.Default.Settings)
}