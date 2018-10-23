package com.fuck.service.provider.service.impl;

import com.fuck.service.provider.dao.TestDao;
import com.fuck.service.provider.service.TestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TestServiceImpl implements TestService {

    private static Logger logger = LogManager.getLogger(TestServiceImpl.class);

    @Resource
    private TestDao testDao;

    @Override
    public List<Map<String, Object>> getInFormation() {
        List<Map<String, Object>> informationList = testDao.getInFormation();
        logger.debug("informationList size:{}", informationList.size());
        return informationList;
    }
}
