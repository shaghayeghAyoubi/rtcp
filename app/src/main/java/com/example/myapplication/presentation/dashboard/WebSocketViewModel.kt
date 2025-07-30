package com.example.myapplication.presentation.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.model.FaceRecognitionMessageDto
import com.example.myapplication.domain.model.FaceRecognitionMessage


import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class WebSocketViewModel : ViewModel() {

    private val stompClient: StompClient = Stomp.over(
        Stomp.ConnectionProvider.OKHTTP,
        "ws://172.15.0.60:7009/face-recognize"
    )

    private val compositeDisposable = CompositeDisposable()

    private val _messages = MutableStateFlow<List<FaceRecognitionMessage>>(emptyList())
    val messages: StateFlow<List<FaceRecognitionMessage>> = _messages

    init {
        connectAndSubscribe()
    }

    private fun connectAndSubscribe() {
        compositeDisposable.add(
            stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ lifecycleEvent ->
                    when (lifecycleEvent.type) {
                        LifecycleEvent.Type.OPENED -> {
                            Log.d("WebSocket", "✅ WebSocket Connected")
                            subscribeToMessages()
                        }
                        LifecycleEvent.Type.ERROR -> {
                            Log.e("WebSocket", "❌ WebSocket Error", lifecycleEvent.exception)
                        }
                        LifecycleEvent.Type.CLOSED -> {
                            Log.d("WebSocket", "⚠️ WebSocket Disconnected")
                        }
                        else -> Unit
                    }
                }, { error ->
                    Log.e("WebSocket", "❌ Lifecycle subscription error", error)
                })
        )

        stompClient.connect()
    }

    private fun subscribeToMessages() {
        Log.d("WebSocket", "🧷 Subscribing to topic...")
        compositeDisposable.add(
            stompClient.topic("/topic/messages")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ message ->
                    Log.d("WebSocket", "📩 Message received: ${message.payload}")
                    try {
                        val dto = Json.decodeFromString<FaceRecognitionMessageDto>(message.payload)
                        val domainMessage = dto.toDomain()
                        _messages.update { it + domainMessage }
                    } catch (e: Exception) {
                        Log.e("WebSocket", "⚠️ Failed to parse message", e)
                    }
                }, { error ->
                    Log.e("WebSocket", "❌ Subscription error", error)
                })
        )
    }

    override fun onCleared() {
        stompClient.disconnect()
        compositeDisposable.dispose()
        super.onCleared()
    }
}
