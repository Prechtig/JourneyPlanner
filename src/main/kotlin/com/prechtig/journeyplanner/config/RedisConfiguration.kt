package com.prechtig.journeyplanner.config

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import java.time.Duration

@Configuration
class RedisConfiguration {

	@Bean
	fun redisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer? {
		return RedisCacheManagerBuilderCustomizer { builder: RedisCacheManagerBuilder ->
			builder.withCacheConfiguration(
				"userCache",
				RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10))
			)
		}
	}

	@Bean
	fun cacheConfiguration(): RedisCacheConfiguration? {
		return RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofMinutes(60))
			.disableCachingNullValues()
			.serializeValuesWith(SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer()))
	}
}