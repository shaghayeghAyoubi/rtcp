package com.example.myapplication.domain.repository

import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer

interface WebRTCRepository {
    suspend fun connectWebRTC(id: Int, channel: Int, renderer: SurfaceViewRenderer, eglBaseContext: EglBase.Context)
}