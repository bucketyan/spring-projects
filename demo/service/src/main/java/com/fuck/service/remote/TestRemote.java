package com.fuck.service.remote;

import com.fuck.service.remote.hystrix.TestRemoteHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "service-provider", fallback = TestRemoteHystrix.class)
public interface TestRemote {

    @RequestMapping("/getInFormation")
    public List<Map<String, Object>> getInFormation();
}
