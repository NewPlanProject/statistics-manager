package org.citic.iiot.diagnosis.dao;

import org.citic.iiot.app.core.mybaits.MapperDaoTemplate;
import org.citic.iiot.diagnosis.domain.Maintain;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MaintainDao extends MapperDaoTemplate<Maintain> {

    @Autowired
    public MaintainDao(SqlSessionTemplate sqlSessionTemplate){
        super(sqlSessionTemplate);
    }
}
