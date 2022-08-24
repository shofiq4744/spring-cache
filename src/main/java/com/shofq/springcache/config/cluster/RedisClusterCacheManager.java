package com.shofq.springcache.config.cluster;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.lettuce.core.SetArgs;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RedisClusterCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClusterCacheManager.class);
    private final ReactiveCommandsManager reactiveCommandsManager;
    private final int LOCAL_CACHE_EXPIRE = 12;

    private MeterRegistry meterRegistry;
    private Map<String, Cache<String, Object>> localCacheHandlerMap = new ConcurrentHashMap<>();


    public RedisClusterCacheManager(ReactiveCommandsManager reactiveCommandsManager) {
        this.reactiveCommandsManager = reactiveCommandsManager;
    }

    /* Get cache from Radis
     * Param cache Key
     */
    public Mono<Object> getFromSharedCache(String key) {
        AtomicLong startTime = new AtomicLong();
        return getReactiveCommands()
                .get(key)
                .flatMap(value -> Mono.just(value))
                .switchIfEmpty(Mono.defer(() -> Mono.empty()))
                .doOnSubscribe(subscription-> startTime.set(System.currentTimeMillis()))
                .doFinally(t-> startTime.set(System.currentTimeMillis()));
    }

    /* Set cache from Radis
     * Param cache Key and data
     */
    public Mono<Boolean> setToSharedCache(Object data, String cacheKey) {
        AtomicLong startTime = new AtomicLong();
        LOGGER.info("Saving data to shared cache for key '{}'", cacheKey);
        Duration sharedCacheTimeout = Duration.ofHours(12);
        SetArgs setArgs = new SetArgs();
        setArgs.px(sharedCacheTimeout.toMillis());
        return getReactiveCommands()
                .set(cacheKey, new String(getValueSerializer().serialize(data), StandardCharsets.UTF_8), setArgs)
                .flatMap(s -> "OK".equals(s) ? Mono.just(true) :
                        Mono.error(new Exception("")))
                .doOnSubscribe(subscription -> startTime.set(System.currentTimeMillis()))
                .doFinally(t-> LOGGER.error(""));
    }

    /* Set local cache to caffeine
     * Param cache Key
     */
    public boolean setToLocalCache(String key,Object data) {
        try {
            Cache<String, Object> caffeineCache = Caffeine.newBuilder()
                    .expireAfterWrite(LOCAL_CACHE_EXPIRE, TimeUnit.HOURS)
                    .maximumSize(100)
                    .recordStats()
                    .build();
            caffeineCache.put(key,data);
            localCacheHandlerMap.put(key, caffeineCache);
            return true;
        } catch (Exception ex) {
            LOGGER.error("Failed to store data to local cache", ex);
        }
        return false;
    }

    /* Get cache from caffeine
     * Param cache Key
     */
    public Object getToLocalCache(String key) {
        try {
            Cache<String, Object> item = localCacheHandlerMap.get(key);
            return item.getIfPresent(key);
        } catch (Exception ex) {
            LOGGER.error("Failed to store data to local cache", ex);
        }
        return false;
    }

    protected static Jackson2JsonRedisSerializer<Object> getValueSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Jackson2JsonRedisSerializer<Object> valueSerializerNotFailOnUnknownProperties =
                new Jackson2JsonRedisSerializer<>(Object.class);
        valueSerializerNotFailOnUnknownProperties.setObjectMapper(objectMapper);

        return valueSerializerNotFailOnUnknownProperties;
    }

    private RedisAdvancedClusterReactiveCommands getReactiveCommands(){
        return reactiveCommandsManager.getClusterReactiveCommands();
    }
}
