package cz.cvut.fel.nss.order.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Config hazelcastConfig() {
        Config config = new Config();
        config.setInstanceName("hazelcast-instance");
        
        MapConfig userCache = new MapConfig();
        userCache.setName("users");
        userCache.setTimeToLiveSeconds(3600);
        config.addMapConfig(userCache);

        MapConfig basketCache = new MapConfig();
        basketCache.setName("baskets");
        basketCache.setTimeToLiveSeconds(86400); // 24 hours
        config.addMapConfig(basketCache);

        MapConfig productCache = new MapConfig();
        productCache.setName("products");
        productCache.setTimeToLiveSeconds(0); // Persistent in memory until restart
        config.addMapConfig(productCache);
        
        return config;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(Config hazelcastConfig) {
        return Hazelcast.getOrCreateHazelcastInstance(hazelcastConfig);
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }
}
