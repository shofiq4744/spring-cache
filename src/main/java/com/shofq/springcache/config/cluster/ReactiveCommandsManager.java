package com.shofq.springcache.config.cluster;

import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import org.springframework.stereotype.Component;

@Component
public class ReactiveCommandsManager {

    private RedisClusterClient redisClusterClient;
    private RedisAdvancedClusterReactiveCommands<String, String> redisReactiveCommands;


    public ReactiveCommandsManager(RedisClusterClient redisClusterClient) {
        this.redisClusterClient = redisClusterClient;
    }
    public RedisAdvancedClusterReactiveCommands<String,String> getClusterReactiveCommands(){
        if(null == redisReactiveCommands){
            synchronized (redisClusterClient){
                if(null == redisReactiveCommands){
                    redisReactiveCommands = redisClusterClient.connect().reactive();
                }
            }
        }

        return redisReactiveCommands;
    }
}
