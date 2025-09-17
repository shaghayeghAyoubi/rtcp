package com.example.myapplication.domain.model

enum class NotificationFilter(val key: String, val label: String) {
    ALL("all", "All messages"),
    ONLY_OK("only_ok", "Only OK"),
    ONLY_FORBIDDEN("only_forbidden", "Only forbidden")
}