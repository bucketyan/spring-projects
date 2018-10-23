package com.fuck.service.provider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
* DESCRIPTION:
* 
* @author zouyan
* @create 2018/10/17-下午2:36
* created by fuck~ 
**/
@Configuration
public class ApiConfig extends WebMvcConfigurerAdapter {

    @Value("${feign.client-id}")
    private String clientId;
    @Value("${feign.client-secret}")
    private String secret;
    @Value("${feign.header}")
    private String authHeader;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiInterceptor(clientId, secret, authHeader)).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
