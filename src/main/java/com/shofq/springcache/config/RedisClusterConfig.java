package com.shofq.springcache.config;

/*
@Configuration
public class RedisClusterConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClusterConfig.class);

    public static final String ADAPTIVE_REFRESH_TRIGGER_TIMEOUT = "500ms";
    public static final String RECONNECT_ATTEMPT = "2";
    private static final int DEFAULT_ADAPTIVE_REFRESH_TRIGGER_TIMEOUT = 500;
    private static final int DEFAULT_RECONNECT_ATTEMPT = 2;
    private static boolean IS_CLUSTER = false;

    private static final List<Duration> DEFAULT_DURATION_LIST = ReconnectDelay.getReconnectDelayList(
            ReconnectDelay.DEFAULT_DURATION_LIST_STR);

    @Bean
    public RedisClusterClient getRedisClusterClient(ReactiveRedisConnectionFactory factory){
        LettuceClientConfiguration clientConfiguration = ((LettuceConnectionFactory) factory).getClientConfiguration();
        Duration timeoutDuration = clientConfiguration.getCommandTimeout();

        List<RedisURI> initialUris = new ArrayList<>();
        if(IS_CLUSTER) {
            RedisClusterConfiguration configuration = ((LettuceConnectionFactory) factory).getClusterConfiguration();
            for (RedisNode node : configuration.getClusterNodes()) {
                initialUris.add(createRedisURIAndApplySettings(node.getHost(), node.getPort(), timeoutDuration));
            }
        }
        RedisClusterClient redisClusterClient = RedisClusterClient.create(getClientResources(), initialUris);
        redisClusterClient.setOptions(ClusterClientOptions.builder()
                .topologyRefreshOptions(getClusterTopologyRefreshOptions())
                .socketOptions(
                        SocketOptions.builder().connectTimeout(timeoutDuration).build())
                .timeoutOptions(TimeoutOptions.enabled(timeoutDuration))
                .build());

        return redisClusterClient;
    }

    private ClusterTopologyRefreshOptions getClusterTopologyRefreshOptions() {
        ClusterTopologyRefreshOptions.Builder builder = ClusterTopologyRefreshOptions.builder()
                .enableAllAdaptiveRefreshTriggers()
                .adaptiveRefreshTriggersTimeout(Duration.ofMillis(DEFAULT_ADAPTIVE_REFRESH_TRIGGER_TIMEOUT))
                .refreshTriggersReconnectAttempts(DEFAULT_RECONNECT_ATTEMPT);

        String adaptiveRefreshTriggersTimeout = ADAPTIVE_REFRESH_TRIGGER_TIMEOUT;
        if (StringUtils.isNotBlank(adaptiveRefreshTriggersTimeout)) {
            ApplicationConversionService applicationConversionService = new ApplicationConversionService();
            try {
                Duration duration = applicationConversionService.convert(adaptiveRefreshTriggersTimeout,
                        Duration.class);
                builder.adaptiveRefreshTriggersTimeout(duration);
            } catch (ConversionFailedException exception) {
                LOGGER.error(
                        "Invalid data for 'redis.adaptiveRefreshTriggersTimeout', starting with the default value: {}",
                        DEFAULT_ADAPTIVE_REFRESH_TRIGGER_TIMEOUT, exception);
            }
        }

        String refreshTriggersReconnectAttempts = RECONNECT_ATTEMPT;
        if (StringUtils.isNotBlank(refreshTriggersReconnectAttempts)) {
            try {
                builder.refreshTriggersReconnectAttempts(Integer.parseInt(refreshTriggersReconnectAttempts));
            } catch (NumberFormatException exception) {
                LOGGER.error(
                        "Invalid data for 'redis.refreshTriggersReconnectAttempts', starting with the default value: {}",
                        DEFAULT_RECONNECT_ATTEMPT, exception);
            }
        }

        return builder.build();
    }

    private ClientResources getClientResources() {
        ClientResources.Builder builder = ClientResources.builder();
        List<Duration> reconnectDelayList = DEFAULT_DURATION_LIST;
        if (CollectionUtils.isNotEmpty(reconnectDelayList)) {
            builder.reconnectDelay(new ReconnectDelay(reconnectDelayList));
        }

        return builder.build();
    }

    private RedisURI createRedisURIAndApplySettings(String host, int port, Duration duration) {
        RedisURI.Builder builder = RedisURI.Builder.redis(host, port);
        builder.withTimeout(duration);
        return builder.build();
    }

}*/
