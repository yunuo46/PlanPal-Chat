package com.gdg.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gdg.domain.ChatMessage
import com.gdg.infra.ApiClient
import com.gdg.redis.RedisPublisher
import com.gdg.session.SessionRegistry
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatWebSocketHandler(
    private val redisPublisher: RedisPublisher,
    private val sessionRegistry: SessionRegistry,
    private val objectMapper: ObjectMapper,
    private val apiClient: ApiClient
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val roomId = getRoomId(session)
        sessionRegistry.add(roomId, session)

        val senderName = getSenderName(session)
        val joinMsg = """{"type":"chat","text":"$senderName 님이 입장하셨습니다."}"""
        sessionRegistry.broadcast(roomId, joinMsg)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val roomId = getRoomId(session)
        sessionRegistry.remove(roomId, session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val json = objectMapper.readTree(message.payload)
        val type = json["type"]?.asText()
        val text = json["text"]?.asText() ?: ""

        val roomId = getRoomId(session)
        val senderName = getSenderName(session)
        val sessionId = session.id

        when (type) {
            "chat" -> {
                val chatMessage = ChatMessage(
                    roomId = roomId,
                    senderName = senderName,
                    content = text,
                    senderSessionId = sessionId
                )
                val payload = objectMapper.writeValueAsString(chatMessage)
                redisPublisher.publish("chat", payload)
            }
            "ai" -> {
                // AI 백엔드에 전달하는 로직 작성
            }
            "refreshMap", "refreshSchedule" -> {
                val broadcastPayload = objectMapper.writeValueAsString(
                    mapOf("type" to type)
                )
                sessionRegistry.broadcast(roomId, broadcastPayload, excludeSessionId = sessionId)
            }
            else -> {
                session.sendMessage(TextMessage("""{"type":"error","message":"Unknown type"}"""))
            }
        }
    }

    private fun getRoomId(session: WebSocketSession): String =
        session.uri?.query?.split("&")?.find { it.startsWith("roomId=") }?.substringAfter("=") ?: "default"

    private fun getSenderName(session: WebSocketSession): String =
        session.uri?.query?.split("&")?.find { it.startsWith("userName=") }?.substringAfter("=") ?: "anonymous"
}