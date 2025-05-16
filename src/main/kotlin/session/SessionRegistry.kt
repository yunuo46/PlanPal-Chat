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

    fun broadcast(roomId: String, message: String, excludeSessionId: String? = null) {
        val sessionList = sessions[roomId] ?: return

        val iterator = sessionList.iterator()
        while (iterator.hasNext()) {
            val session = iterator.next()

            if (!session.isOpen) {
                println("INFO: Remove invalid session in room $roomId (sessionId=${session?.id})")
                iterator.remove()
                continue
            }
            if (excludeSessionId != null && session.id == excludeSessionId) continue
            session.sendMessage(TextMessage(message))
        }
    }
}
