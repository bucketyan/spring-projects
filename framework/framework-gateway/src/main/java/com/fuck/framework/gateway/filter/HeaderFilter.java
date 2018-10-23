package com.fuck.framework.gateway.filter;

import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.fuck.framework.gateway.properties.PermitAllConfigProperties;
import com.fuck.framework.gateway.service.PermissionService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * 网关filter
 * 对于自定义放行的url直接进行放行
 * 对于携带token的请求 验证url对应权限及token对应的用户权限 权限不通过返回403 (之后RemoteTokenServices会验证token有效性)
 * 对于非自定义放行url及未携带token的请求之后会返回401
 * @author zouyan
 * @create 2018-09-30 下午5:04
 * created by fuck~
 **/
public class HeaderFilter implements Filter{

    private static Logger logger = LogManager.getLogger(HeaderFilter.class);

    @Autowired
    private PermitAllConfigProperties permitAllConfigProperties;

    @Autowired
    private PermissionService permissionService;

    private static final String TOKEN_USER_ID = "userId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String authorization = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        String requestURI = ((HttpServletRequest) servletRequest).getRequestURI();
        logger.info(String.format("request URI : %s", requestURI));
        //对于自定义放行的url直接进行放行
        if(isPermitAllUrl(requestURI)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //非自定义放行url查看是否存在token 验证是否为jwt token 校验权限 权限不通过返回403
        if (StringUtils.isNotEmpty(authorization)) {
            if (isJwtBearerToken(authorization)) {
                //解析token
                authorization = StringUtils.substringBetween(authorization, ".");
                String decoded = new String(Base64.decodeBase64(authorization));
                /**
                 * {"user_name":"u1","scope":["all"],"exp":1539097540,
                 * "userId":"u1","authorities":["ROLE_TEST"],
                 * "jti":"8332f5ca-49bc-497c-8092-b25cfca5bd27",
                 * "client_id":"haha"}
                 */
                Map properties = new ObjectMapper().readValue(decoded, Map.class);
                //获取userId
                String userId = (String) properties.get(TOKEN_USER_ID);
               // RequestContext.getCurrentContext().addZuulRequestHeader(TOKEN_USER_ID, userId);
                //通过userId动态获取权限信息 缓存一小时
                List<String> authorities = permissionService.getAuthoritiesByUserId(userId);
               // List<String> authorities = (List<String>) properties.get("authorities");
                //权限校验
                Boolean checkPermission = false;
                //根据请求URL 获取URL对应权限
                /**
                 * permissionMap结构（可扩展 如方法限定 GET、POST等）：
                 permissionMap.put("id", "1");
                 permissionMap.put("name", "ROLE_TEST");
                 permissionMap.put("description", "demo服务1-1");
                 permissionMap.put("url", "/demo/test");
                 */
                List<Map<String, Object>> needRoleList = permissionService.getURLRole(requestURI);
                if (CollectionUtils.isNotEmpty(needRoleList)) {
                    checkOut : for (Map<String, Object> needRoleMap : needRoleList) {
                        String needRole = MapUtils.getString(needRoleMap, "name");
                        for (String authority : authorities) {
                            if (authority.equals(needRole)) {
                                checkPermission = true;
                                logger.info("userId:{}, requestURI:{} 权限校验通过", userId, requestURI);
                                break checkOut;
                            }
                        }
                    }
                }
                if (!checkPermission) {
                    logger.info("userId:{}, requestURI:{} 权限校验不通过", userId, requestURI);
                    //权限校验不通过
                    ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private boolean isPermitAllUrl(String url) {
        return permitAllConfigProperties.isPermitAllUrl(url);
    }

    private boolean isJwtBearerToken(String token) {
        return StringUtils.countMatches(token, ".") == 2 && (token.startsWith("Bearer") || token.startsWith("bearer"));
    }
}
