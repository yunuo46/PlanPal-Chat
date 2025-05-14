package com.gdg.session

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class SessionRegistry {
    private val sessions = ConcurrentHashMap<String, MutableList<WebSocketSession>>()

    fun add(roomId: String, session: WebSocketSession) {
        sessions.computeIfAbsent(roomId) { mutableListOf() }.add(session)
    }

    fun remove(roomId: String, session: WebSocketSession) {
        sessions[roomId]?.remove(session)
    }

    fun broadcast(roomId: String, message: String) {
        sessions[roomId]?.forEach {
            if (it.isOpen) {
                it.sendMessage(TextMessage(message))
            }
        }
    }
}
