package com.chat.system.service;

import com.chat.system.entity.ChatMessageEntity;
import com.chat.system.model.ChatMessage;
import com.chat.system.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ReactiveRedisTemplate<String, ChatMessage> redisTemplate;
    private final ChatMessageRepository messageRepository;
    private static final String CHAT_TOPIC = "chat-updates";

    // 1. Publish message to Redis Topic and persist to database
    public Mono<Long> publishMessage(ChatMessage message) {
        log.info("Publishing message - Type: {}, Sender: {}, Room: {}", 
            message.getType(), message.getSender(), message.getRoomId());
            
        // Save to database asynchronously (only for CHAT messages, not TYPING)
        if (message.getType() == ChatMessage.Type.CHAT || 
            message.getType() == ChatMessage.Type.JOIN || 
            message.getType() == ChatMessage.Type.LEAVE) {
            
            Mono.fromCallable(() -> {
                ChatMessageEntity entity = new ChatMessageEntity();
                entity.setType(message.getType().name());
                entity.setContent(message.getContent());
                entity.setSender(message.getSender());
                entity.setRoomId(message.getRoomId());
                return messageRepository.save(entity);
            })
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(
                saved -> log.debug("Message saved to database: id={}", saved.getId()),
                error -> log.error("Error saving message to database", error)
            );
        }
        
        // Publish to Redis for real-time distribution
        return redisTemplate.convertAndSend(CHAT_TOPIC, message)
            .doOnSuccess(subscribers -> log.info("Message published to {} subscribers", subscribers))
            .doOnError(error -> log.error("Error publishing message to Redis", error));
    }

    // 2. Subscribe to Redis Topic
    public Flux<ChatMessage> subscribeToMessages() {
        log.info("Subscribing to Redis topic: {}", CHAT_TOPIC);
        return redisTemplate.listenTo(ChannelTopic.of(CHAT_TOPIC))
                .map(message -> {
                    ChatMessage msg = message.getMessage();
                    log.debug("Received from Redis - Type: {}, Sender: {}", msg.getType(), msg.getSender());
                    return msg;
                });
    }
    
    // 3. Get chat history from database
    public List<ChatMessage> getChatHistory(String roomId, int limit) {
        log.info("Fetching chat history for room: {}", roomId);
        List<ChatMessageEntity> entities = messageRepository
            .findTop50ByRoomIdOrderByCreatedAtDesc(roomId);
        
        log.info("Found {} messages in history", entities.size());
        
        // Convert entities to ChatMessage DTOs and reverse the order
        return entities.stream()
            .map(entity -> new ChatMessage(
                ChatMessage.Type.valueOf(entity.getType()),
                entity.getContent(),
                entity.getSender(),
                entity.getRoomId()
            ))
            .collect(Collectors.toList());
    }
}