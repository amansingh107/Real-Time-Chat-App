package com.chat.system.service;

import com.chat.system.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.redis.listener.ChannelTopic;

@Service
@RequiredArgsConstructor
public class ChatService {

    private ReactiveRedisTemplate<String, ChatMessage> redisTemplate;
    private static final String CHAT_TOPIC = "chat-updates";

    // 1. Publish message to Redis Topic
    public Mono<Long> publishMessage(ChatMessage message) {
        return redisTemplate.convertAndSend(CHAT_TOPIC, message);
    }

    // 2. Subscribe to Redis Topic and convert to a Flux stream
    // Using .share() or .publish().autoConnect() is important for multicasting
    public Flux<ChatMessage> subscribeToMessages() {
        return redisTemplate.listenTo(ChannelTopic.of(CHAT_TOPIC))
                .map(message -> message.getMessage());
    }

    // Helper to get channel topic (needed for listenTo method)
    // In newer Spring Data Redis versions, listenToChannel is direct.
    // If you get compilation errors, use:
    // return redisTemplate.listenTo(ChannelTopic.of(CHAT_TOPIC)).map(ReactiveSubscription.Message::getMessage);
}