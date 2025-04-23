package com.gpg.handler

import com.gpg.model.ChatMessage
import com.gpg.service.ChatService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatWebSocketHandler(
    private val chatService: ChatService
) : TextWebSocketHandler() {

    private val sessions = mutableMapOf<String, MutableList<WebSocketSession>>()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val roomId = getRoomId(session)
        sessions.computeIfAbsent(roomId) { mutableListOf() }.add(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val roomId = getRoomId(session)
        val senderId = getSenderId(session)

        val chatMessage = ChatMessage(
            roomId = roomId,
            senderId = senderId,
            content = message.payload
        )

        // 비동기 저장
        scope.launch {
            chatService.save(chatMessage)
        }

        // 동기 브로드캐스트
        sessions[roomId]?.forEach {
            if (it.isOpen) {
                it.sendMessage(TextMessage("[${senderId}] ${message.payload}"))
            }
        }
    }

    private fun getRoomId(session: WebSocketSession): String =
        session.uri?.query?.split("&")?.find { it.startsWith("roomId=") }?.substringAfter("=") ?: "default"

    private fun getSenderId(session: WebSocketSession): String =
        session.uri?.query?.split("&")?.find { it.startsWith("userId=") }?.substringAfter("=") ?: "anonymous"
}
