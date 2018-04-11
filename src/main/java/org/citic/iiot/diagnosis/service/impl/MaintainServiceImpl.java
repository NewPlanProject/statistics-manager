package org.citic.iiot.diagnosis.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.citic.iiot.app.core.data.Result;
import org.citic.iiot.app.core.data.ResultCode;
import org.citic.iiot.app.core.util.CodeUtil;
import org.citic.iiot.app.core.util.UUIDUtil;
import org.citic.iiot.diagnosis.dao.FaultrecordDao;
import org.citic.iiot.diagnosis.dao.MaintainDao;
import org.citic.iiot.diagnosis.domain.Faultrecord;
import org.citic.iiot.diagnosis.domain.Maintain;
import org.citic.iiot.diagnosis.service.MaintainService;
import org.citic.iiot.diagnosis.vo.MaintainInVO;
import org.citic.iiot.diagnosis.vo.MaintainSubOutVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yxw on 2017/7/17.
 */
@Slf4j
@Service
@Transactional
public class MaintainServiceImpl implements MaintainService {

    @Autowired
    private MaintainDao maintainDao;

    @Autowired
    private FaultrecordDao faultrecordDao;

    @Override
    public Result<Map<String,Object>> getMaintainList(MaintainInVO maintainInVO) {
        Result<Map<String, Object>> res = new Result<Map<String, Object>>(ResultCode.ERROR_DATA,"查询失败",null);
        log.info("Enter getMaintainList maintainInVO={}",maintainInVO);
        List<Maintain> maintains = null;
        Map<String, Object> resultMap = null;
        Maintain maintain= new Maintain();
        BeanUtils.copyProperties(maintainInVO, maintain);
        try {
            //根据多个deviceid查询
            if(StringUtils.isNotEmpty(maintain.getDeviceid())){
                String[] deviceids = maintain.getDeviceid().split(",");
                maintain.setDeviceidList(Arrays.asList(deviceids));
            }
            maintains = this.maintainDao.selectList(maintain,"pageMaintain");
            for(Maintain m:maintains){
                Faultrecord faultrecord=new Faultrecord(); //根据故障记录id获取故障名称
                if(StringUtils.isNotEmpty(m.getFaultid())&&m.getFaultid().length()==32){
                    faultrecord.setId(m.getFaultid());
                    faultrecord = this.faultrecordDao.selectOne(faultrecord, "queryFaultrecordObject");
                    if(CodeUtil.isNotNull(faultrecord)){
                        if(StringUtils.isNotEmpty(faultrecord.getName())){
                            m.setFaultname(faultrecord.getName());
                        }
                    }
                }
            }
            int totalSize = this.maintainDao.selectCount(maintain,"pageMaintainCount");
            resultMap = new HashMap<String, Object>();
            resultMap.put("rows",maintains);
            resultMap.put("totalSize",totalSize);
        }catch (Exception e){
            log.error("Find Exception", e);
        }
        res.setCode(ResultCode.SUCCESS);
        res.setContent(resultMap);
        res.setMsg("查询成功");
        log.info("Get maintains list size={}",maintains!=null?maintains.size() : "");
        return res;
    }

    @Override
    public int save(Maintain maintain) {
        log.info("Enter save maintain={}",maintain);
        int result =0;
        if(StringUtils.isEmpty(maintain.getId())){
            String uuid = UUIDUtil.creatUUID();
            maintain.setId(uuid);
            maintain.setCreatetime(new Date());
            //******** 1828_新建维修维护记录，数据库里的update_date为空_贾楠_20170914_start ********
            maintain.setUpdateDate(new Date());
            //******** 1828_新建维修维护记录，数据库里的update_date为空_贾楠_20170914_end ********
            result=maintainDao.save(maintain);
        }else{
            maintain.setUpdateDate(new Date());
            result=maintainDao.update(maintain);
        }
        return result;
    }

    @Override
    public void del(String[] ids) {
        log.info("Enter del ids={}",ids);
        List<Maintain> maintainList = new ArrayList<>();
        try {
            for (String id : ids) {
                Maintain maintain = new Maintain();
                maintain.setId(id);
                maintainList.add(maintain);
            }
            maintainDao.deleteBatch(maintainList);
        } catch (Exception e) {
            log.error("Find Exception", e);
        }
    }

    @Override
    public Maintain getMaintainById(String id) {
        log.info("Enter getMaintainById id={}",id);
        Maintain maintain = new Maintain();
        maintain.setId(id);
        Maintain mt = maintainDao.selectOne(maintain);
        if(StringUtils.isNotEmpty(mt.getFaultid())&&mt.getFaultid().length()==32){
            Faultrecord faultrecord=new Faultrecord();
            faultrecord.setId(mt.getFaultid());
            faultrecord = this.faultrecordDao.selectOne(faultrecord, "queryFaultrecordObject");
            mt.setFaultname(faultrecord.getName());
        }
        return mt;
    }

