package com.fuck.service.controller;

import com.fuck.service.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private TestService testService;

    @RequestMapping("/test")
    public String test(@RequestParam("id") String id) {
        return id;
    }

    @RequestMapping("/test2")
    public List<Map<String, Object>> test() {
        List<Map<String, Object>> informationList = testService.getInFormation();
        return informationList;
    }
    /*public String test2(HttpServletRequest request) {
        return "ok";
    }*/

}
