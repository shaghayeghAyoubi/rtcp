package com.example.myapplication.presentation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView


@Composable
fun RtspVideoPlayer(modifier: Modifier = Modifier, videoUrl: String) {
    val context = LocalContext.current

    // Create and remember ExoPlayer with RTSP support
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                val mediaItem = MediaItem.Builder()
                    .setUri(videoUrl)
                    .setMimeType(MimeTypes.APPLICATION_RTSP) // 👈 Important!
                    .build()
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
    }

    // Display using PlayerView inside Compose
    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = true
            }
        }
    )

    // Cleanup when the Composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}
