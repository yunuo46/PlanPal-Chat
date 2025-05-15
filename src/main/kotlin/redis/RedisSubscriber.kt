package com.gdg.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.gdg.domain.ChatMessage
import com.gdg.session.SessionRegistry
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Component

@Component
class RedisSubscriber(
    private val sessionRegistry: SessionRegistry,
    private val objectMapper: ObjectMapper,
    private val container: RedisMessageListenerContainer
) {

    @PostConstruct
    fun subscribe() {
        val listener = MessageListener { message, _ ->
            println(">>> Redis SUBSCRIBE: received message=${String(message.body)}")
            CoroutineScope(Dispatchers.IO).launch {
                val body = String(message.body)
                val chat = objectMapper.readValue(body, ChatMessage::class.java)
                sessionRegistry.broadcast(
                    chat.roomId,
                    """{"type":"chat","text":"[${chat.senderName}] ${chat.content}"}""",
                    excludeSessionId = chat.senderSessionId
                )
            }
        }
        container.addMessageListener(listener, ChannelTopic("chat"))
    }
}
