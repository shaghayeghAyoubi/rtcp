package com.example.myapplication

import androidx.compose.runtime.mutableStateOf

// SharedNavigationManager.kt
// SharedNavigationManager.kt
object SharedNavigationManager {
    private var _pendingMessageId = mutableStateOf<String?>(null)
    val pendingMessageId: String? get() = _pendingMessageId.value

    fun setPendingMessageId(messageId: String) {
        _pendingMessageId.value = messageId
    }

    fun consumePendingMessageId(): String? {
        return _pendingMessageId.value.also { _pendingMessageId.value = null }
    }

    fun hasPendingNavigation(): Boolean {
        return _pendingMessageId.value != null
    }
}