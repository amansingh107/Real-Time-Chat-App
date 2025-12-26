package com.chat.system.config;

import com.chat.system.model.ChatMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer; // Ensure the '2' is here
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, ChatMessage> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        // 1. Serialize Keys as simple Strings
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // 2. Serialize Values (ChatMessage) as JSON using Jackson
        // FIX: Ensure you use 'Jackson2JsonRedisSerializer'
        Jackson2JsonRedisSerializer<ChatMessage> valueSerializer =
                new Jackson2JsonRedisSerializer<>(ChatMessage.class);

        // 3. Build the Serialization Context
        RedisSerializationContext.RedisSerializationContextBuilder<String, ChatMessage> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);

        RedisSerializationContext<String, ChatMessage> context =
                builder.value(valueSerializer).build();

        // 4. Create the Template
        return new ReactiveRedisTemplate<>(factory, context);
    }
}