package com.example.myapplication.data.remote.api

interface SignalingApi {
    suspend fun sendOfferAndGetAnswer(id: Int, channel: Int, offerSdp: String): String
}