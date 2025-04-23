package com.gpg.repository

import com.gpg.model.ChatMessage
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ChatMessageRepository : ReactiveMongoRepository<ChatMessage, String> {
    fun findByRoomId(roomId: String): Flux<ChatMessage>
}