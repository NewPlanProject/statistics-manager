package org.citic.iiot.diagnosis.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.citic.iiot.app.core.util.CodeUtil;
import org.citic.iiot.app.core.util.UUIDUtil;
import org.citic.iiot.diagnosis.dao.KnowledgeDao;
import org.citic.iiot.diagnosis.domain.Knowledge;
import org.citic.iiot.diagnosis.service.KnowledgeService;
import org.citic.iiot.diagnosis.service.RuleService;
import org.citic.iiot.diagnosis.vo.KnowledgeInVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yxw on 2017/7/19.
 */
@Slf4j
@Service
@Transactional
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    private KnowledgeDao knowledgeDao;

    @Autowired
    private RuleService ruleService;

    @Override
    public List<Knowledge> knowledgesByKnowId(String knowid) {
        log.debug("Enter knowledgesByKnowId knowid={}",knowid);
        Knowledge know = new Knowledge();
        know.setParentid(knowid);
        //根据knowid获取知识库分析数据集合
        List<Knowledge> knowledges=knowledgeDao.selectList(know,"selectListByFid");
        log.debug("Get knowledge list size={}", knowledges != null ? knowledges.size() : 0);
        return knowledges;
    }

    @Override
    public Knowledge detail(Knowledge knowledge) {
        log.debug("Enter detail knowledge={}",knowledge);
        Knowledge resultKnow = this.knowledgeDao.selectOne(knowledge);
        log.debug("Get knowledge resultKnow={}", resultKnow);
        return resultKnow;
    }

    @Override
    public void saveKnowledge(Knowledge knowledge) throws Exception{
        log.debug("Enter saveKnowledge knowledge={}",knowledge);
        List<Knowledge> knowledges = new ArrayList<Knowledge>();
        //保存父条目信息
        String knowid = UUIDUtil.creatUUID();
        knowledge.setKnowid(knowid);
        knowledge.setCreatetime(new Date());
        knowledge.setUpdatetime(new Date());
        knowledge.setParentid("father");
        knowledges.add(knowledge);
        List<Map> maps = (List<Map>) JSON.parse(knowledge.getMsgJson());
        //获取知识库分析数据集合
        if(maps != null && maps.size()>0) {
            for (Map map : maps) {
                Knowledge k = new Knowledge();
                //复制必要属性
                BeanUtils.copyProperties(knowledge, k);
                //设置必要属性
                k.setKnowid(UUIDUtil.creatUUID());
                k.setKnowname(null);
                k.setAnalyse(map.get("analyse") != null ? map.get("analyse").toString() : null);
                k.setSolution(map.get("solution") != null ? map.get("solution").toString() : null);
                k.setFaultcategoryid(map.get("faultcategoryid") != null ? map.get("faultcategoryid").toString() : null);
                k.setUrl(map.get("url") != null ? map.get("url").toString() : null);
                k.setDisindex(map.get("disindex") != null ? map.get("disindex").toString() : null);
                k.setParentid(knowid);
                knowledges.add(k);
            }
        }
        //跟新 规则库的知识id
        this.ruleService.updateKnowledgeidByCode(knowledge.getCode(),knowid);
        //保存知识库
        knowledgeDao.saveBatch(knowledges);
        log.info("Do saveKnowledge knowledges size={}",knowledges != null ? knowledges.size() : 0);
    }

    @Override
    public void updateKnowledge(Knowledge knowledge)throws Exception{
        log.info("Enter updateKnowledge knowledge={}",knowledge);
        List<Knowledge> insertKnows = new ArrayList<Knowledge>();
        List<Knowledge> updateKnows = new ArrayList<Knowledge>();
        knowledge.setUpdatetime(new Date());
        updateKnows.add(knowledge);
        //获取前台传入的  现象分析与 解决方案
        List<Map> maps = (List<Map>) JSON.parse(knowledge.getMsgJson());
        //获取知识库分析数据集合
        if(maps != null && maps.size()>0){
            for(Map map : maps){
                if(CodeUtil.isNotNull(map) && map.size()>0){
                    // map 转换为对象
                    Knowledge k = new Knowledge();
                    //复制必要属性
                    BeanUtils.copyProperties(knowledge,k);
                    k.setAnalyse(map.get("analyse") != null ? map.get("analyse").toString() : null);
                    k.setSolution(map.get("solution") != null ? map.get("solution").toString() : null);
                    k.setFaultcategoryid(map.get("faultcategoryid") != null ? map.get("faultcategoryid").toString() : null);
                    k.setUrl(map.get("url") != null ? map.get("url").toString() : null);
                    k.setKnowid(map.get("knowid") != null ? map.get("knowid").toString() : null);
                    k.setDisindex(map.get("disindex") != null ? map.get("disindex").toString() : null);
                    //knowname去重
                    k.setKnowname(null);
                    //若有knowid 认为是修改
                    if(CodeUtil.isNotNull(k) && CodeUtil.isNotNullEmpty(k.getKnowid())){
                        k.setUpdatetime(new Date());
                        updateKnows.add(k);
                    }else if(CodeUtil.isNotNull(k) && !CodeUtil.isNotNullEmpty(k.getKnowid())){//若无knowid 认为是新增
                        k.setKnowid(UUIDUtil.creatUUID());
                        k.setCreatetime(new Date());
                        k.setUpdatetime(new Date());
                        k.setParentid(knowledge.getKnowid());
                        insertKnows.add(k);
                    }
                }
            }
        }
        //批量修改
        if(CodeUtil.isNotNullZero(updateKnows)){
            knowledgeDao.updateBatch(updateKnows);
        }
        //批量新增
        if(CodeUtil.isNotNullZero(insertKnows)){
            knowledgeDao.saveBatch(insertKnows);
        }
        log.info("Leave updateKnowledge updateKnows list size={},insertKnows list size={}", updateKnows.size(),insertKnows.size());
    }


    @Override
    public List<Knowledge> getKnowledgeList(KnowledgeInVO knowledgeInVO) {
        log.info("Enter getKnowledgeList knowledgeInVO={}",knowledgeInVO);
        Knowledge knowledge= new Knowledge();
        BeanUtils.copyProperties(knowledgeInVO, knowledge);
        List<Knowledge> knowledgeList = knowledgeDao.selectList(knowledge, "pageKnowledge");
        log.info("Get getKnowledgeList knowledgeList size={}", knowledgeList != null ? knowledgeList.size():"");
        return knowledgeList;
    }

    @Override
    public int selectCount(KnowledgeInVO knowledgeInVO) {
        log.info("Enter selectCount knowledgeInVO={}",knowledgeInVO);
        Knowledge knowledge= new Knowledge();
        BeanUtils.copyProperties(knowledgeInVO, knowledge);
        int count = knowledgeDao.selectCount(knowledge, "pageKnowledgeCount");
        log.info("Leave selectCount count={}",count);
        return count;
    }

    @Override
    public Boolean dels(String codes) {
        log.info("Enter dels codes={}",codes);
        Knowledge knowledge = new Knowledge();
        knowledge.setCode(codes);
        int result = knowledgeDao.delete(knowledge,"deleteKnowledgesByCodes");
        this.ruleService.updateKnowledgeidByCodes(codes);
        boolean res = result != 0 ? true : false;
        log.info("Leave dels result={}",result);
        return res;
    }

    @Override
    public Boolean del(String knowid) {
        log.info("Enter del knowid={}",knowid);
        boolean res = false;
        Knowledge knowledge = new Knowledge();
        knowledge.setKnowid(knowid);
        int result = knowledgeDao.delete(knowledge);
        res = result == 1 ? true : false;
        log.info("Leave del result={}",result);
        return res;
    }
}
