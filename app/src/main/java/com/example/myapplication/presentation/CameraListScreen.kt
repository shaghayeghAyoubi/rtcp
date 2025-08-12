package com.example.myapplication.presentation



import com.example.myapplication.PushNotificationService
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
//import com.example.myapplication.data.webrtc.WebRTCClient

import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import com.example.myapplication.domain.model.RecognizedPerson
import com.example.myapplication.presentation.webrtc.WebRtcViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraListScreen(
    navController: NavController,
    viewModel: CameraListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value
    val recognizedPeopleState = viewModel.recognizedPeopleState.collectAsState().value

    var selectedCameraId by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {

                    // 1. Recognized people horizontal list
                    if (recognizedPeopleState.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    } else if (recognizedPeopleState.error != null) {
                        Text(
                            text = "Error: ${recognizedPeopleState.error}",
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        RecognizedPeopleList(recognizedPeople = recognizedPeopleState.recognizedPeople)
                    }
                    StartServiceButton()
                    // 2. Camera list vertical LazyColumn
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(state.cameras.size) { index ->
                            val camera = state.cameras[index]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        selectedCameraId = camera.id
                                        navController.navigate("messages")
                                    },
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = if (camera.id == selectedCameraId)
                                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                else
                                    CardDefaults.cardColors()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Title: ${camera.title}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "ID: ${camera.id}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            WebRtcVideoScreen(
                                id = camera.id,
                                channel = 1,
                                modifier = Modifier
                                    .height(250.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }

                    if (state.loadingStream) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        }
                    }
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StartServiceButton() {
    val context = LocalContext.current

    Button(onClick = {
        val intent = Intent(context, PushNotificationService::class.java)
        context.startForegroundService(intent)
    }) {
        Text("Start Foreground Service")
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
                        text = person.recognizedDate.take(10), // Show date only (yyyy-MM-dd)
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
@Composable
fun WebRtcVideoScreen(
    id: Int,
    channel: Int,
    modifier: Modifier = Modifier,
    viewModel: WebRtcViewModel = hiltViewModel(key = "$id-$channel")
) {
    val videoTrack by viewModel.remoteVideoTrack.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.connectWebRtc(id = id, channel = channel)
    }

    Box(modifier = modifier) {
        if (videoTrack != null) {
            AndroidView(
                factory = { context ->
                    SurfaceViewRenderer(context).apply {
                        init(viewModel.eglBase.eglBaseContext, null)
                        setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
                        setZOrderMediaOverlay(true)
                    }
                },
                update = { renderer ->
                    videoTrack?.addSink(renderer)
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = "Connecting to camera $id...",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

fun base64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT)
        android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        android.util.Log.e("Base64", "Failed to decode image", e)
        null
    }
}