package org.citic.iiot.diagnosis.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.citic.iiot.app.core.data.Result;
import org.citic.iiot.app.core.data.ResultCode;
import org.citic.iiot.app.core.session.User;
import org.citic.iiot.app.core.util.CodeUtil;
import org.citic.iiot.app.core.util.UUIDUtil;
import org.citic.iiot.diagnosis.dao.FaultrecordDao;
import org.citic.iiot.diagnosis.dao.KnowledgeDao;
import org.citic.iiot.diagnosis.dao.RuleDao;
import org.citic.iiot.diagnosis.domain.Faultrecord;
import org.citic.iiot.diagnosis.domain.Knowledge;
import org.citic.iiot.diagnosis.domain.Rule;
import org.citic.iiot.diagnosis.service.FaultrecordService;
import org.citic.iiot.diagnosis.vo.FaultrecordInVO;
import org.citic.iiot.diagnosis.vo.KnowledgeRelateFaultInVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: Mr.zheng
 * Date: 2017/8/16
 * Time: 11:40
 */
@Service
@Slf4j
@Transactional
public class FaultrecordServiceImpl implements FaultrecordService {

    @Autowired
    private FaultrecordDao faultrecordDao;

    @Autowired
    private KnowledgeDao knowledgeDao;

    @Autowired
    private RuleDao ruleDao;

    @Override
    public List<Faultrecord> getDeviceRunStatus(String deviceid) {
        log.info("Enter getDeviceRunStatus deviceid={}",deviceid);
        Faultrecord faultrecord = new Faultrecord();
        faultrecord.setDeviceid(deviceid);
        //根据code获取知识库分析数据集合
        List<Faultrecord> faultrecords = faultrecordDao.selectList(faultrecord,"deviceRunStatus");
        log.info("Get Faultrecord list size={}", faultrecords != null ? faultrecords.size() : 0);
        return faultrecords;
    }

    @Override
    public List<Faultrecord> getDeviceRunStatusList(List<String> deviceIds) {
        log.info("Enter getDeviceRunStatus deviceIds={}",deviceIds);
        Faultrecord faultrecord = new Faultrecord();
        faultrecord.setDeviceidList(deviceIds);
        List<Faultrecord> faultrecords = faultrecordDao.selectList(faultrecord,"getDeviceRunStatusList");
        log.info("Get Faultrecord list size={}", faultrecords != null ? faultrecords.size() : 0);
        return faultrecords;
    }

    @Override
    public Result<Map<String, Object>> getfaultrecordList(FaultrecordInVO faultrecordInVO) {
        Result<Map<String, Object>> res = new Result<Map<String, Object>>(ResultCode.ERROR_DATA,"查询失败",null);
        log.info("Enter getfaultrecordList faultrecordInVO={}",faultrecordInVO);
        List<Faultrecord> faultrecords = null;
        Map<String, Object> resultMap = null;
        Faultrecord faultrecord= new Faultrecord();
        //将获取的空字符串赋值为null（避免查出空列表）
        if(faultrecordInVO.getStatus()=="")faultrecordInVO.setStatus(null);
        BeanUtils.copyProperties(faultrecordInVO, faultrecord);
        int totalSize=0;
        try {
            //根据多个deviceid查询
            if(StringUtils.isNotEmpty(faultrecord.getDeviceid())){
                String[] deviceids = faultrecord.getDeviceid().split(",");
                faultrecord.setDeviceidList(Arrays.asList(deviceids));
            }
            faultrecords = this.faultrecordDao.selectList(faultrecord, "pageFaultrecord");
            totalSize = this.faultrecordDao.selectCount(faultrecord,"pageFaultrecordCount");
            resultMap = new HashMap<String, Object>();
            resultMap.put("rows",faultrecords);
            resultMap.put("totalSize",totalSize);
        }catch (Exception e){
            log.error("Find Exception", e);
        }
        res.setCode(ResultCode.SUCCESS);
        res.setContent(resultMap);
        res.setMsg("查询成功");
        log.info("Get getfaultrecordList size={}",faultrecords!=null?faultrecords.size() : "");
        return res;
    }

