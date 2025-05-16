package com.gdg.dto.chat

data class ChatResponse(
    val type: String = "chat",
    val senderName: String,
    val text: String,
    val timestamp: Long
)