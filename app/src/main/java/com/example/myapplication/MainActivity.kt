package com.example.myapplication // <-- replace with your actual package

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Replace these imports with your real classes / package paths
import com.example.myapplication.WebSocketForegroundService
import com.example.myapplication.WebSocketManager
import com.example.myapplication.presentation.CameraListScreen
import com.example.myapplication.presentation.dashboard.WebSocketMessageScreen
import com.example.myapplication.presentation.login.LoginScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var webSocketManager: WebSocketManager

    // Permission launcher for Android 13+ notification permission
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // optional: log or handle user choice
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request POST_NOTIFICATIONS on Android 13+
        requestNotificationPermissionIfNeeded()

        setContent {
            AppNavHost()
        }

        // Start the foreground service which will in turn call webSocketManager.connectAsync()
        startWebSocketForegroundService()

        // Optionally prompt the user to exempt the app from battery optimizations
        // (uncomment to call automatically; better UX: call after user completes login or from a settings screen)
        // ensureBatteryOptimizationIgnored()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun startWebSocketForegroundService() {
        val startIntent = Intent(this, WebSocketForegroundService::class.java).apply {
            action = WebSocketForegroundService.ACTION_START
        }
        // Use ContextCompat.startForegroundService to be safe on modern Android versions
        ContextCompat.startForegroundService(this, startIntent)
    }

    /**
     * Call this when user logs out or you explicitly want to stop the connection.
     */
    fun stopWebSocketForegroundService() {
        val stopIntent = Intent(this, WebSocketForegroundService::class.java).apply {
            action = WebSocketForegroundService.ACTION_STOP
        }
        // A normal startService is sufficient for STOP action
        startService(stopIntent)
    }

    /**
     * Helper to ask user to exclude app from battery optimization (use judiciously).
     * It's better to explain to user why this is necessary before firing this Intent.
     */
    fun ensureBatteryOptimizationIgnored() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(PowerManager::class.java) ?: return
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }

    /**
     * If your notifications open the app with an extra like "open_screen":"messages",
     * forward that to the nav graph so the app opens the correct screen.
     */
    override fun onNewIntent(intent: Intent) {
        if (intent != null) {
            super.onNewIntent(intent)
        }
        intent ?: return
        val openScreen = intent.getStringExtra("open_screen")
        if (openScreen == "messages") {
            // navigate to messages screen (we're using Compose NavHost below so we trigger via intent)
            // we pass the value to the composable via the intent extras — see AppNavHost() where it handles this
            setIntent(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun AppNavHost() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") { LoginScreen(navController) }
            composable("camera_list") { CameraListScreen(navController) }
            composable("messages") {
                // pass the injected manager into the messages screen
                WebSocketMessageScreen(navController, webSocketManager)
            }
        }

        // If MainActivity was launched from a notification with "open_screen" extras,
        // navigate to messages after navController is ready.
        val initial = intent?.getStringExtra("open_screen")
        if (initial == "messages") {
            // navigate after composition
            androidx.compose.runtime.LaunchedEffect(Unit) {
                navController.navigate("messages") {
                    // optional: clear login from backstack if you want
                    popUpTo("login") { inclusive = false }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Do NOT disconnect websocket here — the foreground service should manage lifecycle.
        // If you want to disconnect when activity destroyed, uncomment:
        // webSocketManager.disconnect()
    }
}