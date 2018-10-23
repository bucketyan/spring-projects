package com.fuck.service.provider.dao.impl;

import com.fuck.service.provider.dao.TestDao;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class TestDaoImpl implements TestDao {

    @Resource
    private SqlSession sqlSession;

    @Override
    public List<Map<String, Object>> getInFormation() {
        return sqlSession.selectList("testMapper.getInFormation");
    }
}
