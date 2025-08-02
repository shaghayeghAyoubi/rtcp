package com.example.myapplication.data.remote.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import android.util.Base64
import java.io.IOException
import javax.inject.Inject


class SignalingApiImpl @Inject constructor(
    private val client: OkHttpClient
)  : SignalingApi {
    override suspend fun sendOfferAndGetAnswer(id: Int, channel: Int, offerSdp: String): String {
        val encoded = Base64.encodeToString(offerSdp.toByteArray(), Base64.NO_WRAP)
        val body = "data=$encoded".toRequestBody("application/x-www-form-urlencoded".toMediaType())
        val request = Request.Builder()
            .url("https://172.15.0.60:8443/stream/$id/channel/$channel/webrtc?uuid=$id&channel=$channel")
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        val answerEncoded = response.body?.string() ?: throw IOException("Empty response")
        return String(Base64.decode(answerEncoded, Base64.NO_WRAP))
    }
}
