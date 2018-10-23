package com.fuck.framework.gateway.service;

import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * 权限service
 * @author zouyan
 * @create 2018-10-09 下午2:37
 * created by fuck~
 **/
public interface PermissionService {

    /**
     * 根据url获取对应访问权限
     * 缓存时间: 一小时
     * url对应访问权限可以有多个 如: /api/x1 可以对应有role1、role2
     * 也可以在role、permission表中做关联 不同role对应相同permission
     *
     * permissionMap结构（可扩展 如方法限定 GET、POST等）：
     permissionMap.put("id", "1");
     permissionMap.put("name", "ROLE_TEST");
     permissionMap.put("description", "demo服务1-1");
     permissionMap.put("url", "/demo/test");
     * @param url
     * @return
     */
    public List<Map<String, Object>> getURLRole(String url);

    /**
     * 通过userId动态获取权限信息
     * 缓存时间: 一小时
     * 返回数据示例:
        {"ROLE_TEST", "ROLE_TEST2"}
     * @param userId
     * @return
     */
    public List<String> getAuthoritiesByUserId(String userId);
}
