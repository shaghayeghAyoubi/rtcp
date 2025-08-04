package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.WebRTCRepository
import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer

// domain/usecase/StartWebRTCStreamingUseCase.kt
class StartWebRTCStreamingUseCase(
    private val webrtcRepository: WebRTCRepository
) {
    suspend operator fun invoke(id: Int, channel: Int, renderer: SurfaceViewRenderer, eglBaseContext: EglBase.Context) {
        webrtcRepository.connectWebRTC(id, channel, renderer, eglBaseContext)
    }
}
