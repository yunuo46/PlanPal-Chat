package com.gdg.repository

import com.gdg.domain.ChatMessage
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ChatMessageRepository : ReactiveMongoRepository<ChatMessage, String> {
    fun findByRoomId(roomId: String): Flux<ChatMessage>
}