package com.christmas.letter.processor.util.cache;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

public class ConfigurableRedisCache extends RedisCache {
	protected ConfigurableRedisCache(
			String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
		super(name, cacheWriter, cacheConfig);
	}
}
