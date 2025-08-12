package com.example.myapplication.presentation.dashboard


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavController
import android.content.Intent
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.PushNotificationService



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebSocketMessageScreen(navController: NavController, viewModel: WebSocketViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Live Messages") } ,   navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (messages.isEmpty()) {
                Text("Waiting for messages...", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { message ->

                        if (message.message == "forbidden") {
                            // Show toast in UI thread


                            // Start PushNotificationService with message
                            val serviceIntent = Intent(context, PushNotificationService::class.java)
                                .putExtra("msg", "⚠ Forbidden message received!")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(serviceIntent)
                            } else {
                                context.startService(serviceIntent)
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {

                                Text("🕒 Time: ${message.createdDate}", style = MaterialTheme.typography.bodySmall)
                                Text("💬 Message: ${message.message}", style = MaterialTheme.typography.bodyMedium)
                                message.nearestNeighbourSimilarity?.let {
                                    Text("🔍 Similarity: $it%", style = MaterialTheme.typography.bodySmall)
                                }

                                message.croppedFace?.let { base64 ->
                                    base64ToBitmap(base64)?.let { bitmap ->
                                        Image(
                                            painter = BitmapPainter(bitmap.asImageBitmap()),
                                            contentDescription = "Cropped Face",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .padding(top = 8.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun base64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        Log.e("Base64", "Failed to decode image", e)
        null
    }
}
