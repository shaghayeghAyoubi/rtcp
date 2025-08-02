package com.example.myapplication.utils

import android.content.Context
import android.util.Base64
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpTransceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.CandidatePairChangeEvent
import org.webrtc.RtpReceiver
import org.webrtc.VideoTrack
import java.io.IOException
import java.util.concurrent.Executors

//class WebRTCClient(
//    private val context: Context,
//    private val surfaceView: SurfaceViewRenderer
//) {
//    private lateinit var peerConnectionFactory: PeerConnectionFactory
//    private var peerConnection: PeerConnection? = null
//    private val eglBase = EglBase.create()
//
//    init {
//        initializePeerConnectionFactory()
//    }
//
//    private fun initializePeerConnectionFactory() {
//        val options = PeerConnectionFactory.InitializationOptions.builder(context)
//            .createInitializationOptions()
//        PeerConnectionFactory.initialize(options)
//
//        peerConnectionFactory = PeerConnectionFactory.builder()
//            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
//            .createPeerConnectionFactory()
//    }
//
//    fun initSurface() {
//        surfaceView.init(eglBase.eglBaseContext, null)
//        surfaceView.setMirror(true)
//    }
//
//    fun initPeerConnection(
//        cameraId: Int,
//        channel: Int = 0,
//        onLoaded: () -> Unit,
//        onError: (String) -> Unit
//    ) {
//        val iceServers = listOf(
//            PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer(),
//            PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer()
//        )
//
//        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
//
//        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
//            override fun onSignalingChange(newState: PeerConnection.SignalingState) {
//                // Optional logging
//            }
//
//            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
//                Log.d("WebRTC", "ICE Connection: $state")
//            }
//
//            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {
//                Log.d("WebRTC", "ICE Gathering: $state")
//            }
//
//            override fun onIceCandidate(candidate: IceCandidate?) {
//                // Optional: Send to server if needed
//            }
//
//            override fun onTrack(transceiver: RtpTransceiver?) {
//                transceiver?.receiver?.track()?.let { track ->
//                    if (track is VideoTrack) {
//                        track.addSink(surfaceView)
//                        onLoaded()
//                    }
//                }
//            }
//
//            override fun onAddStream(stream: MediaStream?) {}
//            override fun onRemoveStream(stream: MediaStream?) {}
//            override fun onDataChannel(channel: DataChannel?) {}
//            override fun onRenegotiationNeeded() {}
//            override fun onIceConnectionReceivingChange(receiving: Boolean) {}
//            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {}
//        })
//
//        val constraints = MediaConstraints().apply {
//            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
//            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
//        }
//
//        peerConnection?.createOffer(object : SdpObserver {
//            override fun onCreateSuccess(desc: SessionDescription?) {
//                desc?.let {
//                    peerConnection?.setLocalDescription(this, it)
//                    sendOfferToServer(it, cameraId, channel, onError)
//                }
//            }
//
//            override fun onSetSuccess() {}
//            override fun onCreateFailure(error: String?) {
//                onError(error ?: "Offer creation failed")
//            }
//
//            override fun onSetFailure(error: String?) {
//                onError(error ?: "Set local description failed")
//            }
//        }, constraints)
//    }
//
//    private fun sendOfferToServer(
//        offer: SessionDescription,
//        cameraId: Int,
//        channel: Int,
//        onError: (String) -> Unit
//    ) {
//        val offerSdp = Base64.encodeToString(offer.description.toByteArray(), Base64.NO_WRAP)
//        val url = "https://172.15.0.60:8443/stream/$cameraId/channel/$channel/webrtc?uuid=$cameraId&channel=$channel"
//
//        val client = OkHttpClient()
//        val body = FormBody.Builder().add("data", offerSdp).build()
//        val request = Request.Builder().url(url).post(body).build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onResponse(call: Call, response: Response) {
//                val responseBody = response.body?.string()
//                if (response.isSuccessful && responseBody != null) {
//                    val answerSdp = String(Base64.decode(responseBody, Base64.NO_WRAP))
//                    peerConnection?.setRemoteDescription(object : SdpObserver {
//                        override fun onSetSuccess() {}
//                        override fun onSetFailure(p0: String?) {}
//                        override fun onCreateSuccess(p0: SessionDescription?) {}
//                        override fun onCreateFailure(p0: String?) {}
//                    }, SessionDescription(SessionDescription.Type.ANSWER, answerSdp))
//                } else {
//                    onError("Failed to receive SDP answer from server")
//                }
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                onError("Request failed: ${e.message}")
//            }
//        })
//    }
//}
class WebRTCManager(
    private val context: Context,
    private val cameraId: Int,
    private val channel: Int,
    private val surfaceViewRenderer: SurfaceViewRenderer
) {

    private val eglBase: EglBase = EglBase.create()
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null

    init {
        initializePeerConnectionFactory()
        initializeSurfaceView()
    }

    private fun initializePeerConnectionFactory() {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)

        val encoderFactory = DefaultVideoEncoderFactory(
            eglBase.eglBaseContext, true, true
        )
        val decoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

        peerConnectionFactory = PeerConnectionFactory
            .builder()
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    private fun initializeSurfaceView() {
        surfaceViewRenderer.init(eglBase.eglBaseContext, null)
        surfaceViewRenderer.setMirror(true)
    }

    fun startConnection() {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)

        peerConnection = peerConnectionFactory?.createPeerConnection(
            rtcConfig,
            object : PeerConnection.Observer {
                override fun onIceCandidate(candidate: IceCandidate?) {
                    Log.d("WebRTC", "ICE candidate: $candidate")
                    candidate?.let { peerConnection?.addIceCandidate(it) }
                }

                override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
                    TODO("Not yet implemented")
                }

                override fun onAddStream(mediaStream: MediaStream?) {
                    Log.d("WebRTC", "AddStream")
                }

                override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {}

                override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {}

                override fun onDataChannel(dc: DataChannel?) {}

                override fun onIceConnectionReceivingChange(p0: Boolean) {}

                override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) {}

                override fun onRemoveStream(mediaStream: MediaStream?) {}

                override fun onRenegotiationNeeded() {}

                override fun onSignalingChange(state: PeerConnection.SignalingState?) {}

                override fun onTrack(transceiver: RtpTransceiver?) {
                    Log.d("WebRTC", "Remote track received")
                    val track = transceiver?.receiver?.track() as? VideoTrack
                    track?.addSink(surfaceViewRenderer)
                }

                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                    Log.d("WebRTC", "Connection state changed: $newState")
                }

                override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState?) {}

                override fun onSelectedCandidatePairChanged(event: CandidatePairChangeEvent?) {}
            }
        )

        createAndSendOffer()
    }

    private fun createAndSendOffer() {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"))
        }

        peerConnection?.createOffer(object : SdpObserver {
            override fun onCreateSuccess(description: SessionDescription?) {
                description?.let {
                    peerConnection?.setLocalDescription(object : SdpObserver {
                        override fun onSetSuccess() {
                            Log.d("WebRTC", "Local SDP set successfully")
                            sendOfferToServer(it)
                        }

                        override fun onSetFailure(error: String?) {
                            Log.e("WebRTC", "Local SDP set failed: $error")
                        }

                        override fun onCreateSuccess(p0: SessionDescription?) {}
                        override fun onCreateFailure(p0: String?) {}
                    }, it)
                }
            }

            override fun onSetSuccess() {}
            override fun onCreateFailure(error: String?) {
                Log.e("WebRTC", "SDP offer creation failed: $error")
            }

            override fun onSetFailure(error: String?) {}
        }, constraints)
    }

    private fun sendOfferToServer(sessionDescription: SessionDescription) {
        val offerSdp = sessionDescription.description
        val encodedSdp = Base64.encodeToString(offerSdp.toByteArray(), Base64.NO_WRAP)

        val url = "https://172.15.0.60:8443/stream/$cameraId/channel/$channel/webrtc?uuid=$cameraId&channel=$channel"

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("data", encodedSdp)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("WebRTC", "Signaling server error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("WebRTC", "Failed to get answer SDP: ${response.code}")
                    return
                }

                val encodedAnswer = response.body?.string() ?: ""
                val decodedAnswer = String(Base64.decode(encodedAnswer, Base64.NO_WRAP))

                val answer = SessionDescription(SessionDescription.Type.ANSWER, decodedAnswer)
                peerConnection?.setRemoteDescription(object : SdpObserver {
                    override fun onSetSuccess() {
                        Log.d("WebRTC", "Remote SDP set successfully")
                    }

                    override fun onSetFailure(error: String?) {
                        Log.e("WebRTC", "Remote SDP set failed: $error")
                    }

                    override fun onCreateSuccess(p0: SessionDescription?) {}
                    override fun onCreateFailure(p0: String?) {}
                }, answer)
            }
        })
    }

    fun release() {
        Log.d("WebRTC", "Releasing WebRTC resources")
        peerConnection?.dispose()
        peerConnectionFactory?.dispose()
        surfaceViewRenderer.release()
        eglBase.release()
    }
}

