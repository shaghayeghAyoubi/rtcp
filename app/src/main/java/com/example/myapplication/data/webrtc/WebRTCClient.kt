package com.example.myapplication.data.webrtc

import android.util.Base64
import android.util.Log
import com.example.myapplication.data.remote.api.SignalingApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.webrtc.*

import kotlin.coroutines.resume

// data/webrtc/WebRTCClient.kt
class WebRTCClient(
    private val renderer: SurfaceViewRenderer,
    private val signalingApi: SignalingApi
) {
    suspend fun startConnection(id: Int, channel: Int) {
        // 1. Initialize EGL context
        val eglBase = EglBase.create()
        renderer.init(eglBase.eglBaseContext, null)
        renderer.setMirror(true)

        // 2. Initialize WebRTC (only once per app lifecycle ideally)
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(renderer.context)
                .createInitializationOptions()
        )

        val factory = PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .createPeerConnectionFactory()

        // 3. Create PeerConnection with STUN servers
        val rtcConfig = PeerConnection.RTCConfiguration(
            listOf(
                PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
            )
        ).apply {
            iceTransportsType = PeerConnection.IceTransportsType.ALL
        }

        val peerConnection = factory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onSignalingChange(state: PeerConnection.SignalingState) {
                Log.d("WebRTC", "Signaling state: $state")
            }

            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
                Log.d("WebRTC", "ICE connection state: $state")
            }

            override fun onAddStream(stream: MediaStream) {}

            override fun onTrack(transceiver: RtpTransceiver) {
                val track = transceiver.receiver.track() as? VideoTrack
                track?.addSink(renderer)
            }

            override fun onIceCandidate(candidate: IceCandidate) {}
            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {}
            override fun onAddTrack(receiver: RtpReceiver, streams: Array<out MediaStream>) {}
            override fun onDataChannel(channel: DataChannel) {}
            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {}
            override fun onConnectionChange(state: PeerConnection.PeerConnectionState) {}
            override fun onIceConnectionReceivingChange(p0: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState) {}
            override fun onSelectedCandidatePairChanged(event: CandidatePairChangeEvent) {}
            override fun onRemoveStream(stream: MediaStream) {}
            override fun onRenegotiationNeeded() {}
        }) ?: throw IllegalStateException("PeerConnection creation failed")

        // 4. Request to receive video
        peerConnection.addTransceiver(MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO)

        // 5. Create offer SDP
        val offer = suspendCancellableCoroutine<SessionDescription> { cont ->
            val constraints = MediaConstraints().apply {
                mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            }

            peerConnection.createOffer(object : SdpObserver {
                override fun onCreateSuccess(desc: SessionDescription?) {
                    desc?.let {
                        peerConnection.setLocalDescription(object : SdpObserver {
                            override fun onSetSuccess() {
                                cont.resume(it)
                            }


                            override fun onCreateSuccess(p0: SessionDescription?) {}
                            override fun onCreateFailure(p0: String?) {}
                            override fun onSetFailure(p0: String?) {
                            }
                        }, it)
                    }
                }

                override fun onCreateFailure(msg: String?) {
                    Log.e("WebRTC", "Offer creation failed: $msg")
                }

                override fun onSetSuccess() {}
                override fun onSetFailure(p0: String?) {}
            }, constraints)
        }

        // 6. Send offer to signaling server
        val answerSdp = signalingApi.sendOfferAndGetAnswer(id, channel, offer.description)

        // 7. Set remote SDP as answer
        val answer = SessionDescription(SessionDescription.Type.ANSWER, answerSdp)
        peerConnection.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {
                Log.d("WebRTC", "Remote SDP set successfully")
            }

            override fun onSetFailure(msg: String?) {
                Log.e("WebRTC", "Set remote fail: $msg")
            }

            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onCreateFailure(p0: String?) {}
        }, answer)
    }
}
