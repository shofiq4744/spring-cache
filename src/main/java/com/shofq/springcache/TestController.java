package com.shofq.springcache;

import com.shofq.springcache.config.single.RedisCacheManager;
import com.shofq.springcache.config.cluster.RedisClusterCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheManager.class);
    private static final String KEY = "cache-key";
    private static final String VALUE = "cache-value";
    private final RedisClusterCacheManager cacheManager;

    public TestController(RedisClusterCacheManager cacheManager){
        this.cacheManager = cacheManager;
    }

    @GetMapping("/set")
    public Mono storeCache(@RequestBody Map<String,String> request){
        Map<String,String> map = new HashMap<>();
        map.put(VALUE,request.get(VALUE));
       // cacheManager.setToLocalCache(KEY,map);
        return cacheManager.setToSharedCache(map, request.get(KEY))
                .flatMap(res-> Mono.just(res));
    }

    @GetMapping("/get/{key}")
    public Mono getCacheValue(@PathVariable String key){
        Map bothCache = new HashMap();
        //bothCache.put("local",cacheManager.getToLocalCache(KEY));
        return cacheManager.getFromSharedCache(key)
                .flatMap(res->{
                    bothCache.put("share",res);
                    return Mono.just(bothCache);
                });
    }
}
