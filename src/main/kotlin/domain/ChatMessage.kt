package com.gdg.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "chat_messages")
data class ChatMessage(
    @Id val id: String? = null,
    val roomId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)