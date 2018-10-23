package com.fuck.service.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DESCRIPTION:
 * 创建自定义请求拦截器，在发送请求前增加了一个请求头信息，进行身份校验
 * @author zouyan
 * @create 2018-10-17 上午11:17
 * created by fuck~
 **/
public class FeignInterceptor implements RequestInterceptor {

    private String clientId;
    private String secret;
    private String authHeader;

    public FeignInterceptor(String clientId, String secret, String header) {
        this.clientId = clientId;
        this.secret = secret;
        this.authHeader = header;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(authHeader,clientId + ":" + secret);
    }
}
