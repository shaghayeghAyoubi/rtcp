package com.example.myapplication.presentation.login



//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.myapplication.presentation.dashboard.CameraListViewModel
//import com.example.yourapp.presentation.login.LoginViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.presentation.settings.SettingsScreen
import com.example.yourapp.presentation.login.LoginViewModel


//@Composable
//fun LoginScreen(  viewModel: LoginViewModel = hiltViewModel()) {
//    val username = viewModel.username
//    val password = viewModel.password
//    val loginState = viewModel.loginState
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        TextField(
//            value = username,
//            onValueChange = { viewModel.username = it },
//            label = { Text("Username") }
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        TextField(
//            value = password,
//            onValueChange = { viewModel.password = it },
//            label = { Text("Password") },
//            visualTransformation = PasswordVisualTransformation()
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = { viewModel.login() }) {
//            Text("Login")
//        }
//
//        loginState?.let {
//            when {
//                it.isSuccess -> Text("Login Successful! Token: ${it.getOrNull()?.token}")
//                it.isFailure -> Text("Error: ${it.exceptionOrNull()?.message}")
//            }
//        }
//    }
//}
//@Composable
//fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
//    val username = viewModel.username
//    val password = viewModel.password
//    val loginState = viewModel.loginState
//
//    var passwordVisible by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF2D2D2D))
//    ) {
//        // Title at top
//        Text(
//            text = "METIS",
//            color = Color.White,
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(top = 80.dp)
//        )
//
//        // Login Card
//        Card(
//            modifier = Modifier
//                .align(Alignment.Center)
//                .padding(horizontal = 16.dp),
//            elevation = CardDefaults.cardElevation(8.dp),
//            shape = RoundedCornerShape(4.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(24.dp)
//                    .widthIn(max = 400.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Sign in to your account",
//                    fontWeight = FontWeight.Normal,
//                    fontSize = 18.sp
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                OutlinedTextField(
//                    value = username,
//                    onValueChange = { viewModel.username = it },
//                    label = { Text("Username") },
//                    singleLine = true,
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = { viewModel.password = it },
//                    label = { Text("Password") },
//                    singleLine = true,
//                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                    trailingIcon = {
//                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                            Icon(
//                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
//                                contentDescription = null
//                            )
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Button(
//                    onClick = { viewModel.login() },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(45.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
//                ) {
//                    Text("Sign In")
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                loginState?.let {
//                    when {
//                        it.isSuccess -> Text("Login Successful! Token: ${it.getOrNull()?.token}")
//                        it.isFailure -> Text("Error: ${it.exceptionOrNull()?.message}")
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun LoginScreen( navController: NavHostController,viewModel: LoginViewModel = hiltViewModel(),) {
    val username = viewModel.username
    val password = viewModel.password
    val loginState = viewModel.loginState

    var passwordVisible by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.navigateToCameraList.collect {
            navController.navigate("main") {
                popUpTo("login") { inclusive = true } // Optional: clear back stack
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.login_bg), // replace with your image
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Login Card
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.9f), shape = MaterialTheme.shapes.medium)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sign in to your account", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { showSettingsDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Need to change language or API URL? Go to Settings",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign In")
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (val state = viewModel.loginState) {
                is LoginState.Loading -> CircularProgressIndicator()
                is LoginState.Success -> {
                    Text("Welcome")
                    Text("Refresh Token")
                }
                is LoginState.Error -> Text("Login Failed: ${state.message}", color = Color.Red)
                LoginState.Idle -> {}
            }
        }
    }
    if (showSettingsDialog) {
        Dialog(onDismissRequest = { showSettingsDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .width(900.dp)
                    .height(450.dp)
            ) {
                SettingsScreen(
                    localizationViewModel = hiltViewModel(),
                    settingsViewModel = hiltViewModel(),
                    isDialog = true, // 👈 tell it’s inside dialog
                    onSave = { showSettingsDialog = false }
                )
            }
        }
    }
}