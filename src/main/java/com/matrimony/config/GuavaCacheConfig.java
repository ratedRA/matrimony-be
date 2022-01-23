package com.matrimony.config;

import com.matrimony.common.GuavaCache;
import com.matrimony.identity.data.UserOtpDetail;
import com.matrimony.identity.model.MatrimonyUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GuavaCacheConfig {

    @Bean(name = "userCache")
    public GuavaCache<String, MatrimonyUser> userOtpCache(){
        GuavaCache<String, MatrimonyUser> userOtpCache = new GuavaCache<>(15);
        return userOtpCache;
    }
}
