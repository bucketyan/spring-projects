package com.fuck.framework.auth.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DESCRIPTION:
 * 自定义logout 移除access token、refresh token
 * @author zouyan
 * @create 2018-10-09 下午7:27
 * created by fuck~
 **/
public class CustomLogoutHandler implements LogoutHandler {

    private static Logger logger = LogManager.getLogger(CustomLogoutHandler.class);

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Assert.notNull(tokenStore, "tokenStore must be set");
        String token = request.getHeader("Authorization");
        Assert.hasText(token, "token must be set");
        if (isJwtBearerToken(token)) {
            token = token.substring(7);
            OAuth2AccessToken existingAccessToken = tokenStore.readAccessToken(token);
            OAuth2RefreshToken refreshToken;
            if (existingAccessToken != null) {
                if (existingAccessToken.getRefreshToken() != null) {
                    logger.info("remove refreshToken!", existingAccessToken.getRefreshToken());
                    refreshToken = existingAccessToken.getRefreshToken();
                    //移除refresh token
                    tokenStore.removeRefreshToken(refreshToken);
                }
                logger.info("remove existingAccessToken!", existingAccessToken);
                //移除access token
                tokenStore.removeAccessToken(existingAccessToken);
            }
            return;
        } else {
            throw new BadClientCredentialsException();
        }
    }

    private boolean isJwtBearerToken(String token) {
        return StringUtils.countMatches(token, ".") == 2 && (token.startsWith("Bearer") || token.startsWith("bearer"));
    }
}
