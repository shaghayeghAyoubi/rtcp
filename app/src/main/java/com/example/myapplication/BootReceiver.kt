package com.example.myapplication


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

/**
 * Receives BOOT_COMPLETED and restarts the foreground websocket service.
 * Make sure the package above matches the manifest package.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val startIntent = Intent(context, WebSocketForegroundService::class.java).apply {
                action = WebSocketForegroundService.ACTION_START
            }
            // Start foreground service safely
            ContextCompat.startForegroundService(context, startIntent)
        }
    }
}