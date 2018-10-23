package com.fuck.framework.auth.security;

import com.fuck.framework.auth.remote.PermissionRemote;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * 自定义provider
 * auth-server不做业务用户名、密码认证
 * 直接透传request 用户名、密码至login服务验证
 * 远程调用业务login接口验证用户名密码
 * login业务验证成功需返回userId
 * 若userId isBlank 判定为为校验失败 需返回errorMsg告知失败原因
 * errorMsg 示例值:("Failed to decode basic authentication token"、"No client credentials presented")
 *
 * 源码中
 * provider authenticate 会赋值userDetails
 * provider比较用户传输密码及真实密码
 * Authentication.getCredentials()不等于UserDetails.getPassword()
 * Authentication.getCredentials()为用户request提交的密码凭证
 * UserDetails.getPassword()为用户正确的密码
 * Authentication.getAuthorities()最后由UserDetails.getAuthorities()赋值
 * 例：
 * DaoAuthenticationProvider --> UserDetails --> UserDetailsChecker --> Credentials、Password equals
 * org.springframework.security.authentication.dao.DaoAuthenticationProvider
 * @author zouyan
 * @create 2017-12-22 9:54
 * Created by fuck~ on 2017/12/22.
 */
@Component
public class RemoteAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private PermissionRemote permissionRemote;

    private RestOperations restTemplate = new RestTemplate();;

    public static final String LOGIN_SERVICE_ID = "login-server";

    public static final String LOGIN_URL = "/login";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //获取request中用户名、密码
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
        String password = (String) authentication.getCredentials();

        //注册中心获取login-server 验证request用户名、密码是否正确
        ServiceInstance serviceInstance = loadBalancerClient.choose(LOGIN_SERVICE_ID);
        if (serviceInstance == null) {
            throw new RuntimeException("login-server serviceInstance not found!");
        }
        String loginUrl = new StringBuffer()
                .append(serviceInstance.getUri()).append(LOGIN_URL)
                .toString();
        /**
         * login业务验证成功需返回userId
         * 若userId isBlank 判定为为校验失败 需返回errorMsg告知失败原因
         * errorMsg 示例值:("Failed to decode basic authentication token"、"No client credentials presented")
         */
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
        formData.add("username", username);
        formData.add("password", password);
        Map<String, Object> map = (Map<String, Object>)postForMap(loginUrl, formData, new HttpHeaders());
        /*Map<String, Object> map =new HashMap<String, Object>(){
            {
                put("userId", "test-fuck");
            }
        };*/
        String userId = (String) map.get("userId");
        if (StringUtils.isBlank(userId)) {
            String errorMsg = (String) map.get("errorMsg");
            throw new BadCredentialsException(errorMsg);
        }
        /**
         * 通过返回的userId查询对应的权限
         */
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        /**
         * permissionMap结构（可扩展 如方法限定 GET、POST等）：
         permissionMap.put("id", "1");
         permissionMap.put("name", "ROLE_TEST");
         permissionMap.put("description", "demo服务1-1");
         permissionMap.put("url", "/demo/test");
         */
        List<Map<String, Object>> permissionList = permissionRemote.getAllPermissionByUserId(userId);

        if (CollectionUtils.isNotEmpty(permissionList)) {
            for (Map<String, Object> permissionMap : permissionList) {
                if (MapUtils.getString(permissionMap, "name") != null) {
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(MapUtils.getString(permissionMap, "name"));
                    grantedAuthorities.add(grantedAuthority);
                }
            }
        }
        //生成UserDetails
        RemoteUserDetails remoteUserDetails = new RemoteUserDetails.UserBuilder(username, password, userId).authorities(grantedAuthorities).build();
        return new RemoteAuthenticationToken(remoteUserDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    private Object postForMap(String path, MultiValueMap<String, String> formData, HttpHeaders headers) {
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }
        @SuppressWarnings("rawtypes")
        Object object = restTemplate.exchange(path, HttpMethod.POST,
                new HttpEntity<MultiValueMap<String, String>>(formData, headers), Map.class).getBody();
        return object;
    }

}
