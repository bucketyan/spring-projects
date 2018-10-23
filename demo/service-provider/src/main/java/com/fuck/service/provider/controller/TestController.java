package com.fuck.service.provider.controller;

import com.fuck.service.provider.service.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * 短信审核 全局黑名单
 * @author zouyan
 * @create 2017-07-17 9:13
 * Created by fuck~ on 2017/7/17.
 */
@RestController
public class TestController {

    @Resource
    private TestService testService;

    @RequestMapping("/getInFormation")
    public List<Map<String, Object>> getInFormation() {
        return testService.getInFormation();
    }
}