    @Override
    public void updateStatusAndAddKnowledge(KnowledgeRelateFaultInVO knowledgeRelateFaultInVO,User user) {
        log.info("Enter updateStatusAndAddKnowledge knowledgeRelateFaultInVO={}",knowledgeRelateFaultInVO);
        Faultrecord faultrecord=new Faultrecord();
        faultrecord.setId(knowledgeRelateFaultInVO.getId());
        faultrecord.setStatus("1");
        Knowledge knowledge=new Knowledge();
        //根据现象代码(Code)查到此知识条目的现象名称和型号或关联
        Rule rule=new Rule();
        rule.setCode(knowledgeRelateFaultInVO.getCode());
        try {
            Rule ruleResult = ruleDao.selectOne(rule);
            //根据现象代码(Code)查到此知识条目应该加入的父节点的knowId
            Knowledge knowledgeDB=new Knowledge();
            knowledgeDB.setCode(knowledgeRelateFaultInVO.getCode());
            knowledgeDB.setParentid("father");
            knowledgeDB = knowledgeDao.selectOne(knowledgeDB);
            //给Knowledge赋值
            String knowid = oprationKnowledge(knowledgeRelateFaultInVO, knowledge, user, ruleResult);
            if(CodeUtil.isNotNull(knowledgeDB)){        //如果根据code查到的knowid不为空，将将此数据作为父id
                if(StringUtils.isNotEmpty(knowledgeDB.getKnowid())){
                    knowledge.setParentid(knowledgeDB.getKnowid());
                }
            }else{                                     //为空,将此knowledge先加入知识条目作为父节点，再复制相同数据,为子节点
                knowledge.setParentid("father");
                knowledge.setKnowname(ruleResult.getRulename());
                this.knowledgeDao.save(knowledge);
                ruleResult.setKnowledgeid(knowid);  //将规则库与知识库关联起来
                this.ruleDao.update(ruleResult);
                oprationKnowledge(knowledgeRelateFaultInVO,knowledge,user,ruleResult);
                knowledge.setKnowname(null);
                knowledge.setParentid(knowid);
            }
            this.knowledgeDao.save(knowledge);
            faultrecord.setKnowid(knowledge.getKnowid());
            faultrecord.setUpdatetime(new Date());
            //对状态和相应情况进行修改
            this.faultrecordDao.update(faultrecord);
        }catch (Exception e){
            log.error("Find Exception", e);
        }
    }

    @Override
    public Result<Map<String, Object>> getFaultrecordObjectUnsolved(Faultrecord faultrecord,User user) {
        Result<Map<String, Object>> res = new Result<Map<String, Object>>(ResultCode.ERROR_DATA,"查询失败",null);
        log.info("Enter getFaultrecordObject faultrecord={}",faultrecord);
        Map<String, Object> resultMap = null;
        try {
            faultrecord = this.faultrecordDao.selectOne(faultrecord, "queryFaultrecordObject");
            Knowledge knowledge=new Knowledge();
            //判断该用户是哪个角色：1.超级用户、2.管理员、3.普通用户
            if(CodeUtil.isNotNull(user)){
                switch(user.getRolelevel()){
                    case "1":
                        knowledge.setOrgcode(user.getOrgcode());
                        break;
                    case "2":
                        knowledge.setOrgid(user.getOrgid());
                        break;
                    case "3":
                        knowledge.setUserid(user.getUserid());
                        break;
                }
            }
            knowledge.setCode(faultrecord.getCode());
            knowledge.setType(faultrecord.getType());
            int j=0;
            List<Knowledge> knowledgesDeviceIdNullList = this.knowledgeDao.selectList(knowledge, "getKnowledgesDeviceIdNull");
            //TODO 以后修改页面显示的内容
            if(!CollectionUtils.isEmpty(knowledgesDeviceIdNullList)){
                for (int i = 0; i <knowledgesDeviceIdNullList.size() ; i++) {
                    knowledgesDeviceIdNullList.get(i).setViewName("方案"+(j+1));
                    j++;
                }
            }
            knowledge.setDeviceid(faultrecord.getDeviceid());
            List<Knowledge> knowledgesDeviceIdList = this.knowledgeDao.selectList(knowledge, "getKnowledgesDeviceId");
            if(!CollectionUtils.isEmpty(knowledgesDeviceIdList)){
                for (int i = 0; i <knowledgesDeviceIdList.size() ; i++) {
                    knowledgesDeviceIdList.get(i).setViewName("方案"+(j+1));
                    j++;
                }
                knowledgesDeviceIdNullList.addAll(knowledgesDeviceIdList);
            }
            faultrecord.setKnowledges(knowledgesDeviceIdNullList);
            resultMap = new HashMap<String, Object>();
            resultMap.put("rows",faultrecord);
        }catch (Exception e){
            log.error("Find Exception", e);
        }
        res.setCode(ResultCode.SUCCESS);
        res.setContent(resultMap);
        res.setMsg("查询成功");
        log.info("Get faultrecord={}",faultrecord);
        return res;
    }

