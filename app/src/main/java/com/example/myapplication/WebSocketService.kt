package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.model.FaceRecognitionMessageDto
import com.example.myapplication.domain.model.FaceRecognitionMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

class WebSocketService : Service() {

    private val stompClient: StompClient = Stomp.over(
        Stomp.ConnectionProvider.OKHTTP,
        "ws://172.15.0.60:7009/face-recognize"
    )

    private val compositeDisposable = CompositeDisposable()

    private val _messages = MutableStateFlow<List<FaceRecognitionMessage>>(emptyList())

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        connectAndSubscribe()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        stompClient.disconnect()
    }

    private fun startForegroundService() {
        val channelId = "websocket_channel"
        val channelName = "WebSocket Service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("WebSocket Service")
            .setContentText("Listening for events...")
            .build()

        startForeground(1, notification)
    }

    private fun connectAndSubscribe() {
        compositeDisposable.add(
            stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ lifecycleEvent ->
                    when (lifecycleEvent.type) {
                        LifecycleEvent.Type.OPENED -> {
                            Log.d("WebSocket", "✅ Connected")
                            subscribeToMessages()
                        }
                        LifecycleEvent.Type.ERROR -> {
                            Log.e("WebSocket", "❌ Error", lifecycleEvent.exception)
                        }
                        LifecycleEvent.Type.CLOSED -> {
                            Log.d("WebSocket", "⚠️ Disconnected -> retrying...")
                            stompClient.connect() // auto-reconnect
                        }
                        else -> Unit
                    }
                }, { error ->
                    Log.e("WebSocket", "❌ Lifecycle error", error)
                })
        )

        stompClient.connect()
    }

    private fun subscribeToMessages() {
        compositeDisposable.add(
            stompClient.topic("/topic/messages")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ message ->
                    Log.d("WebSocket", "📩 Message: ${message.payload}")
                    try {
                        val dto = Json.decodeFromString<FaceRecognitionMessageDto>(message.payload)
                        val domainMessage = dto.toDomain()
                        _messages.update { it + domainMessage }

                        // If condition met -> notify user
                        if (domainMessage.message == "OK") {
//                            PushNotificationService.showNotification(
//                                this,
//                                "Special Event",
//                                "A face has been recognized!"
//                            )
                        }
                    } catch (e: Exception) {
                        Log.e("WebSocket", "⚠️ Parse failed", e)
                    }
                }, { error ->
                    Log.e("WebSocket", "❌ Subscription error", error)
                })
        )
    }
}
