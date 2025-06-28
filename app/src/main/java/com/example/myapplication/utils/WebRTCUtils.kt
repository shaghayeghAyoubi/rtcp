package com.example.myapplication.utils

import android.content.Context
import android.util.Base64
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
import org.webrtc.VideoTrack
import java.io.IOException

class WebRTCClient(
    private val context: Context,
    private val surfaceView: SurfaceViewRenderer
) {
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private var peerConnection: PeerConnection? = null
    private val eglBase = EglBase.create()
    private val mediaStream = MediaStream(0)

    fun initPeerConnection(cameraId: Int, channel: Int, onLoaded: () -> Unit, onError: (String) -> Unit) {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer(),
            PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer(),
        )

        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)

        peerConnectionFactory = PeerConnectionFactory
            .builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .createPeerConnectionFactory()

        surfaceView.init(eglBase.eglBaseContext, null)
        surfaceView.setMirror(true)

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onSignalingChange(newState: PeerConnection.SignalingState) {
                if (newState == PeerConnection.SignalingState.HAVE_LOCAL_OFFER) {
                    sendOfferToServer(cameraId, channel)
                }
            }

            override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                TODO("Not yet implemented")
            }

            override fun onIceConnectionReceivingChange(p0: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
                TODO("Not yet implemented")
            }

            override fun onTrack(transceiver: RtpTransceiver?) {
                transceiver?.receiver?.track()?.let { track ->
                    if (track is VideoTrack) {
                        track.addSink(surfaceView)
                        onLoaded()
                    }
                }
            }

            override fun onIceCandidate(candidate: IceCandidate?) {
                // Optional: Send to server if needed
            }

            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
                TODO("Not yet implemented")
            }

            override fun onAddStream(p0: MediaStream?) {
                TODO("Not yet implemented")
            }

            override fun onRemoveStream(p0: MediaStream?) {
                TODO("Not yet implemented")
            }

            override fun onDataChannel(p0: DataChannel?) {
                TODO("Not yet implemented")
            }

            override fun onRenegotiationNeeded() {
                TODO("Not yet implemented")
            }

            // Other overrides omitted for brevity...
        })

        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        }

        peerConnection?.createOffer(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {
                desc?.let {
                    peerConnection?.setLocalDescription(this, it)
                }
            }

            override fun onSetSuccess() {}

            override fun onCreateFailure(error: String?) {
                onError(error ?: "Offer creation failed")
            }

            override fun onSetFailure(error: String?) {
                onError(error ?: "Set local description failed")
            }
        }, constraints)
    }

    private fun sendOfferToServer(cameraId: Int, channel: Int) {
        val offer = peerConnection?.localDescription ?: return
        val offerSdp = Base64.encodeToString(offer.description.toByteArray(), Base64.NO_WRAP)

        val url = "https://172.15.0.60:8443/stream/$cameraId/channel/$channel/webrtc?uuid=$cameraId&channel=$channel"

        val client = OkHttpClient()
        val body = FormBody.Builder().add("data", offerSdp).build()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val answerSdp = String(Base64.decode(responseBody, Base64.NO_WRAP))
                    peerConnection?.setRemoteDescription(
                        object : SdpObserver {
                            override fun onSetSuccess() {}
                            override fun onSetFailure(p0: String?) {}
                            override fun onCreateSuccess(p0: SessionDescription?) {}
                            override fun onCreateFailure(p0: String?) {}
                        },
                        SessionDescription(SessionDescription.Type.ANSWER, answerSdp)
                    )
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }
}

