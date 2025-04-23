package com.gpg.service

import com.gpg.model.ChatMessage
import com.gpg.repository.ChatMessageRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val chatMessageRepository: ChatMessageRepository
) {
    suspend fun save(message: ChatMessage): ChatMessage? {
        return chatMessageRepository.save(message).awaitFirstOrNull()
    }
}