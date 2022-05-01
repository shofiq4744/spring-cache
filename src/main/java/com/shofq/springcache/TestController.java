package com.shofq.springcache;

import com.shofq.springcache.config.RedisCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheManager.class);
    private final String KEY = "testKey";
    private final RedisCacheManager cacheManager;

    public TestController(RedisCacheManager cacheManager){
        this.cacheManager = cacheManager;
    }

    @GetMapping("/store")
    public Mono storeCache(){
        Map<String,String> map = new HashMap<>();
        map.put("id","1");
        map.put("district","dhaka");
        map.put("value","200");
        cacheManager.setToLocalCache(KEY,map);
        cacheManager.setToSharedCache(map,KEY);
        LOGGER.debug("cache store success cache key ",KEY);
        return Mono.just("success");
    }

    @GetMapping("/get")
    public Mono getCache(){
        Map bothCache = new HashMap();
        bothCache.put("local",cacheManager.getToLocalCache(KEY));
        return cacheManager.getFromSharedCache(KEY)
                .flatMap(res->{
                    bothCache.put("share",res);
                    return Mono.just(bothCache);
                });
    }
}
