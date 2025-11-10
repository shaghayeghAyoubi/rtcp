package com.example.myapplication

// SharedNavigationManager.kt
object SharedNavigationManager {
    private var pendingMessageId: String? = null
    private var shouldOpenDialog: Boolean = false

    fun setPendingNavigation(messageId: String, openDialog: Boolean = true) {
        pendingMessageId = messageId
        shouldOpenDialog = openDialog
    }

    fun getPendingNavigation(): Pair<String?, Boolean> {
        return Pair(pendingMessageId, shouldOpenDialog).also {
            pendingMessageId = null
            shouldOpenDialog = false
        }
    }

    fun hasPendingNavigation(): Boolean {
        return pendingMessageId != null
    }
}