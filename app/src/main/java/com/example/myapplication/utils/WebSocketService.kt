//package com.example.myapplication.utils
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.IBinder
//import android.util.Log
//import android.widget.Toast
//import androidx.core.app.NotificationCompat
//import com.example.myapplication.data.mapper.toDomain
//import com.example.myapplication.data.model.FaceRecognitionMessageDto
//import dagger.hilt.android.AndroidEntryPoint
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.CompositeDisposable
//import io.reactivex.schedulers.Schedulers
//import kotlinx.serialization.json.Json
//import ua.naiksoftware.stomp.Stomp
//import ua.naiksoftware.stomp.StompClient
//import ua.naiksoftware.stomp.dto.LifecycleEvent
//
//@AndroidEntryPoint
//class WebSocketService : Service() {
//
//    private val CHANNEL_ID = "WebSocketServiceChannel"
//
//    private lateinit var stompClient: StompClient
//    private val compositeDisposable = CompositeDisposable()
//
//    override fun onCreate() {
//        super.onCreate()
//        createNotificationChannel()
//
//        // Start as a foreground service immediately
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("WebSocket Service")
//            .setContentText("Listening for messages...")
//            .setSmallIcon(android.R.drawable.stat_notify_sync)
//            .build()
//
//        startForeground(1, notification)
//
//        connectAndSubscribe()
//    }
//
//    private fun connectAndSubscribe() {
//        stompClient = Stomp.over(
//            Stomp.ConnectionProvider.OKHTTP,
//            "ws://172.15.0.60:7009/face-recognize"
//        )
//
//        compositeDisposable.add(
//            stompClient.lifecycle()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ lifecycleEvent ->
//                    when (lifecycleEvent.type) {
//                        LifecycleEvent.Type.OPENED -> {
//                            Log.d("WebSocketService", "✅ Connected")
//                            subscribeToMessages()
//                        }
//                        LifecycleEvent.Type.ERROR -> {
//                            Log.e("WebSocketService", "❌ Error", lifecycleEvent.exception)
//                        }
//                        LifecycleEvent.Type.CLOSED -> {
//                            Log.d("WebSocketService", "⚠️ Disconnected")
//                        }
//                        else -> Unit
//                    }
//                }, { error ->
//                    Log.e("WebSocketService", "❌ Lifecycle subscription error", error)
//                })
//        )
//
//        stompClient.connect()
//    }
//
//    private fun subscribeToMessages() {
//        compositeDisposable.add(
//            stompClient.topic("/topic/messages")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ message ->
//                    Log.d("WebSocketService", "📩 Message received: ${message.payload}")
//                    try {
//                        val dto = Json.decodeFromString<FaceRecognitionMessageDto>(message.payload)
//                        val domainMessage = dto.toDomain()
//
//                        // Show notification if needed
//                        if (domainMessage.message == "OK") {
//                            showForbiddenNotification("⚠ Forbidden message received!")
//                        }
//                    } catch (e: Exception) {
//                        Log.e("WebSocketService", "⚠ Failed to parse message", e)
//                    }
//                }, { error ->
//                    Log.e("WebSocketService", "❌ Subscription error", error)
//                })
//        )
//    }
//
//    private fun showForbiddenNotification(msg: String) {
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Alert")
//            .setContentText(msg)
//            .setSmallIcon(android.R.drawable.stat_notify_error)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.notify(2, notification)
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val serviceChannel = NotificationChannel(
//                CHANNEL_ID,
//                "WebSocket Service Channel",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val manager = getSystemService(NotificationManager::class.java)
//            manager?.createNotificationChannel(serviceChannel)
//        }
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        // Service is sticky so it restarts automatically
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//        stompClient.disconnect()
//        compositeDisposable.dispose()
//        super.onDestroy()
//        Toast.makeText(this, "WebSocket service stopped", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}