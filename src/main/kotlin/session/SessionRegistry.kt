package com.gdg.session

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Component
class SessionRegistry {
    private val logger = LoggerFactory.getLogger(SessionRegistry::class.java)
    private val sessions = ConcurrentHashMap<String, CopyOnWriteArrayList<WebSocketSession>>()

    fun add(roomId: String, session: WebSocketSession) {
        sessions.computeIfAbsent(roomId) { CopyOnWriteArrayList() }.add(session)
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
                logger.info("Closed session removed: roomId=$roomId, sessionId=${session.id}")
                iterator.remove()
                continue
            }
            if (excludeSessionId != null && session.id == excludeSessionId) continue
            try {
                println(">>> Redis SUBSCRIBE: received message=$message")
                session.sendMessage(TextMessage(message))
                logger.debug("Message sent to sessionId=${session.id} in room $roomId")
            } catch (e: Exception) {
                logger.error("Failed to send message to sessionId=${session.id} in room $roomId", e)
            }
        }
    }
}
