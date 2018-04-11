package org.citic.iiot.diagnosis.dao;

import org.citic.iiot.app.core.mybaits.MapperDaoTemplate;
import org.citic.iiot.diagnosis.domain.Knowledge;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class KnowledgeDao extends MapperDaoTemplate<Knowledge> {

    @Autowired
    public KnowledgeDao(SqlSessionTemplate sqlSessionTemplate){
        super(sqlSessionTemplate);
    }

}
