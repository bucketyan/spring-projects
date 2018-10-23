package com.fuck.service.service.impl;

import com.fuck.service.remote.TestRemote;
import com.fuck.service.remote.hystrix.TestRemoteHystrix;
import com.fuck.service.service.TestService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 *
 * @author zouyan
 * @create 2017-05-31 15:40
 * Created by fuck~ on 2017/5/31.
 */
@Service
public class TestServiceImpl implements TestService {

    private static Logger logger = LogManager.getLogger(TestServiceImpl.class);

    @Autowired
    private TestRemote testRemote;

    @Override
    public List<Map<String, Object>> getInFormation() {
        List<Map<String, Object>> informationList = testRemote.getInFormation();
        if (CollectionUtils.isNotEmpty(informationList)) {
            TestRemoteHystrix.informationList = informationList;
        }
        logger.debug("testService getInFormation informationList:{}", informationList);
        return informationList;
    }
}
