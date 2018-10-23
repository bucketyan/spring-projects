package com.fuck.framework.gateway.service.impl;

import com.fuck.framework.gateway.remote.PermissionRemote;
import com.fuck.framework.gateway.remote.hystrix.PermissionRemoteHystrix;
import com.fuck.framework.gateway.service.PermissionService;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ModifiedExpiryPolicy;
import javax.cache.spi.CachingProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * 权限service
 * @author zouyan
 * @create 2018-10-09 下午2:42
 * created by fuck~
 **/
@Service
public class PermissionServiceImpl implements PermissionService {

    private static Logger logger = LogManager.getLogger(PermissionServiceImpl.class);

    @Autowired
    private PermissionRemote permissionRemote;

    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    //resolve a cache manager
    private CachingProvider cachingProvider = Caching.getCachingProvider();
    private CacheManager cacheManager = cachingProvider.getCacheManager();
    private MutableConfiguration<String, List<Map<String, Object>>> oneHourConfig =
            new MutableConfiguration<String, List<Map<String, Object>>>()
                    .setExpiryPolicyFactory(ModifiedExpiryPolicy.factoryOf(Duration.ONE_HOUR))
                    .setStatisticsEnabled(false);
    private MutableConfiguration<String, List<String>> oneHourListStringConfig =
            new MutableConfiguration<String, List<String>>()
                    .setExpiryPolicyFactory(ModifiedExpiryPolicy.factoryOf(Duration.ONE_HOUR))
                    .setStatisticsEnabled(false);

    //key: url value: url对应权限信息
    private static Cache<String, List<Map<String, Object>>> permissionListCache;
    //key: userId value: userId对应权限信息
    private static Cache<String, List<String>> authoritiesCache;

    @PostConstruct
    public void defaultInitial() {
        permissionListCache = cacheManager.createCache("permissionListCache", oneHourConfig);
        authoritiesCache = cacheManager.createCache("authoritiesCache", oneHourListStringConfig);
        Thread initThread = new Thread(){
            @Override
            public void run() {
                while (true) {
                    try {
                        logger.debug(applicationInfoManager.getInfo().getStatus());
                        if (!applicationInfoManager.getInfo().getStatus().equals(InstanceInfo.InstanceStatus.UP)) {
                            Thread.sleep(1000);
                        } else {
                            //预加载权限集合缓存
                            refreshPermissionListCache();
                            logger.info("预加载权限集合缓存完毕");
                            break;
                        }
                    } catch (Exception e) {
                        logger.error("permissionService initThread error", e);
                    }
                }
            }
        };
        initThread.start();
    }

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
    @Override
    public List<Map<String, Object>> getURLRole(String url) {
        List<Map<String, Object>> result = permissionListCache.get(url);
        if (CollectionUtils.isEmpty(result)) {
            //缓存中没有进行缓存更新
            refreshPermissionListCache();
            result = permissionListCache.get(url);
        }
        logger.debug("getURLRole url:{}, needRole:{}", url, result);
        return result;
    }

    private void refreshPermissionListCache() {
        List<Map<String, Object>> permissionList = permissionRemote.getAllPermission();
        if (CollectionUtils.isNotEmpty(permissionList)) {
            //fallback update
            PermissionRemoteHystrix.permissionList = permissionList;
            for (Map<String, Object> permissionMap : permissionList) {
                String permissionUrl = MapUtils.getString(permissionMap, "url");
                List<Map<String, Object>> roleList = permissionListCache.get(permissionUrl);
                if (CollectionUtils.isEmpty(roleList)) {
                    roleList = new ArrayList<Map<String, Object>>();
                }
                roleList.add(permissionMap);
                permissionListCache.put(permissionUrl, roleList);
            }
        }
    }

    /**
     * 通过userId动态获取权限信息
     * 缓存时间: 一小时
     * 返回数据示例:
     {"ROLE_TEST", "ROLE_TEST2"}
     * @param userId
     * @return
     */
    @Override
    public List<String> getAuthoritiesByUserId(String userId) {
        List<String> result = authoritiesCache.get(userId);
        if (CollectionUtils.isEmpty(result)) {
            //通过userId获取对应的所有权限
            List<Map<String, Object>> userPermissionList = permissionRemote.getAllPermissionByUserId(userId);
            if (CollectionUtils.isNotEmpty(userPermissionList)) {
                //fallback update
                PermissionRemoteHystrix.userPermissionList = userPermissionList;
                List<String> userAuthorities = new ArrayList<String>();
                for (Map<String, Object> userPermissionMap : userPermissionList) {
                    /**
                     * permissionMap结构（可扩展 如方法限定 GET、POST等）：
                     permissionMap.put("id", "1");
                     permissionMap.put("name", "ROLE_TEST");
                     permissionMap.put("description", "demo服务1-1");
                     permissionMap.put("url", "/demo/test");
                     */
                    userAuthorities.add(MapUtils.getString(userPermissionMap, "name"));
                }
                authoritiesCache.put(userId, userAuthorities);
                result = userAuthorities;
            }
        }
        logger.debug("getAuthoritiesByUserId userId:{}, userAuthorities:{}", userId, result);
        return result;
    }
}
