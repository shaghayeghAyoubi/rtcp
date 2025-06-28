package com.example.myapplication.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.SurfaceViewRenderer

@Composable
fun WebRTCVideoView(
    viewModel: CameraListViewModel
) {
    val context = LocalContext.current
    val eglBaseContext = viewModel.getEglBaseContext()
    val videoTrack = viewModel.getVideoTrack()

    AndroidView(factory = {
        val surfaceView = SurfaceViewRenderer(context).apply {
            init(eglBaseContext, null)
            setMirror(true)
        }

        videoTrack?.addSink(surfaceView)

        surfaceView
    }, modifier = Modifier
        .fillMaxSize()
        .padding(8.dp))
}
