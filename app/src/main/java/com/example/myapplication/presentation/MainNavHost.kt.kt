package com.example.myapplication.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.myapplication.MainActivityViewModel
import com.example.myapplication.SharedNavigationManager
import com.example.myapplication.WebSocketManager
import com.example.myapplication.presentation.dashboard.CameraListScreen
import com.example.myapplication.presentation.event.WebSocketMessageScreen
import com.example.myapplication.presentation.localization.LocalizationViewModel
import com.example.myapplication.presentation.localization.strings.Strings
import com.example.myapplication.presentation.navigation.Screen
import com.example.myapplication.presentation.recognized_poeple.RecognizedPeopleScreen
import com.example.myapplication.presentation.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavHost(
    webSocketManager: WebSocketManager,
    onLogout: () -> Unit = {},
    localizationViewModel: LocalizationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Event, Screen.Dashboard, Screen.RecognizedPeople, Screen.Settings)
    val strings by localizationViewModel.strings.collectAsState()

    // Observe the pending message ID and navigate to Event screen when it exists
    val pendingMessageId = SharedNavigationManager.pendingMessageId

    LaunchedEffect(pendingMessageId) {
        if (pendingMessageId != null) {
            // Navigate to Event screen
            navController.navigate(Screen.Event.route) {
                // Clear the back stack to avoid multiple instances
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title(strings)) },
                        label = { Text(screen.title(strings)) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Event.route) { backStackEntry ->
                // Get the pending message ID for the dialog
                val currentPendingMessageId = SharedNavigationManager.pendingMessageId

                WebSocketMessageScreen(
                    webSocketManager = webSocketManager,
                    messageId = currentPendingMessageId
                )
            }
            composable(Screen.Dashboard.route) { CameraListScreen(navController = navController) }
            composable(Screen.RecognizedPeople.route) { RecognizedPeopleScreen() }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}