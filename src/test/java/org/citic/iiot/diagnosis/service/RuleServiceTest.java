package org.citic.iiot.diagnosis.service;

import lombok.extern.slf4j.Slf4j;
import org.citic.iiot.app.core.session.User;
import org.citic.iiot.app.core.util.UUIDUtil;
import org.citic.iiot.diagnosis.Application;
import org.citic.iiot.diagnosis.dao.MonitorPointDao;
import org.citic.iiot.diagnosis.domain.MonitorPoint;
import org.citic.iiot.diagnosis.domain.Rule;
import org.citic.iiot.diagnosis.util.FileUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * User: Mr.zheng
 * Date: 2017/9/20
 * Time: 18:54
 */
@SpringBootTest(classes=Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class RuleServiceTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private MonitorPointDao monitorPointDao;

    @Test
    public void testAll() throws Exception {
        //新建一个监控点
        MonitorPoint monitorPoint = new MonitorPoint();
        monitorPoint.setPointid(UUIDUtil.creatUUID());
        monitorPoint.setDeviceid(UUIDUtil.creatUUID());
        monitorPoint.setName("CSName0001");
        monitorPoint.setForeginid(UUIDUtil.creatUUID());
        monitorPoint.setConfigid(UUIDUtil.creatUUID());
        int i = monitorPointDao.save(monitorPoint);
        Assert.assertEquals(1,i);
        //新建一个规则
        Rule rule = new Rule();
        rule.setCode("CSYJ0001");
        rule.setRulename(UUIDUtil.creatUUID());
        rule.setType("1");
        rule.setRuleConfig("this['sensor']=='L0023'&&this['measure']>=40");
        rule.setDisp("这是一个测试的预警");
        rule.setMonitorPointid(monitorPoint.getPointid());
        rule.setSalience(5);
        String drlAppendBodyString = FileUtil.makeFileBody(rule);
        rule.setRulecontent(drlAppendBodyString);
        rule.setRuleid(UUIDUtil.creatUUID());
        rule.setCreatetime(new Date());
        rule.setUpdatetime(new Date());
        rule.setRunrate(1000*20);
        boolean isSaveOk = this.ruleService.createRule(rule);
        Assert.assertTrue(isSaveOk);

        //测试pagelist接口
        Rule listRule = new Rule();
        listRule.setRulename(rule.getRulename());
        listRule.setLimitLine(10);
        listRule.setOrderString("createtime");
        listRule.setStartLine(0);
        listRule.setSequence("DESC");
        List<Rule> rules = ruleService.ruleList(listRule);
        Assert.assertEquals(rules.size(),1);
        //测试pagecount 接口
        int count = ruleService.ruleCount(listRule);
        Assert.assertEquals(count,1);
        //测试knowleage rule 接口
        List<Rule> knowleageRules = ruleService.knowledgeRuleList(listRule);
        Assert.assertEquals(knowleageRules.size(),1);
        //测试knowleage count 接口
        int knowleageCount = ruleService.knowLedgeRuleCount(listRule);
        Assert.assertEquals(knowleageCount,1);
        //测试详细接口
        Rule ruleDetail = new Rule();
        ruleDetail.setRuleid(rule.getRuleid());
        Assert.assertNotNull(ruleService.ruleDetail(ruleDetail));
        //测试 通过point 获取规则s
        Assert.assertNotNull(ruleService.findRulesByPointid(monitorPoint.getPointid()));
        User user=new User();
        //测试修改
        rule.setDisp("sssss");
        Assert.assertTrue(ruleService.updateRule(rule,user));

        Thread.sleep(1000*5);
        //测试删除
        boolean isDelOk = this.ruleService.deleteRule(rule);
        Assert.assertTrue(isDelOk);
        int pointNum = monitorPointDao.delete(monitorPoint);
        Assert.assertEquals(pointNum,1);
    }
}