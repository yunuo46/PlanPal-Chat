package com.gdg.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisPublisher(
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun publish(topic: String, message: String) {
        println(">>> Redis PUBLISH: topic=$topic, message=$message")
        redisTemplate.convertAndSend(topic, message)
    }
}