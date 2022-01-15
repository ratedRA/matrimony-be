package com.matrimony.config;

import com.matrimony.common.GuavaCache;
import com.matrimony.identity.data.UserOtpDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GuavaCacheConfig {

    @Bean(name = "userOtpCache")
    public GuavaCache<String, UserOtpDetail> userOtpCache(){
        GuavaCache<String, UserOtpDetail> userOtpCache = new GuavaCache<>(15);
        return userOtpCache;
    }
}
