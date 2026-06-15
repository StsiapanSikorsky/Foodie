package com.Foodie.booking_service.config;

import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.response.PaginationResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private RedisSerializer<Object> jsonSerializer() {
        return GenericJacksonJsonRedisSerializer.builder()
                // Метод автоматически включает сохранение информации о типах (аналог LaissezFaireSubTypeValidator)
                .enableUnsafeDefaultTyping()
                .build();
    }

    @Bean
    public RedisTemplate<String, BookingDto> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, BookingDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        RedisSerializer<Object> serializer = jsonSerializer();

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, PaginationResponse<BookingDto>> paginationRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, PaginationResponse<BookingDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        RedisSerializer<Object> serializer = jsonSerializer();

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}
