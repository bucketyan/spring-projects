package com.fuck.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* DESCRIPTION:
*
* @author zouyan
* @create 2018/10/17-上午11:10
* created by fuck~
**/
@Configuration
public class FeignApiConfig {
    @Value("${feign.client-id}")
    private String clientId;
    @Value("${feign.client-secret}")
    private String secret;
    @Value("${feign.header}")
    private String authHeader;

    @Bean
    public FeignInterceptor authenticationInterceptor() {
        return new FeignInterceptor(clientId, secret, authHeader);
    }

}
