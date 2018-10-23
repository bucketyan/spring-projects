package com.fuck.service.remote.hystrix;

import com.fuck.service.remote.TestRemote;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TestRemoteHystrix implements TestRemote{

    private static Logger logger = LogManager.getLogger(TestRemoteHystrix.class);

    public static List<Map<String, Object>> informationList;


    @Override
    public List<Map<String, Object>> getInFormation() {
        logger.warn("testRemote getInFormation failed! start fallback");
        return informationList;
    }
}
