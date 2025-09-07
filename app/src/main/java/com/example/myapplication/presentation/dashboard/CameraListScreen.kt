package com.example.myapplication.presentation.dashboard



import androidx.compose.foundation.background
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
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myapplication.presentation.dashboard.webrtc.WebRtcStatus
import com.example.myapplication.presentation.dashboard.webrtc.WebRtcViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraListScreen(
    navController: NavController,
    viewModel: CameraListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value


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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // --- Cameras ---
                    items(state.cameras.size) { index ->
                        val camera = state.cameras[index]

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
//                                .clickable {
//                                    selectedCameraId = camera.id
//                                    navController.navigate("messages")
//                                }
                        ) {
                            // --- Video ---
                            WebRtcVideoScreen(
                                id = camera.id,
                                channel = 1,
                                modifier = Modifier
                                    .height(250.dp)
                                    .fillMaxWidth()
                            )

                            // --- Camera title overlay ---
                            Text(
                                text = camera.title,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 8.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
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

@Composable
fun WebRtcVideoScreen(
    id: Int,
    channel: Int,
    modifier: Modifier = Modifier,
    // keep the hiltViewModel as you had it (optionally pass a navBackStackEntry if you want a different scope)
    viewModel: WebRtcViewModel = hiltViewModel(key = "$id-$channel")
) {
    val videoTrack by viewModel.remoteVideoTrack.observeAsState()
    val status by viewModel.status.observeAsState(WebRtcStatus.LOADING)

    val rendererRef = remember { mutableStateOf<SurfaceViewRenderer?>(null) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Only attempt to connect when we actually need a connection.
    // Use status in the key so this effect will run when status changes.
    LaunchedEffect(id, channel, status) {
        // If already connected or connecting, don't reconnect.
        if (status == WebRtcStatus.CONNECTED || status == WebRtcStatus.CONNECTING) return@LaunchedEffect
        // otherwise, ask the ViewModel to connect
        viewModel.connectWebRtc(id = id, channel = channel)
    }

    // Create the SurfaceViewRenderer exactly once and keep a reference for sink management.
    AndroidView(
        factory = { ctx ->
            SurfaceViewRenderer(ctx).apply {
                // initialize with EGL context from ViewModel (assuming viewModel.eglBase exists)
                init(viewModel.eglBase.eglBaseContext, null)
                setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
                setZOrderMediaOverlay(true)
                rendererRef.value = this
            }
        },
        update = { /* nothing here; sink management handled below */ },
        modifier = modifier
    )

    // Manage adding and removing sinks based on current videoTrack & renderer lifecycle.
    DisposableEffect(videoTrack, rendererRef.value) {
        val renderer = rendererRef.value
        if (videoTrack != null && renderer != null) {
            // add sink when both track + renderer are available
            videoTrack!!.addSink(renderer)
        }

        onDispose {
            // remove sink on disposal (so we don't leak or double-add on recompose)
            if (videoTrack != null && renderer != null) {
                try {
                    videoTrack!!.removeSink(renderer)
                } catch (_: Exception) { /* safe-guard if already removed */ }
            }
            // don't force-release renderer here if you want to reuse it across short-lived recompositions.
            // If you truly want to tear it down when the composable is destroyed for good, you can call:
            // renderer?.release()
            // rendererRef.value = null
        }
    }

    // Optional: show UI for statuses and loading
    Box(modifier = Modifier.fillMaxSize()) {
        when (status) {
            WebRtcStatus.LOADING -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            WebRtcStatus.ERROR -> {
                Text(
                    text = "Failed to connect to camera $id",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            WebRtcStatus.CONNECTED -> {
                // video is rendered by the SurfaceViewRenderer that we created above
            }

            WebRtcStatus.CONNECTING -> TODO()
        }
    }

    // IMPORTANT: do NOT call viewModel.release() inside a DisposableEffect that runs on every leave.
    // Instead, let the ViewModel release resources in onCleared(), or release when the NavBackStackEntry is destroyed.
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