package com.chat.system.handler;

import com.chat.system.model.ChatMessage;
import com.chat.system.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    
    // Local map to track which Session ID belongs to which User ID
    // Note: In a real distributed scenario with auto-scaling, you'd use Redis for this too.
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();
        log.info("New WebSocket connection: {}", sessionId);

        // 1. Handle INCOMING messages
        Mono<Void> input = session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .flatMap(msg -> {
                try {
                    ChatMessage chatMessage = objectMapper.readValue(msg, ChatMessage.class);
                    log.info("Received message - Type: {}, Sender: {}, Room: {}", 
                        chatMessage.getType(), chatMessage.getSender(), chatMessage.getRoomId());
                    
                    // Capture the UserID from the JOIN message to map it to the session
                    if (chatMessage.getType() == ChatMessage.Type.JOIN) {
                        sessionUserMap.put(sessionId, chatMessage.getSender());
                        log.info("User {} joined with session {}", chatMessage.getSender(), sessionId);
                    }
                    
                    if (chatMessage.getType() == ChatMessage.Type.TYPING) {
                        log.info("Publishing TYPING event from: {}", chatMessage.getSender());
                    }
                    
                    return chatService.publishMessage(chatMessage);
                } catch (Exception e) {
                    log.error("Error processing message", e);
                    return Mono.error(e);
                }
            })
            .doFinally(signalType -> {
                // Handle Disconnection (LEAVE)
                // If the connection closes, we find who it was and broadcast LEAVE
                String userId = sessionUserMap.remove(sessionId);
                if (userId != null) {
                    log.info("User {} disconnected (session: {})", userId, sessionId);
                    ChatMessage leaveMessage = new ChatMessage(
                        ChatMessage.Type.LEAVE, "", userId, "general" // Assuming 'general' room for now
                    );
                    chatService.publishMessage(leaveMessage).subscribe();
                }
            })
            .then();

        // 2. Handle OUTGOING messages
        Flux<WebSocketMessage> output = chatService.subscribeToMessages()
            .map(chatMessage -> {
                try {
                    String json = objectMapper.writeValueAsString(chatMessage);
                    log.debug("Sending message to session {}: Type={}, Sender={}", 
                        sessionId, chatMessage.getType(), chatMessage.getSender());
                    return json;
                } catch (Exception e) {
                    log.error("Error serializing message", e);
                    throw new RuntimeException(e);
                }
            })
            .map(session::textMessage);

        return Mono.zip(input, session.send(output)).then();
    }
}