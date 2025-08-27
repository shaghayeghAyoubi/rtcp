package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.CameraListScreen
import com.example.myapplication.presentation.dashboard.WebSocketMessageScreen

import com.example.myapplication.presentation.login.LoginScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.yourapp.presentation.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//
//    @Inject lateinit var webSocketManager: WebSocketManager
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            val navController = rememberNavController()
//
//            NavHost(
//                navController = navController,
//                startDestination = "login"
//            ) {
//                composable("login") {
//                    val viewModel: LoginViewModel = hiltViewModel()
//
//                    // ✅ Observe login result and connect WebSocket
//                    LaunchedEffect(Unit) {
//                        viewModel.navigateToCameraList.collect {
//                            // 🔌 Connect WebSocket only after login success
//                            webSocketManager.connect()
//
//                            navController.navigate("camera_list") {
//                                popUpTo("login") { inclusive = true }
//                            }
//                        }
//                    }
//
//                    LoginScreen(navController)
//                }
//
//                composable("camera_list") {
//                    CameraListScreen(navController)
//                }
//
//                composable("messages") {
//                    WebSocketMessageScreen(navController, webSocketManager)
//                }
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // ✅ Always clean up connection
//        webSocketManager.disconnect()
//    }
//}
//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//
//    @Inject lateinit var webSocketManager: WebSocketManager
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            val navController = rememberNavController()
//            NavHost(
//                navController = navController,
//                startDestination = "login"
//            ) {
//                composable("login") { LoginScreen(navController) }
//                composable("camera_list") { CameraListScreen(navController) }
//                composable("messages") { WebSocketMessageScreen(navController, webSocketManager) }
//            }
//        }
//
//        // Attempt initial connect if token already stored.
//        lifecycleScope.launch {
//            try {
//                webSocketManager.connect()
//            } catch (e: Exception) {
//                Log.e("MainActivity", "Failed initial websocket connect", e)
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        webSocketManager.disconnect()
//    }
//}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var webSocketManager: WebSocketManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") { LoginScreen(navController) }
                composable("camera_list") { CameraListScreen(navController) }
                composable("messages") { WebSocketMessageScreen(navController, webSocketManager) }
            }
        }

        // Attempt initial connect if token already stored.
        val serviceIntent = Intent(this, PushNotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}