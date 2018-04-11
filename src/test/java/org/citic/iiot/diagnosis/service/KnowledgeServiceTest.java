package org.citic.iiot.diagnosis.service;

import lombok.extern.slf4j.Slf4j;
import org.citic.iiot.app.core.util.UUIDUtil;
import org.citic.iiot.diagnosis.Application;
import org.citic.iiot.diagnosis.domain.Knowledge;
import org.citic.iiot.diagnosis.vo.KnowledgeInVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * User: Mr.zheng
 * Date: 2017/9/20
 * Time: 18:54
 */
@SpringBootTest(classes=Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class KnowledgeServiceTest {

    @Autowired
    private KnowledgeService knowledgeService;

    @Test
    public void saveKnowledge() throws Exception {
        //新增知识
        Knowledge knowledge = new Knowledge();
        knowledge.setCode("CSYJ0001");
        knowledge.setType("1");
        knowledge.setKnowname(UUIDUtil.creatUUID());
        knowledge.setMsgJson("[{\"analyse\": \"这是一个修改的故障3\", \"solution\": \"想办法解决他3\"}]");
        knowledgeService.saveKnowledge(knowledge);
        Assert.assertNotNull(knowledge.getKnowid());
        log.info("saveKnowledge success");

        //查询
        List<Knowledge> list = knowledgeService.knowledgesByKnowId(knowledge.getKnowid());
        Assert.assertEquals(list.size(),1);
        log.info("knowledgesByKnowId success");

        //查询详细
        Knowledge knowledgeDetail = new Knowledge();
        knowledgeDetail.setKnowid(knowledge.getKnowid());
        knowledgeDetail.setParentid("father");
        Assert.assertNotNull(knowledgeService.detail(knowledgeDetail));
        log.info("detail success");

        KnowledgeInVO knowledgeInVO = new KnowledgeInVO();
        knowledgeInVO.setStartLine(0);
        knowledgeInVO.setLimitLine(10);
        knowledgeInVO.setOrderString("createtime");
        knowledgeInVO.setSequence("DESC");
        knowledgeInVO.setKnowname(knowledge.getKnowname());
        knowledgeInVO.setCode(knowledge.getCode());
        List<Knowledge> lists = knowledgeService.getKnowledgeList(knowledgeInVO);
        Assert.assertEquals(lists.size(),1);
        log.info("getKnowledgeList success");

        int count = knowledgeService.selectCount(knowledgeInVO);
        Assert.assertEquals(count,1);
        log.info("selectCount success");

        knowledge.setStartLine(2);
        knowledgeService.updateKnowledge(knowledge);
        Assert.assertEquals(knowledge.getStartLine(),2);
        log.info("updateKnowledge success");

        String code = "'"+knowledge.getCode()+"'";
        Assert.assertTrue(knowledgeService.dels(code));
        log.info("dels success");
    }
}