    @Override
    public Result<Map<String, Object>> getFaultrecordObjectSolved(Faultrecord faultrecord) {
        Result<Map<String, Object>> res = new Result<Map<String, Object>>(ResultCode.ERROR_DATA,"查询失败",null);
        log.info("Enter getFaultrecordObject faultrecord={}",faultrecord);
        Map<String, Object> resultMap = null;
        try {
            faultrecord = this.faultrecordDao.selectOne(faultrecord, "queryFaultrecordObject");
            Knowledge knowledge=new Knowledge();
            if(StringUtils.isNotEmpty(faultrecord.getKnowid())){
                knowledge.setKnowid(faultrecord.getKnowid());
                knowledge=this.knowledgeDao.selectOne(knowledge,"selectListByFid");
            }else{
                knowledge.setAnalyse(faultrecord.getAnalyse());
                knowledge.setSolution(faultrecord.getSolution());
            }
            faultrecord.setKnowledge(knowledge);
            resultMap = new HashMap<String, Object>();
            resultMap.put("rows",faultrecord);
        }catch (Exception e){
            log.error("Find Exception", e);
        }
        res.setCode(ResultCode.SUCCESS);
        res.setContent(resultMap);
        res.setMsg("查询成功");
        log.info("Get faultrecord={}",faultrecord);
        return res;
    }

    @Override
    public void updateStatus(KnowledgeRelateFaultInVO knowledgeRelateFaultInVO) {
        log.info("Enter updateStatus knowledgeRelateFaultInVO={}",knowledgeRelateFaultInVO);
        Faultrecord faultrecord=new Faultrecord();
        faultrecord.setId(knowledgeRelateFaultInVO.getId());
        if(StringUtils.isNotEmpty(knowledgeRelateFaultInVO.getAnalyse())){
            faultrecord.setAnalyse(knowledgeRelateFaultInVO.getAnalyse());
            faultrecord.setSolution(knowledgeRelateFaultInVO.getSolution());
        }
        try {
            faultrecord.setStatus("1");
            faultrecord.setUpdatetime(new Date());
            this.faultrecordDao.update(faultrecord);
        }catch (Exception e){
            log.error("Find Exception", e);
        }
    }

    @Override
    public void batchDisposeStatus(String id) {          //--------  批量处理预警/故障
        log.info("Enter batchDisposeStatus id={}",id);
        Faultrecord faultrecord=new Faultrecord();
        String[] split = id.split(",");
        for (String faultId:split) {
            if(faultId.split("_")[1].equals("0")){
                faultrecord.setId(faultId.split("_")[0]);
                faultrecord.setUpdatetime(new Date());
                faultrecord.setStatus("1");
                this.faultrecordDao.update(faultrecord);
            }
        }
    }

    @Override
    public Map<String, Integer> getUnsolvedFaultrecordCount(Faultrecord faultrecord) {
        log.info("Enter getUnsolvedFaultrecordCount faultrecord={}",faultrecord);
        Map<String,Integer> res = new HashMap<String,Integer>();
        //根据多个deviceid查询
        if(StringUtils.isNotEmpty(faultrecord.getDeviceid())){
            String[] deviceids = faultrecord.getDeviceid().split(",");
            faultrecord.setDeviceidList(Arrays.asList(deviceids));
        }
        Integer UnsolvedWarnCount = this.faultrecordDao.selectCount(faultrecord, "getUnsolvedWarnCount");
        Integer UnsolvedFaultCount = this.faultrecordDao.selectCount(faultrecord, "getUnsolvedFaultCount");
        Integer total=UnsolvedWarnCount+UnsolvedFaultCount;
        res.put("UnsolvedWarnCount",UnsolvedWarnCount);
        res.put("UnsolvedFaultCount",UnsolvedFaultCount);
        res.put("total",total);
        return res;
    }

