package com.example.myapplication.data.repository

import com.example.myapplication.data.remote.api.SignalingApi
import com.example.myapplication.data.webrtc.WebRTCClient
import com.example.myapplication.domain.repository.WebRTCRepository
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

// data/repository/WebRTCRepositoryImpl.kt
class WebRTCRepositoryImpl @Inject constructor(
    private val signalingApi: SignalingApi
) : WebRTCRepository {
    override suspend fun connectWebRTC(id: Int, channel: Int, renderer: SurfaceViewRenderer) {
        WebRTCClient(renderer, signalingApi).startConnection(id, channel)
    }
}