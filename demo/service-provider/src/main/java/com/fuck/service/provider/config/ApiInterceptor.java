package com.fuck.service.provider.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* DESCRIPTION:
*
* @author zouyan
* @create 2018/10/17-下午2:34
* created by fuck~
**/
public class ApiInterceptor extends HandlerInterceptorAdapter {

    private String clientId;
    private String secret;
    private String authHeader;

    public ApiInterceptor(String clientId, String secret, String header) {
        this.clientId = clientId;
        this.secret = secret;
        this.authHeader = header;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String requestToken = httpRequest.getHeader(authHeader);
        String authToken = clientId + ":" + secret;
        if (StringUtils.isEmpty(requestToken) || !requestToken.equals(authToken)) {
            //status 401
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return false;
        }
        return super.preHandle(httpRequest, httpResponse, handler);
    }
}
