package com.example.myapplication.presentation

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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.WebSocketManager
import com.example.myapplication.presentation.dashboard.CameraListScreen
import com.example.myapplication.presentation.event.WebSocketMessageScreen
import com.example.myapplication.presentation.localization.LocalizationViewModel
import com.example.myapplication.presentation.localization.strings.Strings
import com.example.myapplication.presentation.login.LoginScreen
import com.example.myapplication.presentation.navigation.Screen
import com.example.myapplication.presentation.recognized_poeple.RecognizedPeopleScreen
import com.example.myapplication.presentation.settings.SettingsScreen
import kotlinx.coroutines.delay


// helper extension
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    webSocketManager: WebSocketManager,
    currentIntent: State<Intent?>,
    stopWebSocketService: () -> Unit,
    localizationViewModel: LocalizationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val strings by localizationViewModel.strings.collectAsState()

    // Message data from notification
    var pendingMessageJson by remember { mutableStateOf<String?>(null) }

    // ✅ Extract data from Intent
    LaunchedEffect(currentIntent.value) {
        currentIntent.value?.let { intent ->
            val openScreen = intent.getStringExtra("open_screen")
            val msgJson = intent.getStringExtra("message_data")
            if (openScreen == "messages" && msgJson != null) {
                pendingMessageJson = msgJson
                intent.removeExtra("open_screen")
                intent.removeExtra("message_data")
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("main") {
            MainNavHost(
                webSocketManager = webSocketManager,
                onLogout = stopWebSocketService,
                initialMessageJson = pendingMessageJson
            )
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavHost(
    webSocketManager: WebSocketManager,
    onLogout: () -> Unit,
    initialMessageJson: String? = null,
    localizationViewModel: LocalizationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Event,
        Screen.Dashboard,
        Screen.RecognizedPeople,
        Screen.Settings
    )
    val strings by localizationViewModel.strings.collectAsState()

    // ✅ Track if navigation already happened
    var handledMessageJson by rememberSaveable { mutableStateOf<String?>(null) }

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
            composable(Screen.Event.route) {
                WebSocketMessageScreen(webSocketManager)
            }
            composable(Screen.Dashboard.route) {
                CameraListScreen(navController = navController)
            }
            composable(Screen.RecognizedPeople.route) {
                RecognizedPeopleScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(
                route = "messages?msg={msgJson}",
                arguments = listOf(navArgument("msgJson") { nullable = true })
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString("msgJson")
                WebSocketMessageScreen(
                    webSocketManager = webSocketManager,
                    initialIntentMessageJson = json
                )
            }
        }
    }

    // ✅ Navigate once when notification is received
    LaunchedEffect(initialMessageJson) {
        if (initialMessageJson != null && initialMessageJson != handledMessageJson) {
            handledMessageJson = initialMessageJson
            delay(400) // Let NavHost fully load
            navController.navigate("messages?msg=$initialMessageJson") {
                popUpTo(Screen.Dashboard.route) { inclusive = false }
                launchSingleTop = true
            }
        }
    }
}



