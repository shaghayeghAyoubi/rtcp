package com.example.myapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetCameraListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import androidx.compose.runtime.*
import org.webrtc.PeerConnection


@HiltViewModel
class CameraListViewModel @Inject constructor(
    private val getCameraListUseCase: GetCameraListUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CameraListState())
    val state: StateFlow<CameraListState> = _state

    private val eglBase = EglBase.create()
    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
    )

    private var peerConnectionFactory: PeerConnectionFactory
    private var peerConnection: PeerConnection? = null

    private val _videoTrack = MutableStateFlow<VideoTrack?>(null)
    val videoTrack: StateFlow<VideoTrack?> = _videoTrack

    init {
        fetchCameras()

        // Initialize WebRTC
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
        peerConnectionFactory = PeerConnectionFactory.builder()
            .createPeerConnectionFactory()
    }

    private fun fetchCameras() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val cameras = getCameraListUseCase()
                _state.value = _state.value.copy(cameras = cameras, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }

    fun fetchCameraStream(cameraId: Int, uuid: Int, channel: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(loadingStream = true, error = null) }

            try {
                val constraints = MediaConstraints()
                peerConnection = peerConnectionFactory.createPeerConnection(
                    PeerConnection.RTCConfiguration(iceServers),
                    object : PeerConnection.Observer {
                        override fun onAddStream(stream: MediaStream?) {
                            stream?.videoTracks?.firstOrNull()?.let {
                                _videoTrack.value = it
                            }
                        }

                        override fun onIceCandidate(candidate: IceCandidate?) {
                            candidate?.let { peerConnection?.addIceCandidate(it) }
                        }

                        // You can implement more callbacks if needed
                        override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {}
                        override fun onDataChannel(p0: DataChannel?) {}
                        override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}
                        override fun onIceConnectionReceivingChange(p0: Boolean) {}
                        override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
                        override fun onRemoveStream(p0: MediaStream?) {}
                        override fun onRenegotiationNeeded() {}
                        override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
                        override fun onTrack(transceiver: RtpTransceiver?) {}
                    }
                )

                peerConnection?.createOffer(object : SdpObserver {
                    override fun onCreateSuccess(offer: SessionDescription) {
                        peerConnection?.setLocalDescription(object : SdpObserver {
                            override fun onSetSuccess() {
                                sendOfferToServer(cameraId, uuid, channel, offer)
                            }

                            override fun onSetFailure(p0: String?) {}
                            override fun onCreateSuccess(p0: SessionDescription?) {}
                            override fun onCreateFailure(p0: String?) {}
                        }, offer)
                    }

                    override fun onCreateFailure(error: String?) {
                        _state.update { it.copy(loadingStream = false, error = error ?: "SDP offer failed") }
                    }

                    override fun onSetSuccess() {}
                    override fun onSetFailure(p0: String?) {}
                }, constraints)

            } catch (e: Exception) {
                _state.update { it.copy(loadingStream = false, error = e.message ?: "Failed to fetch stream") }
            }
        }
    }

    private fun sendOfferToServer(cameraId: Int, uuid: Int, channel: Int, offer: SessionDescription) {
        val url = "https://172.15.0.60:8443/stream/$cameraId/channel/$channel/webrtc?uuid=$uuid&channel=$channel"
        val json = JSONObject().apply {
            put("sdp", offer.description)
            put("type", "offer")
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _state.update { it.copy(loadingStream = false, error = e.message ?: "Network error") }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    val answerJson = JSONObject(it)
                    val answer = SessionDescription(
                        SessionDescription.Type.ANSWER,
                        answerJson.getString("sdp")
                    )
                    peerConnection?.setRemoteDescription(object : SdpObserver {
                        override fun onSetSuccess() {
                            _state.update { it.copy(loadingStream = false) }
                        }

                        override fun onSetFailure(p0: String?) {
                            _state.update { it.copy(loadingStream = false, error = p0 ?: "Failed to set answer") }
                        }

                        override fun onCreateSuccess(p0: SessionDescription?) {}
                        override fun onCreateFailure(p0: String?) {}
                    }, answer)
                } ?: run {
                    _state.update { it.copy(loadingStream = false, error = "Empty response") }
                }
            }
        })
    }
}