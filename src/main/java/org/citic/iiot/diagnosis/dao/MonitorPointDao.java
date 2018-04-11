package org.citic.iiot.diagnosis.dao;

import org.citic.iiot.app.core.mybaits.MapperDaoTemplate;
import org.citic.iiot.diagnosis.domain.MonitorPoint;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MonitorPointDao extends MapperDaoTemplate<MonitorPoint> {

    @Autowired
    public MonitorPointDao(SqlSessionTemplate sqlSessionTemplate){
        super(sqlSessionTemplate);
    }

}
