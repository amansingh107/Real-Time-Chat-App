package com.chat.system.service;

import com.chat.system.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatService {

    private final ReactiveRedisTemplate<String, ChatMessage> redisTemplate;
    private static final String CHAT_TOPIC = "chat-updates";

    // MANUAL CONSTRUCTOR INJECTION
    // This forces Spring to provide the template or fail immediately at startup
    @Autowired
    public ChatService(ReactiveRedisTemplate<String, ChatMessage> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 1. Publish message to Redis Topic
    public Mono<Long> publishMessage(ChatMessage message) {
        return redisTemplate.convertAndSend(CHAT_TOPIC, message);
    }

    // 2. Subscribe to Redis Topic
    public Flux<ChatMessage> subscribeToMessages() {
        return redisTemplate.listenTo(ChannelTopic.of(CHAT_TOPIC))
                .map(message -> message.getMessage());
    }
}