    @Override
    public Map<String, Object> getFaultrecordCountLast7(Faultrecord faultrecord) {
        long begin = System.currentTimeMillis();
        //声明返回值
        Map<String,Object> result=new HashMap<String,Object>();
        //声明数组
        String[] dateArray=new String[7];
        Integer[] warnUnsolvedCount=new Integer[7];
        Integer[] faultUnsolvedCount=new Integer[7];
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfend=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date end=new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(end);
            //查询七天：包括当天日期，所以向前回滚六天
            calendar.add(Calendar.DAY_OF_MONTH,-6);
            Date start = calendar.getTime();
            //给日期赋值
            faultrecord.setStartDate(sdf.parse(sdf.format(start)));
            String endTime = sdf.format(end)+" 23:59:59";
            faultrecord.setEndDate(sdfend.parse(endTime));
            long begin1 = System.currentTimeMillis();
            List<Map<String, Object>> warnResults = this.faultrecordDao.selectMaps(faultrecord, "get7DaysWarnCount");
            log.warn("Exec get7DaysWarnCount costs ={}", System.currentTimeMillis() - begin1);
            Map<String,Integer> warnMap=new HashMap<>();
            for (Map map : warnResults){      //将获取数据格式更改：key:value=时间:数量
                warnMap.put((String)map.get("t"),((Long) map.get("n")).intValue());
            }
            long begin2 = System.currentTimeMillis();
            List<Map<String, Object>> faultResults = this.faultrecordDao.selectMaps(faultrecord, "get7DaysFaultCount");
            log.warn("Exec getFaultCountByDate costs ={}", System.currentTimeMillis() - begin2);
            Map<String,Integer> faultMap=new HashMap<>();
            for (Map map : faultResults){
                faultMap.put((String)map.get("t"),((Long) map.get("n")).intValue());
            }
            Date endDate=faultrecord.getEndDate();
            for(int i=0;i<7;i++){             //将数组填充完整，如果对应日期无数据，用0补充
                dateArray[6-i]=sdf.format(endDate);
                if(CodeUtil.isNotNull(warnMap.get(dateArray[6-i]))){
                    warnUnsolvedCount[6-i]=warnMap.get(dateArray[6-i]);
                }else{
                    warnUnsolvedCount[6-i]=0;
                }
                if(CodeUtil.isNotNull(faultMap.get(dateArray[6-i]))){
                    faultUnsolvedCount[6-i]=faultMap.get(dateArray[6-i]);
                }else{
                    faultUnsolvedCount[6-i]=0;
                }
                Calendar c = Calendar.getInstance();
                c.setTime(endDate);   //设置当前日期
                c.add(Calendar.DATE, -1); //日期减1
                endDate = c.getTime();//结果
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("dateList",dateArray);
        result.put("warnUnsolvedCountList",warnUnsolvedCount);
        result.put("faultUnsolvedCountList",faultUnsolvedCount);
        log.warn("Exec INNER getUnsolvedFaultrecordCountLast7 costs ={}", System.currentTimeMillis() - begin);
        return result;
    }


    @Override
    public Map<String, Object> getUnsolvedFaultrecordCountLast7(Faultrecord faultrecord) {
        //声明返回值
        Map<String,Object> result=new HashMap<String,Object>();
        //获取当前时间
        Date endDate = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        //声明数组
        String[] dateArray=new String[7];
        Integer[] warnUnsolvedCount=new Integer[7];
        Integer[] faultUnsolvedCount=new Integer[7];
        try {
            for(int i=0;i<7;i++){
                endDate=sdf.parse(sdf.format(endDate));
                faultrecord.setEndDate(endDate);
                int warnCount = this.faultrecordDao.selectCount(faultrecord, "getWarnCountByDate");
                warnUnsolvedCount[6-i]=warnCount;
                int faultCount = this.faultrecordDao.selectCount(faultrecord, "getFaultCountByDate");
                faultUnsolvedCount[6-i]=faultCount;
                dateArray[6-i]=sdf.format(endDate);
                Calendar c = Calendar.getInstance();
                c.setTime(endDate);   //设置当前日期
                c.add(Calendar.DATE, -1); //日期减1
                endDate = c.getTime();//结果
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        result.put("dateList",dateArray);
        result.put("warnUnsolvedCountList",warnUnsolvedCount);
        result.put("faultUnsolvedCountList",faultUnsolvedCount);
        return result;
    }

    //更改状态并加入知识库操作
    private String oprationKnowledge(KnowledgeRelateFaultInVO knowledgeRelateFaultInVO,
                                   Knowledge knowledge,User user,Rule ruleResult){
        BeanUtils.copyProperties(knowledgeRelateFaultInVO, knowledge);
        if(CodeUtil.isNotNull(user)){   //将用户信息加入知识条目
            knowledge.setUserid(user.getUserid());
            knowledge.setOrgid(user.getOrgid());
            knowledge.setOrgcode(user.getOrgcode());
        }
        String knowid = UUIDUtil.creatUUID();
        knowledge.setKnowid(knowid);
        knowledge.setCreatetime(new Date());
        knowledge.setUpdatetime(new Date());
        if(CodeUtil.isNotNull(ruleResult)){
            knowledge.setRelationid(ruleResult.getRelationid());
            knowledge.setRelationname(ruleResult.getRelationname());
            knowledge.setDevicemodelid(ruleResult.getDevicemodelid());
            knowledge.setDevicemodelname(ruleResult.getDevicemodelname());
            knowledge.setDeviceid(ruleResult.getDeviceuuid());
        }
        return knowid;
    }
}
