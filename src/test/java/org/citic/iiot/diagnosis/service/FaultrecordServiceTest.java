package org.citic.iiot.diagnosis.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.citic.iiot.app.core.data.Result;
import org.citic.iiot.app.core.data.ResultCode;
import org.citic.iiot.app.core.session.SessionInfo;
import org.citic.iiot.app.core.session.User;
import org.citic.iiot.app.core.util.CodeUtil;
import org.citic.iiot.diagnosis.Application;
import org.citic.iiot.diagnosis.domain.Faultrecord;
import org.citic.iiot.diagnosis.service.FaultrecordService;
import org.citic.iiot.diagnosis.vo.FaultrecordInVO;
import org.citic.iiot.diagnosis.vo.KnowledgeRelateFaultInVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by git on 2017/8/16.
 */
@SpringBootTest(classes=Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class FaultrecordServiceTest implements SessionInfo{
    @Autowired
    private FaultrecordService faultrecordService;

    @Test
    public void list() {
        FaultrecordInVO faultrecordInVO = new FaultrecordInVO();
        faultrecordInVO.setLimitLine(10);
        faultrecordInVO.setStartLine(0);
        //faultrecordInVO.setType("2");
        //faultrecordInVO.setName("高");
        faultrecordInVO.setName("");
        Result<Map<String, Object>> faultrecordList = faultrecordService.getfaultrecordList(faultrecordInVO);
        Assert.assertTrue(faultrecordList.getContent().size()>0);
    }

    @Test  //根据故障/预警id和status查询故障/预警记
    public void getFaultrecordObject() {
        User user=new User();
        Result<Map<String,Object>> resBean = new Result<Map<String,Object>>(ResultCode.SUCCESS,"查询成功",null);
        Faultrecord faultrecord=new Faultrecord();
        faultrecord.setId("32284b547b904a54b7db16b1cf12f801");
        faultrecord.setStatus("1");
        if(faultrecord.getStatus().equals("0")) {
            resBean= faultrecordService.getFaultrecordObjectUnsolved(faultrecord,user);
        }else{
            resBean= faultrecordService.getFaultrecordObjectSolved(faultrecord);
        }
        Assert.assertTrue(resBean.getContent().size()>0);
    }


    @Test   //更改状态及加入知识库
    public void updateStatusAndAddKnowledge() {
        User user=new User();
        KnowledgeRelateFaultInVO knowledgeRelateFaultInVO=new KnowledgeRelateFaultInVO();
        knowledgeRelateFaultInVO.setId("6632ff777d8a4694b7fa4c0686f9cf33");
        knowledgeRelateFaultInVO.setAnalyse("分析");
        knowledgeRelateFaultInVO.setSolution("解决方案");
        knowledgeRelateFaultInVO.setCode("LMGZ001");
        knowledgeRelateFaultInVO.setType("2");
//        knowledgeRelateFaultInVO.setDeviceid("5c67e4c4eafa4f1f987ebbeb16b92610");
        Boolean result=false;
        try {
            if(StringUtils.isEmpty(knowledgeRelateFaultInVO.getDeviceid())){
                faultrecordService.updateStatus(knowledgeRelateFaultInVO);
            }else{
                faultrecordService.updateStatusAndAddKnowledge(knowledgeRelateFaultInVO,user);
            }
            result=true;
        }catch (Exception e){
            log.error("Find Exception", e);
        }
        Assert.assertTrue(result);
    }

    @Test  //获取当前未处理预警/故障的数量
    public void getUnsolvedFaultrecordCount() {
        Map<String,Integer> res = new HashMap<String,Integer>();
        Faultrecord faultrecord=new Faultrecord();
        faultrecord.setStatus("0");
        res=faultrecordService.getUnsolvedFaultrecordCount(faultrecord);
        Assert.assertTrue(res.size()>0);
        log.info("返回数据：{}", res);
    }

    @Test  //获取近七日未处理预警/故障的数量
    public void UnsolvedFaultrecordCountLast7() {
        Map<String,Object> res = new HashMap<String,Object>();
        Faultrecord faultrecord=new Faultrecord();
        faultrecord.setStatus("0");
        res= faultrecordService.getUnsolvedFaultrecordCountLast7(faultrecord);
        Assert.assertTrue(res.size()>0);
        log.info("返回数据：{}", res);
    }

}
