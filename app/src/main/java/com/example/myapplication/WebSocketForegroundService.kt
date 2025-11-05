package com.example.myapplication



import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.datasource.local.NotificationFilterLocalDataSource
import com.example.myapplication.domain.model.FaceRecognitionMessage
import com.example.myapplication.domain.model.NotificationFilter
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


// WebSocketForegroundService.kt
@AndroidEntryPoint
class WebSocketForegroundService : LifecycleService() {

    @Inject lateinit var webSocketManager: WebSocketManager

    @Inject lateinit var notificationFilterLocalDataSource: NotificationFilterLocalDataSource


    private val serviceScope = lifecycleScope // available on LifecycleService

    companion object {
        const val ACTION_START = "com.example.app.action.START_WEBSOCKET_SERVICE"
        const val ACTION_STOP  = "com.example.app.action.STOP_WEBSOCKET_SERVICE"
        const val FOREGROUND_CHANNEL_ID = "ws_foreground_channel"
        const val MESSAGE_CHANNEL_ID    = "ws_messages_channel"
        const val FOREGROUND_NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START -> {
                startAsForeground()
                startCollectingMessages()
                // Trigger initial connect using stored token (WebSocketManager.connectAsync())
                webSocketManager.connectAsync()
            }
            ACTION_STOP -> {
                stopForegroundService()
            }
            else -> {
                // default: start
                startAsForeground()
                startCollectingMessages()
                webSocketManager.connectAsync()
            }
        }
        // If killed, let system recreate (START_STICKY - attempt to keep service running)
        return START_STICKY
    }

    private fun startAsForeground() {
        val notification = buildForegroundNotification()
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun stopForegroundService() {
        serviceScope.launch {
            webSocketManager.disconnect()
        }
        stopForeground(true)
        stopSelf()
    }

    private fun startCollectingMessages() {
        serviceScope.launch {
            // Combine filter and messages -> react whenever either changes
            combine(
                notificationFilterLocalDataSource.getFilter(),
                webSocketManager.messages
            ) { filter, messages -> filter to messages }
                .collect { (filter, list) ->
                    if (list.isNotEmpty()) {
                        val latest = list.last()

                        val shouldNotify = when (filter) {
                            NotificationFilter.ALL -> true
                            NotificationFilter.ONLY_OK -> latest.message == "OK"
                            NotificationFilter.ONLY_FORBIDDEN -> latest.message == "ONLY_FORBIDDEN"
                        }

                        if (shouldNotify) {
                            postIncomingMessageNotification(latest)
                        }
                    }
                }
        }
    }

    private fun postIncomingMessageNotification(msg: FaceRecognitionMessage) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("open_screen", "messages")
            putExtra("message_data", Gson().toJson(msg)) // ✅ خود پیام رو هم به صورت JSON بفرست
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            msg.nearestNeighbourId.hashCode(), // هر نوتیفیکیشن یه request code منحصر‌به‌فرد داشته باشه
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // unique id per notification if you want multiple
        val notifId = System.currentTimeMillis().toInt()

        val notification = NotificationCompat.Builder(this, MESSAGE_CHANNEL_ID)
            .setContentTitle("Face recognition alert")
            .setContentText("Forbidden event detected")
            .setSmallIcon(R.drawable.ic_notification) // replace with your icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(notifId, notification)
    }

    private fun buildForegroundNotification(): Notification {
        val contentIntent = Intent(this, MainActivity::class.java).let { intent ->
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        return NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setContentTitle("MyApp — Live connection")
            .setContentText("Listening for face recognition events")
            .setSmallIcon(R.drawable.ic_notification) // replace with your icon
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)

            // Foreground channel (low importance usually; user must see the persistent notification)
            nm?.createNotificationChannel(
                NotificationChannel(
                    FOREGROUND_CHANNEL_ID,
                    "Connection",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Shows that the app keeps a live websocket connection"
                }
            )

            // Message channel (important alerts)
            nm?.createNotificationChannel(
                NotificationChannel(
                    MESSAGE_CHANNEL_ID,
                    "Face events",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for forbidden/alert events"
                    setShowBadge(true)
                }
            )
        }
    }

    override fun onDestroy() {
        serviceScope.launch {
            webSocketManager.disconnect()
        }
        super.onDestroy()
    }


}