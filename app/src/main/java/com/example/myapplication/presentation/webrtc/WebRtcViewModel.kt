package com.example.myapplication.presentation.webrtc

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.MediaStreamTrack
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.RtpTransceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Base64
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

enum class WebRtcStatus {
    LOADING, CONNECTED, ERROR
}


@HiltViewModel
class WebRtcViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) : ViewModel(), PeerConnection.Observer {

    private val _remoteVideoTrack = MutableLiveData<VideoTrack?>()
    val remoteVideoTrack: LiveData<VideoTrack?> = _remoteVideoTrack

    private val _status = MutableLiveData<WebRtcStatus>()
    val status: LiveData<WebRtcStatus> = _status

    val eglBase = EglBase.create()
    private val peerConnectionFactory: PeerConnectionFactory
    private var peerConnection: PeerConnection? = null

    init {
        val options = PeerConnectionFactory.Options()
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions()
        )
        val encoderFactory = DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true)
        val decoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    fun connectWebRtc(id: Int, channel: Int) {
        _status.postValue(WebRtcStatus.LOADING)
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer(),
            PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer()
        )
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, this)

        peerConnection?.addTransceiver(
            MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
            RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.RECV_ONLY)
        )

        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"))
        }

        peerConnection?.createOffer(object : SdpObserver {
            override fun onCreateSuccess(offer: SessionDescription) {
                peerConnection?.setLocalDescription(object : SdpObserver {
                    override fun onSetSuccess() {
                        val url = "https://172.15.0.60:8443/stream/$id/channel/$channel/webrtc?uuid=$id&channel=$channel"
                        val data = Base64.encodeToString(offer.description.toByteArray(), Base64.NO_WRAP)
                        val requestBody = RequestBody.create(
                            "application/x-www-form-urlencoded; charset=UTF-8".toMediaTypeOrNull(),
                            "data=$data"
                        )
                        val request = Request.Builder().url(url).post(requestBody).build()

                        viewModelScope.launch(Dispatchers.IO) {
                            try {
                                okHttpClient.newCall(request).execute().use { resp ->
                                    val answerBase64 = resp.body?.string() ?: ""
                                    val answerSdp =
                                        String(Base64.decode(answerBase64, Base64.DEFAULT))
                                    withContext(Dispatchers.Main) {
                                        peerConnection?.setRemoteDescription(
                                            object : SdpObserver {
                                                override fun onSetSuccess() {
                                                    _status.postValue(WebRtcStatus.CONNECTED)
                                                }
                                                override fun onSetFailure(error: String) {
                                                    _status.postValue(WebRtcStatus.ERROR)
                                                }
                                                override fun onCreateSuccess(desc: SessionDescription) {}
                                                override fun onCreateFailure(err: String) {}
                                            },
                                            SessionDescription(
                                                SessionDescription.Type.ANSWER,
                                                answerSdp
                                            )
                                        )
                                    }
                                }
                            } catch (e : Exception) {
                                _status.postValue(WebRtcStatus.ERROR)
                            }
                        }
                    }
                    override fun onSetFailure(e: String) {
                        _status.postValue(WebRtcStatus.ERROR)
                    }

                    override fun onCreateSuccess(desc: SessionDescription) {}
                    override fun onCreateFailure(err: String)  {}
                }, offer)
            }
            override fun onCreateFailure(err: String) {
                _status.postValue(WebRtcStatus.ERROR)
            }
            override fun onSetSuccess() {}
            override fun onSetFailure(err: String) {}
        }, constraints)
    }

    // PeerConnection.Observer callbacks
    override fun onAddStream(stream: MediaStream) {}
    override fun onDataChannel(dc: DataChannel) {}
    override fun onIceConnectionReceivingChange(receiving: Boolean) {}
    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {}
    override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {}
    override fun onIceCandidate(candidate: IceCandidate) {}
    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {}
    override fun onSignalingChange(state: PeerConnection.SignalingState) {}
    override fun onRemoveStream(stream: MediaStream) {}
    override fun onRenegotiationNeeded() {}
    override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState) {}
    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState) {}

    override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<MediaStream>) {
        val track = receiver.track()
        if (track is VideoTrack) {
            _remoteVideoTrack.postValue(track)
        }
    }

    override fun onTrack(transceiver: RtpTransceiver) {
        transceiver.receiver.track()?.let { track ->
            if (track is VideoTrack) _remoteVideoTrack.postValue(track)
        }
    }

    fun release() {
        _remoteVideoTrack.value?.let { track ->
            track.setEnabled(false)
            track.dispose()
        }
        _remoteVideoTrack.postValue(null)
        peerConnection?.close()
        peerConnection = null
        _status.postValue(WebRtcStatus.ERROR) // or IDLE
    }

    override fun onCleared() {
        super.onCleared()
        release()
        eglBase.release()
    }
}
