package com.gdg.service

import com.gdg.domain.ChatMessage
import com.gdg.repository.ChatMessageRepository
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