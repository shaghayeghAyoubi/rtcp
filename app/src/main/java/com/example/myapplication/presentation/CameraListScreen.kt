package com.example.myapplication.presentation
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.SurfaceViewRenderer

@Composable
fun CameraListScreen(
    viewModel: CameraListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.error}")
            }
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize()) {
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
                                    viewModel.fetchCameraStream(camera.id,camera.id )
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Title: ${camera.title}", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "ID: ${camera.id}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // ✅ Show loading indicator over the entire screen
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

                // ✅ If stream URL is available, show your video player here
                state.streamUrl?.let {

                }
            }
        }
    }
}
@Composable
fun StreamViewer(
    streamUrl: String,
    modifier: Modifier = Modifier
) {
    // Create and remember the SurfaceViewRenderer as a state object
    val context = LocalContext.current
    val surfaceView = remember {
        SurfaceViewRenderer(context).apply {
            // initialize immediately if you want
        }
    }

    // Remember the WebRtcClient tied to the SurfaceViewRenderer
    val webRtcClient = remember(surfaceView) {
        WebRtcClient(surfaceView.context, surfaceView).apply { initialize() }
    }

    // Connect to the stream URL whenever it changes
    LaunchedEffect(streamUrl) {
        webRtcClient.connectToStream(streamUrl)
    }

    // Make sure to release the SurfaceViewRenderer when composable leaves composition
    DisposableEffect(surfaceView) {
        onDispose {
            surfaceView.release()
        }
    }

    // Now embed the SurfaceViewRenderer in Compose UI
    AndroidView(
        factory = { surfaceView },
        modifier = modifier
    )
}