    @Override
    public List<MaintainSubOutVO> getMaintainByIdExport(String[] ids) {
        List<MaintainSubOutVO> maintainList = new ArrayList<>();
        try {
            for (String id : ids) {
                Maintain maintain = new Maintain();
                maintain.setId(id);
                maintain = maintainDao.selectOne(maintain, "getMaintainByIdExport");
                //************ 1822_导出的列表数据与页面列表的不一致_贾楠_2017/09/14_start
                if(StringUtils.isNotEmpty(maintain.getFaultid())&&maintain.getFaultid().length()==32){
                    Faultrecord faultrecord=new Faultrecord();
                    faultrecord.setId(maintain.getFaultid());
                    faultrecord = this.faultrecordDao.selectOne(faultrecord, "queryFaultrecordObject");
                    maintain.setFaultname(faultrecord.getName());
                }
                //************ 1822_导出的列表数据与页面列表的不一致_贾楠_2017/09/14_end
                MaintainSubOutVO maintainSubOutVO=new MaintainSubOutVO();
                BeanUtils.copyProperties(maintain, maintainSubOutVO);
                maintainList.add(maintainSubOutVO);
            }
        } catch (Exception e) {
            log.error("Find Exception", e);
        }
        return maintainList;
    }

    @Override
    public Map<String, Integer> getMaintainCount(Maintain maintain) {
        Map<String,Integer> res = new HashMap<String,Integer>();
        //维修本月设备数量
        maintain.setType("1");
        Integer repairCount = this.maintainDao.selectCount(maintain, "getRepairCount");
        //维护本月设备数量
        maintain.setType("2");
        Integer maintainCount = this.maintainDao.selectCount(maintain, "getMaintainCount");
        Integer total=repairCount+maintainCount;
        res.put("repairCount",repairCount);
        res.put("maintainCount",maintainCount);
        res.put("total",total);
        return res;
    }

    @Override
    public Map<String, Object> getMaintainCountLast3Month(Maintain maintain) {
        //声明返回值
        Map<String,Object> result=new HashMap<String,Object>();
        //获取当前时间
        Date endDate = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
        //声明数组
        String[] dateArray=new String[3];
        Integer[] repairCount=new Integer[3];
        Integer[] maintainCount=new Integer[3];
        try {
            for(int i=0;i<3;i++){
                endDate=sdf.parse(sdf.format(endDate));
                maintain.setMonth(sdf.format(endDate));
                int repairCountByMonth = this.maintainDao.selectCount(maintain, "getRepairCountByMonth");
                repairCount[2-i]=repairCountByMonth;
                int maintainCountByMonth = this.maintainDao.selectCount(maintain, "getMaintainCountByMonth");
                maintainCount[2-i]=maintainCountByMonth;
                dateArray[2-i]=sdf.format(endDate);
                Calendar c = Calendar.getInstance();
                c.setTime(endDate);   //设置当前日期
                c.add(Calendar.MONTH, -1); //日期减1月
                endDate = c.getTime();//结果
            }
        } catch (ParseException e) {
            log.error("Find Exception", e);
        }
        result.put("dateList",dateArray);
        result.put("repairCountList",repairCount);
        result.put("maintainCountList",maintainCount);
        return result;
    }


    @Override
    public Map<String, Object> findMaintainCountLast3Month(Maintain maintain) {
        long begin = System.currentTimeMillis();
        //声明返回值
        Map<String,Object> result=new HashMap<String,Object>();
        //获取当前时间
        Date endDate = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
        //声明数组
        String[] dateArray=new String[3];
        Integer[] repairCount=new Integer[3];
        Integer[] maintainCount=new Integer[3];
        try {
            long begin1 = System.currentTimeMillis();
            List<Map<String, Object>> repairResults = this.maintainDao.selectMaps(maintain, "findRepairCountByMonth");
            log.warn("Exec findRepairCountByMonth costs ={}", System.currentTimeMillis() - begin1);
            Map<String,Integer> repairMap=new HashMap<>();
            for (Map map : repairResults){      //将获取数据格式更改：key:value=时间:数量
                repairMap.put((String)map.get("t"),((Long) map.get("n")).intValue());
            }
            long begin2 = System.currentTimeMillis();
            List<Map<String, Object>> maintainResults = this.maintainDao.selectMaps(maintain, "findMaintainCountByMonth");
            log.warn("Exec findMaintainCountByMonth costs ={}", System.currentTimeMillis() - begin2);
            Map<String,Integer> maintainMap=new HashMap<>();
            for (Map map : maintainResults){      //将获取数据格式更改：key:value=时间:数量
                maintainMap.put((String)map.get("t"),((Long) map.get("n")).intValue());
            }
            for(int i=0;i<3;i++){
                endDate=sdf.parse(sdf.format(endDate));
                dateArray[2-i]=sdf.format(endDate);
                if(CodeUtil.isNotNull(repairMap.get(dateArray[2-i]))){
                    repairCount[2-i]=repairMap.get(dateArray[2-i]);
                }else{
                    repairCount[2-i]=0;
                }
                if(CodeUtil.isNotNull(maintainMap.get(dateArray[2-i]))){
                    maintainCount[2-i]=maintainMap.get(dateArray[2-i]);
                }else{
                    maintainCount[2-i]=0;
                }
                Calendar c = Calendar.getInstance();
                c.setTime(endDate);   //设置当前日期
                c.add(Calendar.MONTH, -1); //日期减1月
                endDate = c.getTime();//结果
            }
        } catch (ParseException e) {
            log.error("Find Exception", e);
        }
        result.put("dateList",dateArray);
        result.put("repairCountList",repairCount);
        result.put("maintainCountList",maintainCount);
        log.warn("Exec Inner maintainCountLast3Month costs ={}", System.currentTimeMillis() - begin);
        return result;
    }

}
