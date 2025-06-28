package com.example.myapplication.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetCameraListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.webrtc.*
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.*

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
        // Initialize WebRTC
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
        peerConnectionFactory = PeerConnectionFactory.builder()
            .createPeerConnectionFactory()

        fetchCameras()
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
            _state.update { it.copy(loadingStream = true, streamError = null) }

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

                        override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}
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

                if (peerConnection == null) {
                    _state.update { it.copy(loadingStream = false, streamError = "Failed to create PeerConnection") }
                    return@launch
                }

                peerConnection?.createOffer(object : SdpObserver {
                    override fun onCreateSuccess(offer: SessionDescription) {
                        println("createOffer success, setting local description")

                        viewModelScope.launch(Dispatchers.Main) {
                            try {
                                peerConnection?.setLocalDescription(object : SdpObserver {
                                    override fun onSetSuccess() {
                                        println("setLocalDescription success, sending offer to server")
                                        sendOfferToServer(cameraId, uuid, channel, offer)
                                    }

                                    override fun onSetFailure(error: String?) {
                                        println("setLocalDescription failed: $error")
                                        _state.update { it.copy(loadingStream = false, streamError = error ?: "setLocalDescription failed") }
                                    }

                                    override fun onCreateSuccess(p0: SessionDescription?) {}
                                    override fun onCreateFailure(p0: String?) {}
                                }, offer)
                            } catch (e: Exception) {
                                println("Exception in setLocalDescription: ${e.message}")
                                _state.update { it.copy(loadingStream = false, streamError = e.message ?: "Error in setLocalDescription") }
                            }
                        }
                    }

                    override fun onCreateFailure(error: String?) {
                        println("createOffer failed: $error")
                        _state.update { it.copy(loadingStream = false, streamError = error ?: "SDP offer failed") }
                    }

                    override fun onSetSuccess() {}
                    override fun onSetFailure(p0: String?) {}
                }, constraints)

            } catch (e: Exception) {
                println("Exception in fetchCameraStream: ${e.message}")
                _state.update { it.copy(loadingStream = false, streamError = e.message ?: "Failed to fetch stream") }
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

        val client = getUnsafeOkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Offer send failed: ${e.message}")
                _state.update { it.copy(loadingStream = false, streamError = e.message ?: "Network error") }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    response.body?.string()?.let {
                        val answerJson = JSONObject(it)
                        val answer = SessionDescription(
                            SessionDescription.Type.ANSWER,
                            answerJson.getString("sdp")
                        )
                        println("Received answer, setting remote description")

                        viewModelScope.launch(Dispatchers.Main) {
                            peerConnection?.setRemoteDescription(object : SdpObserver {
                                override fun onSetSuccess() {
                                    println("setRemoteDescription success")
                                    _state.update { it.copy(loadingStream = false) }
                                }

                                override fun onSetFailure(error: String?) {
                                    println("setRemoteDescription failed: $error")
                                    _state.update { it.copy(loadingStream = false, streamError = error ?: "Failed to set answer") }
                                }

                                override fun onCreateSuccess(p0: SessionDescription?) {}
                                override fun onCreateFailure(p0: String?) {}
                            }, answer)
                        }
                    } ?: run {
                        println("Empty response from server")
                        _state.update { it.copy(loadingStream = false, streamError = "Empty response") }
                    }
                } catch (e: Exception) {
                    println("Exception parsing server response: ${e.message}")
                    _state.update { it.copy(loadingStream = false, streamError = e.message ?: "Error parsing response") }
                }
            }
        })
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun clearStreamState() {
        _state.update {
            it.copy(
                loadingStream = false,
                streamUrl = null,
                streamError = null
            )
        }
    }
}
