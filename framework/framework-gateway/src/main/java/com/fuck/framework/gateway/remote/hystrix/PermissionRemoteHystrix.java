package com.fuck.framework.gateway.remote.hystrix;

import com.fuck.framework.gateway.remote.PermissionRemote;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 *
 * @author zouyan
 * @create 2018-09-29 上午11:33
 * created by fuck~
 **/
@Component
public class PermissionRemoteHystrix implements PermissionRemote {

    private static Logger logger = LogManager.getLogger(PermissionRemoteHystrix.class);

    public static List<Map<String, Object>> permissionList;

    public static List<Map<String, Object>> userPermissionList;

    /**
     * 获取所有权限
     * 如: select * from sys_permission
     * @return
     */
    @RequestMapping("/getAllPermission")
    public List<Map<String, Object>> getAllPermission() {
        logger.warn("permissionRemote getAllPermission failed! start fallback");
        return permissionList;
    }

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
    public List<Map<String, Object>> getAllPermissionByUserId(@RequestParam("userId") String userId) {
        logger.warn("permissionRemote getAllPermission failed! start fallback");
        return userPermissionList;
    }
}
