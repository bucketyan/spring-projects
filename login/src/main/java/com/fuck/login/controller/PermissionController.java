package com.fuck.login.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * 权限controller
 * @author zouyan
 * @create 2018-09-28 下午5:39
 * created by fuck~
 **/
@RestController
public class PermissionController {

    private static final Logger logger = LogManager.getLogger(PermissionController.class);

    /**
     * 获取所有权限
     * 如: select * from sys_permission
     * @return
     */
    @RequestMapping("/getAllPermission")
    public List<Map<String, Object>> getAllPermission() {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> permission1 = new HashMap<String, Object>();
        permission1.put("id", "1");
        permission1.put("name", "ROLE_TEST");
        permission1.put("description", "demo服务1-1");
        permission1.put("url", "/service/demo/test");

        Map<String, Object> permission2 = new HashMap<String, Object>();
        permission2.put("id", "2");
        permission2.put("name", "ROLE_TEST2");
        permission2.put("description", "demo服务1-2");
        permission2.put("url", "/service/demo/test2");

        result.add(permission1);
        result.add(permission2);

        logger.info("getAllPermission: {}", result);

        return result;
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

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if ("1".equals(userId)) {
            Map<String, Object> permission1 = new HashMap<String, Object>();
            permission1.put("id", "1");
            permission1.put("name", "ROLE_TEST");
            permission1.put("description", "demo服务1-1");
            permission1.put("url", "/service/demo/test");

            Map<String, Object> permission2 = new HashMap<String, Object>();
            permission2.put("id", "2");
            permission2.put("name", "ROLE_TEST2");
            permission2.put("description", "demo服务1-2");
            permission2.put("url", "/service/demo/test2");

            result.add(permission1);
            result.add(permission2);
        } else {
            Map<String, Object> permission = new HashMap<String, Object>();
            permission.put("id", "1");
            permission.put("name", "ROLE_TEST");
            permission.put("description", "demo服务1-1");
            permission.put("url", "/service/demo/test");

            result.add(permission);
        }

        logger.info("getAllPermissionByUserId userId:{}, result::{}", userId, result);
        return result;
    }

}
