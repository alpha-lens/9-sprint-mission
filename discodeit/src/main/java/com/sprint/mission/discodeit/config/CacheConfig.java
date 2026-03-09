package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("userChannelList", "channelCache");
    cacheManager.setCaffeine(Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES) // 저장 후 1분 뒤 만료
        .maximumSize(1000)); // 최대 1000개까지 저장
    return cacheManager;
  }
}