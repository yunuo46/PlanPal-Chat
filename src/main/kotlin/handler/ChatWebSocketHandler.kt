package com.gdg.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gdg.domain.ChatMessage
import com.gdg.redis.RedisPublisher
import com.gdg.session.SessionRegistry
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatWebSocketHandler(
    private val redisPublisher: RedisPublisher,
    private val sessionRegistry: SessionRegistry,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val roomId = getRoomId(session)
        sessionRegistry.add(roomId, session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val roomId = getRoomId(session)
        sessionRegistry.remove(roomId, session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val chatMessage = ChatMessage(
            roomId = getRoomId(session),
            senderId = getSenderId(session),
            content = message.payload,
            senderSessionId = session.id
        )

        val json = objectMapper.writeValueAsString(chatMessage)
        redisPublisher.publish("chat", json)
    }

    private fun getRoomId(session: WebSocketSession): String =
        session.uri?.query?.split("&")?.find { it.startsWith("roomId=") }?.substringAfter("=") ?: "default"

    private fun getSenderId(session: WebSocketSession): String =
        session.uri?.query?.split("&")?.find { it.startsWith("userId=") }?.substringAfter("=") ?: "anonymous"
}