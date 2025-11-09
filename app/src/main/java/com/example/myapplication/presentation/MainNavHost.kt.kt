package com.example.myapplication.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
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
import java.net.URLDecoder


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    webSocketManager: WebSocketManager,
    stopWebSocketService: () -> Unit,
    localizationViewModel: LocalizationViewModel = hiltViewModel(),
    initialIntent: Intent? = null
) {
    val navController = rememberNavController()
    val strings by localizationViewModel.strings.collectAsState()

    // Helper to determine whether to show bottom bar:
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rawRoute = navBackStackEntry?.destination?.route ?: ""
    val currentRoute = rawRoute.substringBefore('?')

    Scaffold(
        bottomBar = {
            // show bottom bar only for routes that are part of the "main" (bottom nav) graph
            val bottomRoutes = listOf(
                Screen.Event.route,
                Screen.Dashboard.route,
                Screen.RecognizedPeople.route,
                Screen.Settings.route
            )
            if (currentRoute in bottomRoutes) {
                NavigationBar {
                    val items = listOf(
                        Screen.Event,
                        Screen.Dashboard,
                        Screen.RecognizedPeople,
                        Screen.Settings
                    )
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title(strings)) },
                            label = { Text(screen.title(strings)) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                // Navigate to the child route inside the "main" graph
                                navController.navigate(screen.route) {
                                    // Pop up to the main graph start destination to avoid building a huge back stack
                                    popUpTo("main") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            // -------------------------
            // 1) Login (top-level)
            // -------------------------
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = {
                        // go to main graph (bottom nav area)
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            // -------------------------
            // 2) Main nested graph (bottom nav)
            // -------------------------
            navigation(
                startDestination = Screen.Dashboard.route,
                route = "main"
            ) {
                composable(Screen.Event.route) {
                    WebSocketMessageScreen(webSocketManager = webSocketManager)
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
            }

            // -------------------------
            // 3) Messages (deep link target) - top-level so it can be opened from anywhere
            // -------------------------
            composable(
                route = "messages?msg={msg}",
                arguments = listOf(navArgument("msg") { nullable = true }),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "myapp://messages?msg={msg}"
                        action = Intent.ACTION_VIEW
                    }
                )
            ) { backStackEntry ->
                val jsonEncoded = backStackEntry.arguments?.getString("msg")
                val decodedJson = jsonEncoded?.let { URLDecoder.decode(it, "UTF-8") }
                Log.d("AppNavHost", "Deep link opened messages, msg present=${decodedJson != null}")

                WebSocketMessageScreen(
                    webSocketManager = webSocketManager,
                    initialIntentMessageJson = decodedJson
                )
            }
        }
    }

    // Optional: handle an initial Intent passed into this composable (cold start)
    // If you use MainActivity->navController.handleDeepLink(intent) you may not need this.
    initialIntent?.let { intent ->
        LaunchedEffect(intent) {
            // If the intent encodes open_screen/message_data extras instead of URI,
            // handle that here. Example:
            val openScreen = intent.getStringExtra("open_screen")
            val msgJson = intent.getStringExtra("message_data")
            if (openScreen == "messages" && msgJson != null) {
                val encoded = Uri.encode(msgJson)
                navController.navigate("messages?msg=$encoded") {
                    launchSingleTop = true
                }
            }
        }
    }
}






