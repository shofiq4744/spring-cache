package com.shofq.springcache.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class CacheManagerTest {

    @InjectMocks
    RedisCacheManager cacheManager;

    @Mock
    ReactiveRedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setup(){

        MockitoAnnotations.initMocks(this);

    }

    @Test
    void setCacheManagerTest(){
        Object cacheData = "testCacheData";
        String cacheKey = "Key";
        cacheManager.setToLocalCache(cacheKey,cacheData);
        Object expectedObj = cacheManager.getToLocalCache(cacheKey);
        assertNotNull(expectedObj);
        assertEquals(cacheData.toString(), expectedObj.toString());
    }
}
