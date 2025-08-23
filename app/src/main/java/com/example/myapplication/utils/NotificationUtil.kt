package com.example.myapplication.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

object NotificationUtil {
    const val CHANNEL_ID = "PushNotificationChannel"
    const val CHANNEL_NAME = "Push Notifications"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(NotificationManager::class.java)
            val existing = mgr?.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Push notifications from server" }
                mgr?.createNotificationChannel(channel)
            }
        }
    }

    fun show(context: Context, title: String, text: String, id: Int = (System.currentTimeMillis().toInt())) {
        ensureChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            // use a valid icon; android built-in works for quick test:
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val mgr = ContextCompat.getSystemService(context, NotificationManager::class.java)
        mgr?.notify(id, builder.build())
    }
}