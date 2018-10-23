package com.fuck.framework.gateway.remote;

import com.fuck.framework.gateway.remote.hystrix.PermissionRemoteHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
* DESCRIPTION:
* 权限获取
* @author zouyan
* @create 2018/9/29-上午11:32
* created by fuck~
**/
@FeignClient(name = "login-server", fallback = PermissionRemoteHystrix.class)
public interface PermissionRemote {

    /**
     * 获取所有权限
     * 如: select * from sys_permission
     * @return
     */
    @RequestMapping("/getAllPermission")
    public List<Map<String, Object>> getAllPermission();

    /**
     * 通过userId获取对应的所有权限
     * 如:
     * select p.* from sys_user u
     LEFT JOIN sys_role_user sru on u.id= sru.user_id
     LEFT JOIN sys_role r on sru.role_id=r.id
     LEFT JOIN sys_permission_role spr on spr.role_id=r.id
     LEFT JOIN sys_permission p on p.id =spr.permission_id
     where u.id=#{userId}
     * @return
     */
    @RequestMapping("/getAllPermissionByUserId")
    public List<Map<String, Object>> getAllPermissionByUserId(@RequestParam("userId") String userId);
}
