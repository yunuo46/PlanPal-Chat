package com.gdg.infra

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class ApiClient(
    private val webClient: WebClient
) {

    @Value("\${backend.url}") // 예: http://backend.internal:8080
    private lateinit var backendUrl: String

    fun sendAiRequest(roomId: String, text: String): Mono<Void> {
        val endpoint = "/api/rooms/$roomId/ai-message"

        val payload = mapOf("text" to text)

        return webClient.post()
            .uri("$backendUrl$endpoint")
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(Void::class.java)
    }
}