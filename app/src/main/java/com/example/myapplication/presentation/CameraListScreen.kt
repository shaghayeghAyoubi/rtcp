package com.example.myapplication.presentation



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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
//import com.example.myapplication.data.webrtc.WebRTCClient

import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpTransceiver
import org.webrtc.*
import android.util.Base64
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.FormBody
import kotlinx.coroutines.*
import android.util.Log
import android.content.Context
import androidx.compose.runtime.livedata.observeAsState
import com.example.myapplication.presentation.webrtc.WebRtcViewModel


@Composable
fun CameraListScreen(
    navController: NavController,
    viewModel: CameraListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value

    // Track selected camera locally to highlight UI or for video player
    var selectedCameraId by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                // Main loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                // Error message
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
                // Main camera list
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
                            channel = 1, // اگر کانال مشخصه، این رو درست بده
                            modifier = Modifier
                                .height(250.dp) // ✅ your chosen height
                                .fillMaxWidth()
                        )
                    }
                }

                // Optional loading overlay when streaming
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


//@Composable
//fun WebRtcVideoScreen(
//    id: Int,
//    channel: Int,
//    modifier: Modifier = Modifier, // ✅ allow height/width to be passed
//    viewModel: WebRtcViewModel = hiltViewModel()
//) {
//    val videoTrack by viewModel.remoteVideoTrack.observeAsState()
//    // Launch connection once
//    LaunchedEffect(Unit) {
//        viewModel.connectWebRtc(id = id, channel = channel)
//    }
//    // Display a SurfaceViewRenderer for the video
//    Box(modifier = Modifier.fillMaxSize()) {
//        videoTrack?.let { track ->
//            AndroidView(factory = { context ->
//                SurfaceViewRenderer(context).apply {
//                    init(viewModel.eglBase.eglBaseContext, null)
//                    setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
//                    setZOrderMediaOverlay(true)
//                }
//            }, update = { renderer ->
//                // Attach the remote video track to the renderer
//                track.addSink(renderer)
//            }, modifier = modifier.fillMaxSize()
//            )
//        }
//    }
//}
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