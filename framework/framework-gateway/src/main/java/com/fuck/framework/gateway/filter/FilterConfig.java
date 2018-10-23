package com.fuck.framework.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* DESCRIPTION:
*
* @author zouyan
* @create 2018/9/30-下午5:05
* created by fuck~
**/
@Configuration
@EnableAutoConfiguration
public class FilterConfig {

    @Autowired
    private HeaderFilter headerFilter;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(headerFilter);
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public HeaderFilter headerEnhanceFilter() {
        return new HeaderFilter();
    }

}
