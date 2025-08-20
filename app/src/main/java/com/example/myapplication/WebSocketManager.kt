package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.model.FaceRecognitionMessageDto
import com.example.myapplication.domain.model.FaceRecognitionMessage
import com.example.myapplication.domain.repository.TokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import javax.inject.Inject
import javax.inject.Singleton
//
//@Singleton
//class WebSocketManager @Inject constructor(
//    @ApplicationContext private val context: Context,
//    private val tokenRepository: TokenRepository
//) {
//
//    private val stompClient: StompClient = Stomp.over(
//        Stomp.ConnectionProvider.OKHTTP,
//        "ws://172.15.0.94:7009/face-recognize"
//    )
//
//    private val compositeDisposable = CompositeDisposable()
//
//    private val _messages = MutableStateFlow<List<FaceRecognitionMessage>>(emptyList())
//    val messages: StateFlow<List<FaceRecognitionMessage>> = _messages.asStateFlow()
//
//    init {
//        connect()
//    }
//
//    private fun connect() {
//        compositeDisposable.add(
//            stompClient.lifecycle()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ event ->
//                    when (event.type) {
//                        LifecycleEvent.Type.OPENED -> {
//                            Log.d("WebSocket", "✅ Connected")
//                            addAuthorizationHeaderAndConnect()
//                            subscribeToMessages()
//                        }
//                        LifecycleEvent.Type.ERROR -> Log.e("WebSocket", "❌ Error", event.exception)
//                        LifecycleEvent.Type.CLOSED -> Log.d("WebSocket", "⚠️ Connection closed")
//                        else -> {}
//                    }
//                }, { error ->
//                    Log.e("WebSocket", "❌ Lifecycle error", error)
//                })
//        )
//    }
//
//    private fun addAuthorizationHeaderAndConnect() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val token = tokenRepository.getAccessToken().firstOrNull()
//            val headers = token?.let { listOf(StompHeader("Authorization", "Bearer $it")) } ?: emptyList()
//            Log.d("WebSocket", "Connecting with headers: $headers")
//            stompClient.connect(headers)
//        }
//    }
//
//    private fun subscribeToMessages() {
//        Log.d("WebSocket", "🧷 Subscribing to /topic/messages")
//        compositeDisposable.add(
//            stompClient.topic("/topic/messages")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ message ->
//                    try {
//                        val dto = Json.decodeFromString<FaceRecognitionMessageDto>(message.payload)
//                        val domainMessage = dto.toDomain()
//                        _messages.update { it + domainMessage }
//
//                        if (domainMessage.message == "OK") {
//                            sendForbiddenNotification()
//                        }
//                    } catch (e: Exception) {
//                        Log.e("WebSocket", "⚠️ Failed to parse message", e)
//                    }
//                }, { error ->
//                    Log.e("WebSocket", "❌ Subscription error", error)
//                })
//        )
//    }
//
//    private fun sendForbiddenNotification() {
//        val intent = Intent(context, PushNotificationService::class.java)
//            .putExtra("msg", "⚠ Forbidden message received!")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intent)
//        } else {
//            context.startService(intent)
//        }
//    }
//
//    fun disconnect() {
//        stompClient.disconnect()
//        compositeDisposable.clear()
//    }
//}
@Singleton
class WebSocketManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenRepository: TokenRepository
) {
    private var stompClient: StompClient? = null
    private val compositeDisposable = CompositeDisposable()

    private val _messages = MutableStateFlow<List<FaceRecognitionMessage>>(emptyList())
    val messages: StateFlow<List<FaceRecognitionMessage>> = _messages.asStateFlow()

    suspend fun connect() {
        val token = tokenRepository.getAccessToken().firstOrNull()
        val url = if (!token.isNullOrEmpty()) {
            "ws://172.15.0.94:7009/face-recognize?token=$token"
        } else {
            "ws://172.15.0.94:7009/face-recognize"
        }

        Log.d("WebSocket", "🔌 Connecting with URL: $url")

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url).apply {
            connect()
        }

        observeLifecycle()
        subscribeToMessages()
    }

    private fun observeLifecycle() {
        stompClient?.let { client ->
            compositeDisposable.add(
                client.lifecycle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ event ->
                        when (event.type) {
                            LifecycleEvent.Type.OPENED -> Log.d("WebSocket", "✅ Connected")
                            LifecycleEvent.Type.ERROR -> Log.e("WebSocket", "❌ Error", event.exception)
                            LifecycleEvent.Type.CLOSED -> Log.d("WebSocket", "⚠️ Connection closed")
                            else -> {}
                        }
                    }, { error ->
                        Log.e("WebSocket", "❌ Lifecycle error", error)
                    })
            )
        }
    }

    private fun subscribeToMessages() {
        stompClient?.let { client ->
            Log.d("WebSocket", "🧷 Subscribing to /topic/messages")
            compositeDisposable.add(
                client.topic("/topic/messages")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ message ->
                        try {
                            val dto = Json.decodeFromString<FaceRecognitionMessageDto>(message.payload)
                            val domainMessage = dto.toDomain()
                            _messages.update { it + domainMessage }

                            if (domainMessage.message == "OK") {
                                sendForbiddenNotification()
                            }
                        } catch (e: Exception) {
                            Log.e("WebSocket", "⚠️ Failed to parse message", e)
                        }
                    }, { error ->
                        Log.e("WebSocket", "❌ Subscription error", error)
                    })
            )
        }
    }

    private fun sendForbiddenNotification() {
        val intent = Intent(context, PushNotificationService::class.java)
            .putExtra("msg", "⚠ Forbidden message received!")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun disconnect() {
        stompClient?.disconnect()
        compositeDisposable.clear()
    }
}

