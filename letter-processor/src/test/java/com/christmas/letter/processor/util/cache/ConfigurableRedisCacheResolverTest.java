package com.christmas.letter.processor.util.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ConfigurableRedisCacheResolverTest {
    private ConfigurableRedisCacheResolver cacheResolver;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private RedisCache redisCache;

    @Mock
    private RedisCacheWriter redisCacheWriter;

    @BeforeEach
    void setup(){
        cacheResolver = new ConfigurableRedisCacheResolver(cacheManager);
    }

    @Test
    void givenValidCacheNames_whenResolveCaches_thenReturnConfigurableCaches(){
        // Arrange
        String cacheName = "30m-xmas-letters";
        when(cacheManager.getCache(cacheName)).thenReturn(redisCache);
        when(redisCache.getNativeCache()).thenReturn(redisCacheWriter);

        CacheOperationInvocationContext<CacheOperation> context = mock(CacheOperationInvocationContext.class);
        var operation = mock(CacheOperation.class);
        when(context.getOperation()).thenReturn(operation);
        when(operation.getCacheNames()).thenReturn(Set.of(cacheName));

        // Act
        Collection<? extends Cache> result = cacheResolver.resolveCaches(context);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isOne();
        assertThat(result.iterator().next()).isExactlyInstanceOf(ConfigurableRedisCache.class);
    }

    @Test
    void givenNoCaches_whenResolveCaches_thenReturnEmptyList() {
        // Arrange
        CacheOperationInvocationContext<CacheOperation> context = mock(CacheOperationInvocationContext.class);
        var operation = mock(CacheOperation.class);
        when(context.getOperation()).thenReturn(operation);
        when(operation.getCacheNames()).thenReturn(Collections.emptySet());

        // Act
        Collection<? extends Cache> result = cacheResolver.resolveCaches(context);

        // Assert
        assertThat(result).isEmpty();
    }
}
