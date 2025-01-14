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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigurableRedisCacheResolver implements CacheResolver {
    private final CacheManager cacheManager;

    @Value("${spring.data.redis.port}")
    private int cacheTTL;

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        if (context != null && context.getOperation() != null && !context.getOperation().getCacheNames().isEmpty()) {
            Collection<ConfigurableRedisCache> caches = new HashSet<>();
            Set<String> cacheNames = new HashSet<>(context.getOperation().getCacheNames());

            cacheNames.forEach(cacheName -> {
                Cache cache = cacheManager.getCache(cacheName);
                ConfigurableRedisCache customCache = cacheWithCustomTTL(cacheName, cache);
                caches.add(customCache);
            });

            return caches;
        }

        return Collections.emptyList();
    }

    private ConfigurableRedisCache cacheWithCustomTTL(String cacheName, Cache cache) {
        var name = cacheName.split("-");
        int ttl = name.length > 0 ? Integer.parseInt(cacheName.substring(0, name[0].length() - 1)) : cacheTTL;

        RedisCache redisCache = (RedisCache) cache;
        RedisCacheWriter cacheWriter = redisCache.getNativeCache();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(ttl))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return new ConfigurableRedisCache(cacheName, cacheWriter, cacheConfig);
    }
}
