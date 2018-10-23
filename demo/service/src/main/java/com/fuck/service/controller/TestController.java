package com.fuck.service.controller;

import com.fuck.service.service.TestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    private static final Logger logger = LogManager.getLogger(TestController.class);

    @Value("${test.case}")
    private String testCase = "";

    @Autowired
    private TestService testService;

    @RequestMapping("/test")
    public List<Map<String, Object>> test() {
        List<Map<String, Object>> informationList = testService.getInFormation();
        return informationList;
    }

    @RequestMapping("/getCase")
    public String getCase() {
        return testCase;
    }

}
