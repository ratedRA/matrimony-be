package com.matrimony.config;

import com.matrimony.common.GuavaCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GuavaCacheConfig {

    @Bean(name = "userOtpCache")
    public GuavaCache<Long, String> userOtpCache(){
        GuavaCache<Long, String> userOtpCache = new GuavaCache<>(15);
        return userOtpCache;
    }
}
