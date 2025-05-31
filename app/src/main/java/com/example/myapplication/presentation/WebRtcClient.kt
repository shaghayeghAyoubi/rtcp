//package com.example.myapplication.presentation
//import android.content.Context
//import android.util.Log
//import org.webrtc.*
//
//class WebRtcClient(
//    private val context: Context,
//    private val surfaceView: SurfaceViewRenderer
//) {
//    private lateinit var peerConnectionFactory: PeerConnectionFactory
//    private var peerConnection: PeerConnection? = null
//    private lateinit var eglBase: EglBase
//
//    fun initialize() {
//        eglBase = EglBase.create()
//        surfaceView.init(eglBase.eglBaseContext, null)
//        surfaceView.setMirror(true)
//
//        PeerConnectionFactory.initialize(
//            PeerConnectionFactory.InitializationOptions.builder(context)
//                .setEnableInternalTracer(true)
//                .createInitializationOptions()
//        )
//
//        peerConnectionFactory = PeerConnectionFactory.builder()
//            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
//            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
//            .createPeerConnectionFactory()
//    }
//
//    fun connectToStream(sdpOffer: String) {
//        val remoteSdp = SessionDescription(SessionDescription.Type.OFFER, sdpOffer)
//
//        val rtcConfig = PeerConnection.RTCConfiguration(emptyList())
//        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
//            override fun onAddStream(mediaStream: MediaStream?) {
//                mediaStream?.videoTracks?.get(0)?.addSink(surfaceView)
//            }
//
//            override fun onTrack(transceiver: RtpTransceiver?) {
//                transceiver?.receiver?.track()?.let {
//                    if (it is VideoTrack) {
//                        it.addSink(surfaceView)
//                    }
//                }
//            }
//
//            override fun onIceCandidate(candidate: IceCandidate?) {
//                // Send ICE to signaling server (optional)
//            }
//
//            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
//                Log.d("WebRTC", "ICE connection state: $state")
//            }
//
//            // other overrides...
//            override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
//            override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
//            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}
//            override fun onDataChannel(p0: DataChannel?) {}
//            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {}
//            override fun onRemoveStream(p0: MediaStream?) {}
//            override fun onRenegotiationNeeded() {}
//        })
//
//        peerConnection?.setRemoteDescription(object : SdpObserverAdapter() {
//            override fun onSetSuccess() {
//                peerConnection?.createAnswer(object : SdpObserverAdapter() {
//                    override fun onCreateSuccess(desc: SessionDescription) {
//                        peerConnection?.setLocalDescription(SdpObserverAdapter(), desc)
//                        // Optionally send this answer back to server
//                    }
//                }, MediaConstraints())
//            }
//        }, remoteSdp)
//    }
//
//    fun release() {
//        peerConnection?.dispose()
//        peerConnectionFactory.dispose()
//        surfaceView.release()
//    }
//}
//
//abstract class SdpObserverAdapter : SdpObserver {
//    override fun onSetSuccess() {}
//    override fun onSetFailure(p0: String?) {}
//    override fun onCreateSuccess(p0: SessionDescription?) {}
//    override fun onCreateFailure(p0: String?) {}
//}
