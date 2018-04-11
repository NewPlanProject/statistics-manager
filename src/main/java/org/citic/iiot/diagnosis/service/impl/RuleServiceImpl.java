package org.citic.iiot.diagnosis.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.citic.iiot.app.core.session.User;
import org.citic.iiot.app.core.util.CodeUtil;
import org.citic.iiot.app.core.util.DateUtil;
import org.citic.iiot.diagnosis.dao.KnowledgeDao;
import org.citic.iiot.diagnosis.dao.MonitorPointDao;
import org.citic.iiot.diagnosis.dao.RuleDao;
import org.citic.iiot.diagnosis.domain.Knowledge;
import org.citic.iiot.diagnosis.domain.MonitorPoint;
import org.citic.iiot.diagnosis.domain.Rule;
import org.citic.iiot.diagnosis.feignclient.DiagnosisExecutor;
import org.citic.iiot.diagnosis.service.RuleService;
import org.citic.iiot.diagnosis.util.FileUtil;
import org.citic.iiot.diagnosis.util.OSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: Mr.zheng
 * Date: 2017/8/1
 * Time: 14:26
 */
@Slf4j
@Service
@Transactional
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private KnowledgeDao knowledgeDao;

    @Autowired
    private MonitorPointDao monitorPointDao;

    @Autowired
    private OSSUtil ossUtil;

    @Autowired
    private DiagnosisExecutor diagnosisExecutor;

    @Override
    public List<Rule> ruleList(Rule rule) {
        log.info("Enter ruleList rule={}", rule);
        List<Rule> rules = this.ruleDao.selectList(rule, "pageRule");
        log.info("Get Rule list size={}", rules != null ? rules.size() : "");
        return rules;
    }

    @Override
    public Integer ruleCount(Rule rule) {
        log.info("Enter ruleList rule={}", rule);
        Map<String, Object> resultMap = null;
        int totalSize = this.ruleDao.selectCount(rule, "pageRuleCount");
        log.info("Get count totalSize={}",totalSize);
        return totalSize;
    }

    @Override
    public List<Rule> knowledgeRuleList(Rule rule) {
        log.info("Enter knowledgeRuleList rule={}", rule);
        List<Rule> rules = this.ruleDao.selectList(rule, "pageKnowledgeRule");
        log.info("Get Rule list size={}", rules != null ? rules.size() : "");
        return rules;
    }

    @Override
    public Integer knowLedgeRuleCount(Rule rule) {
        log.info("Enter knowLedgeRuleCount rule={}", rule);
        Map<String, Object> resultMap = null;
        int totalSize = this.ruleDao.selectCount(rule, "pageKnowledgeRuleCount");
        log.info("Get count totalSize={}",totalSize);
        return totalSize;
    }

    @Override
    public Boolean createRule(Rule rule) {
        log.info("Enter createRule rule={}",rule);
        //保存规则

        SimpleDateFormat  sdf = new SimpleDateFormat(DateUtil.YYYYMMDDHHMMSS);

        int result = ruleDao.save(rule);
        String filename = rule.getMonitorPointid()+"_"+sdf.format(new Date())+".drl";
        //生成并保存文件
        if(result == 1){
            List<Rule> rules = findRulesByPointid(rule.getMonitorPointid());
//            List<Rule> rules = this.ruleDao.selectList(rule);
            String drlAppendHeadString = FileUtil.makeFileHead();
            StringBuffer sb = new StringBuffer(drlAppendHeadString);
            for(Rule r : rules){
                sb.append(r.getRulecontent());
            }
            // 生成尾部
//            String end = FileUtil.makeFileEnd(rule);
//            sb.append(end);
            //生成文件 并启动规则
            ossUtil.OSSUploadUtil(sb.toString(),rule.getMonitorPointid(),filename,rule);
        }
        log.info("Leave createRule result={}",result);
        return result == 1 ? true : false;
    }

    @Override
    public Rule ruleDetail(Rule rule) {
        log.info("Enter ruleDetail rule={}",rule);
        rule = this.ruleDao.selectOne(rule);
        log.info("Get Rule rule={}",rule);
        return rule;
    }

    @Override
    public Boolean updateRule(Rule rule,User user) {
        try {
            log.info("Enter updateRule rule={}",rule);

            SimpleDateFormat  sdf = new SimpleDateFormat(DateUtil.YYYYMMDDHHMMSS);
            //修改规则
            rule.setUpdatetime(new Date());
            int result = ruleDao.update(rule);
            if(CodeUtil.isNotNull(user)){
                rule.setUserid(user.getUserid());
                rule.setOrgid(user.getOrgid());
                rule.setOrgcode(user.getOrgcode());
            }
            //生成保存到oss服务的文件名称
            String filename = rule.getMonitorPointid()+"_"+sdf.format(new Date())+".drl";

            if(result == 1){
                List<Rule> rules = findRulesByPointid(rule.getMonitorPointid());
                String drlAppendHeadString = FileUtil.makeFileHead();
                StringBuffer sb = new StringBuffer(drlAppendHeadString);
                for(Rule r : rules){
                    sb.append(r.getRulecontent());
                }
                // 生成尾部
//                String end = FileUtil.makeFileEnd(rule);
//                sb.append(end);
                //生成文件并启动规则
                ossUtil.OSSUploadUtil(sb.toString(),rule.getMonitorPointid(),filename,rule);
            }
            //页面输出信息
            log.info("Leave updateRule result={}",result);
            return result == 1 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean deleteRule(Rule rule) {
        log.info("Enter deleteRule rule={}",rule);
        SimpleDateFormat  sdf = new SimpleDateFormat(DateUtil.YYYYMMDDHHMMSS);
        Rule ruleParam = new Rule();
        ruleParam.setRuleid(rule.getRuleid());
        rule = this.ruleDao.selectOne(ruleParam);
        int result = ruleDao.delete(ruleParam);
        Knowledge knowledge = new Knowledge();
        knowledge.setCode(rule.getCode());
        this.knowledgeDao.delete(knowledge,"deleteKnowledgeByCode");
        String filename = rule.getMonitorPointid()+"_"+sdf.format(new Date())+".drl";
        List<Rule> rules = findRulesByPointid(rule.getMonitorPointid());
        if(CodeUtil.isNotNullZero(rules)){
            String drlAppendHeadString = FileUtil.makeFileHead();
            StringBuffer sb = new StringBuffer(drlAppendHeadString);
            for(Rule r : rules){
                sb.append(r.getRulecontent());
            }
            // 生成尾部
//            String end = FileUtil.makeFileEnd(rule);
//            sb.append(end);
            //生成文件并启动规则
            ossUtil.OSSUploadUtil(sb.toString(),rule.getMonitorPointid(),filename,rule);
        }else{
            //更新monitorpoint表中相关的规则信息
            if(CodeUtil.isNotNullEmpty(rule.getMonitorPointid())){
                updateMonitorPointRule(rule.getMonitorPointid());
                //停止规则
                diagnosisExecutor.ruleStop(rule.getMonitorPointid());
            }
        }
        log.info("Leave deleteRule result={}",result);
        return result == 1 ? true : false;
    }

    @Override
    public List<Rule> findRulesByPointid(String pointid) {
        log.info("Enter findRulesByPointid pointid={}",pointid);
        Rule rule = new Rule();
        rule.setMonitorPointid(pointid);
        List<Rule> rules = this.ruleDao.selectList(rule);
        log.info("Get rule list size={}",rules!=null ? rules.size() : 0);
        return rules;
    }

    @Override
    public Boolean updateMonitorPointRule(String pointid) {
        log.info("Enter updateMonitorPointRule pointid={}",pointid);
        MonitorPoint monitorPoint = new MonitorPoint();
        monitorPoint.setPointid(pointid);
        int rusult = this.monitorPointDao.update(monitorPoint,"updateDrlnameAndRunrate");
        log.info("Leave updateMonitorPointRule rusult={}",rusult);
        return rusult == 1 ? true : false;
    }

    @Override
    public Boolean updateKnowledgeidByCode(String code, String knowledegeid) {
        log.info("Enter updateKnowledgeidByCode code={},knowledegeid={}",code,knowledegeid);
        Rule rule = new Rule();
        rule.setCode(code);
        rule.setKnowledgeid(knowledegeid);
        int res = this.ruleDao.update(rule,"updateKnowledgeidByCode");
        boolean result = res == 1 ? true : false;
        log.info("Leave updateKnowledgeidByCode result={}",result);
        return result;
    }

    @Override
    public Boolean updateKnowledgeidByCodes(String code) {
        log.info("Enter updateKnowledgeidByCodes code={}",code);
        Rule rule = new Rule();
        rule.setCode(code);
        int res = this.ruleDao.update(rule,"updateKnowledgeidByCodes");
        boolean result = res == 1 ? true : false;
        log.info("Leave updateKnowledgeidByCodes result={}",result);
        return result;
    }
}
