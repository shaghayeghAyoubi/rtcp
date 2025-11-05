package com.example.myapplication.presentation.event


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.WebSocketManager
import com.example.myapplication.domain.model.FaceRecognitionMessage
import com.example.myapplication.domain.model.RecognizedPerson
import com.example.myapplication.presentation.dashboard.CameraListViewModel
import com.google.gson.Gson


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebSocketMessageScreen(
    webSocketManager: WebSocketManager,
    viewModel: CameraListViewModel = hiltViewModel(),
    initialIntentMessageJson: String? = null // ✅ new param
) {
    val messages by webSocketManager.messages.collectAsState()
    val recognizedPeopleState = viewModel.recognizedPeopleState.collectAsState().value
    val context = LocalContext.current

    // Keep track of the selected message for detail dialog
    var selectedMessage by remember { mutableStateOf<FaceRecognitionMessage?>(null) }


    // ✅ Automatically open dialog when app opened from notification
    LaunchedEffect(initialIntentMessageJson) {
        initialIntentMessageJson?.let {
            runCatching {
                val msg = Gson().fromJson(it, FaceRecognitionMessage::class.java)
                selectedMessage = msg
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    when {
                        recognizedPeopleState.isLoading -> {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }

                        recognizedPeopleState.error != null -> {
                            Text(
                                text = "Error: ${recognizedPeopleState.error}",
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        else -> {
                            RecognizedPeopleList(
                                recognizedPeople = recognizedPeopleState.recognizedPeople
                            )
                        }
                    }
                }

                // --- Messages section ---
                if (messages.isEmpty()) {
                    item {
                        Text(
                            "Waiting for messages...",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else {
                    items(messages) { message ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMessage = message },
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "🕒 Time: ${message.createdDate}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "💬 Message: ${message.message}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                message.nearestNeighbourSimilarity?.let {
                                    Text(
                                        "🔍 Similarity: $it%",
                                        style = MaterialTheme.typography.bodySmall
                                    )
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

        // --- Move the dialog OUTSIDE the LazyColumn ---
        if (selectedMessage != null) {
            AlertDialog(
                onDismissRequest = { selectedMessage = null },
                title = { Text("Message Details") },
                text = {
                    Column {
                        Text("🕒 Time: ${selectedMessage!!.createdDate}")
                        Text("💬 Message: ${selectedMessage!!.message}")
                        selectedMessage!!.nearestNeighbourSimilarity?.let {
                            Text("🔍 Similarity: $it%")
                        }
                        selectedMessage!!.croppedFace?.let { base64 ->
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
                },
                confirmButton = {
                    TextButton(onClick = { selectedMessage = null }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}


@Composable
fun RecognizedPeopleList(recognizedPeople: List<RecognizedPerson>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(recognizedPeople) { person ->
            Card(
                modifier = Modifier
                    .width(150.dp)
                    .clickable {
                        // Optional: handle click, navigate, etc.
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val bitmap = remember(person.croppedFaceUrl) {
                        base64ToBitmap(person.croppedFaceUrl)
                    }

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Cropped face",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color.Gray, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No Image", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = person.camera.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = person.recognizedDate?.take(10) ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
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
