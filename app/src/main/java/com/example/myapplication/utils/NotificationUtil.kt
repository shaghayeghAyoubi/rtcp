package com.example.myapplication.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat



object NotificationUtil {
    private const val CHANNEL_ID = "websocket_channel"
    private const val CHANNEL_NAME = "WebSocket Notifications"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun show(context: Context, title: String, text: String, id: Int = (System.currentTimeMillis().toInt())) {
        ensureChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // must be valid
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val mgr = ContextCompat.getSystemService(context, NotificationManager::class.java)
        mgr?.notify(id, builder.build())
    }
}