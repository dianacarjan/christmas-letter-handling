package com.christmas.letter.processor.util.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigurableRedisCacheResolver implements CacheResolver {
    private final CacheManager cacheManager;

    @Value("${spring.data.redis.port}")
    private int cacheTTL;

    @Override
    public  Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        return context.getOperation()
                .getCacheNames()
                .stream().map(cacheName -> {
                    Cache cache = cacheManager.getCache(cacheName);

                    return cache != null ? cacheWithCustomTTL(cacheName, cache) : cache;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private ConfigurableRedisCache cacheWithCustomTTL(String cacheName, Cache cache) {
        RedisCache redisCache = (RedisCache) cache;
        RedisCacheWriter cacheWriter = redisCache.getNativeCache();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(getCacheTTL(cacheName)))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return new ConfigurableRedisCache(cacheName, cacheWriter, cacheConfig);
    }

    private int getCacheTTL(String cacheName) {
        String[] nameParts = cacheName.split("-");

        return nameParts.length > 0 ? Integer.parseInt(cacheName.substring(0, nameParts[0].length() - 1)) : cacheTTL;
    }
}
