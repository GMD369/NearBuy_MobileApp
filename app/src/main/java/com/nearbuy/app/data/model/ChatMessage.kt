package com.nearbuy.app.data.model

data class ChatMessage(
    val id: String,
    val text: String,
    val timestamp: Long,
    val isSentByMe: Boolean
)
