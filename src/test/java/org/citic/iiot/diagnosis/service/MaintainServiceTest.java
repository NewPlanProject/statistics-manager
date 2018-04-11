package org.citic.iiot.diagnosis.service;

import lombok.extern.slf4j.Slf4j;
import org.citic.iiot.app.core.data.Result;
import org.citic.iiot.diagnosis.Application;
import org.citic.iiot.diagnosis.domain.Maintain;
import org.citic.iiot.diagnosis.service.MaintainService;
import org.citic.iiot.diagnosis.vo.MaintainInVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Created by yxw on 2017/7/19.
 */
@SpringBootTest(classes=Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class MaintainServiceTest {

    @Autowired
    private MaintainService maintainService;

    @Test
    public  void save(){
        Maintain maintain = new Maintain();
        maintain.setTitle("标题test");
        maintain.setDevicename("设备名称");
        maintain.setDeviceid("2349324");
        maintain.setFaultid("6f167de377a647e888160db969a617f2");
        maintain.setDetail("详情test");
        maintain.setType("1");
        int save = maintainService.save(maintain);
        Assert.assertEquals(1,save);
        log.info("保存成功!");

    }


    @Test
    public void list() {
        MaintainInVO maintainInVO = new MaintainInVO();
        maintainInVO.setLimitLine(10);
        maintainInVO.setStartLine(0);
        maintainInVO.setTitle("标");
        Result<Map<String, Object>> maintainList = maintainService.getMaintainList(maintainInVO);
        Assert.assertTrue(maintainList.getContent().size()>0);
        log.info("返回数据：{}", maintainList);
    }


    @Test
    public void testDel() {
        String[] ids = new String[]{"b75051e653ab4504b797f531e6e50570","bad007f033234e33813efc389b96cf33"};
        maintainService.del(ids);
        log.info("删除成功!");
    }


    @Test
    public void getMaintainById() {
        Maintain maintain = maintainService.getMaintainById("5c346518816e4096a40492cb607caae5");
        log.info("查询成功,{}" , maintain);
    }

}
