package com.chat.system.handler;

import com.chat.system.model.ChatMessage;
import com.chat.system.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper; // For JSON parsing

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        // 1. Handle INCOMING messages (User -> Server -> Redis)
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(msg -> {
                    try {
                        ChatMessage chatMessage = objectMapper.readValue(msg, ChatMessage.class);
                        return chatService.publishMessage(chatMessage);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .then();

        // 2. Handle OUTGOING messages (Redis -> Server -> User)
        Flux<WebSocketMessage> output = chatService.subscribeToMessages()
                .map(chatMessage -> {
                    try {
                        return objectMapper.writeValueAsString(chatMessage);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(session::textMessage);

        // 3. Combine both streams
        return Mono.zip(input, session.send(output)).then();
    }
}