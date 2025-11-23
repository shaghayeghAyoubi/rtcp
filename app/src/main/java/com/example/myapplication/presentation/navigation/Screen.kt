package com.example.myapplication.presentation.navigation

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.R
import com.example.myapplication.presentation.localization.strings.Strings

sealed class Screen(val route: String, val title: (Strings) -> String, val iconRes: Int  ) {
    object Event : Screen("event",  title = { it.event }, R.drawable.ic_data_backup)
    object Dashboard : Screen("dashboard",   title = { it.dashboard } , R.drawable.ic_camera)
    object RecognizedPeople : Screen("recognized_people", title = { it.recognizedPeople }, R.drawable.ic_reliable)
    object Settings : Screen("settings", title = { it.settings }, R.drawable.ic_settings)
}