package org.citic.iiot.diagnosis.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.citic.iiot.app.core.data.Result;
import org.citic.iiot.app.core.data.ResultCode;
import org.citic.iiot.app.core.json.BaseJSON;
import org.citic.iiot.app.core.session.SessionInfo;
import org.citic.iiot.app.core.session.User;
import org.citic.iiot.app.core.util.CodeUtil;
import org.citic.iiot.app.core.validator.CiticValid;
import org.citic.iiot.app.core.validator.CiticValids;
import org.citic.iiot.diagnosis.domain.Faultrecord;
import org.citic.iiot.diagnosis.service.CacheService;
import org.citic.iiot.diagnosis.service.FaultrecordService;
import org.citic.iiot.diagnosis.vo.FaultrecordInVO;
import org.citic.iiot.diagnosis.vo.KnowledgeRelateFaultInVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Mr.zheng
 * Date: 2017/8/16
 * Time: 11:42
 */
@Slf4j
@Controller
@RequestMapping("/faultrecord")
public class FaultrecordController implements SessionInfo{

    @Autowired
    private CacheService cacheService;

    @Autowired
    private FaultrecordService faultrecordService;

    @ApiOperation(value = "获取监控点状态", notes = "根据设备ID来获取信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceid", value = "设备uuid", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pointids", value = "监控点ids", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping("/monitorPointStatus")
    @ResponseBody
    public Map getMonitorPointStatus(@RequestParam(value = "deviceid")String deviceid,@RequestParam(value = "pointids")String[] pointids) {
        log.info("Enter getMonitorPointStatus deviceid={},pointids={}", deviceid,pointids);
        Map<String,Object> res = new HashMap<String,Object>();
        if(CodeUtil.isNotNullEmpty(deviceid) && CodeUtil.isNotNull(pointids)){
            res = cacheService.getHashEntries("deviceid_"+deviceid);
            if(CodeUtil.isNotNull(res)){
                Map<String,Object> noExtisMap = new HashMap<String,Object>();
                for(int i=0; i<pointids.length; i++){
                    noExtisMap.put(pointids[i],-1);
                    noExtisMap.put(pointids[i]+"_record","{\"type\":-1}");
                }
                noExtisMap.putAll(res);
                res = noExtisMap;
            }else{
                for(int i=0; i<pointids.length; i++){
                    res.put(pointids[i],-1);
                    res.put(pointids[i]+"_record","{\"type\":-1}");
                }
            }
        }
        log.info("Leave getMonitorPointStatus map={}", res);
        return res;
    }

    @ApiOperation(value = "获取关联监控点状态", notes = "根据设备ID来获取信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramJson", value = "设备uuid与监控点id", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/relationMonitorPointStatus", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map relationMonitorPointStatus(@RequestParam(value = "paramJson")String paramJson) {
        log.info("Enter relationMonitorPointStatus paramJson={}", paramJson);
        List<Map<String,Object>> mapList = (List<Map<String,Object>>)JSON.parse(paramJson);
        Map<String,Object> result = new HashMap<String,Object>();
        for(Map<String,Object> map : mapList){
            Map<String,Object> res = new HashMap<String,Object>();
            String deviceid = map.get("deviceid") != null ? map.get("deviceid").toString() : null;
            String pointids = map.get("pointids") != null ? map.get("pointids").toString(): null;
            if(CodeUtil.isNotNullEmpty(deviceid) && CodeUtil.isNotNullEmpty(pointids)){
                res = cacheService.getHashEntries("deviceid_"+deviceid);
                if(CodeUtil.isNotNull(res)){
                    Map<String,Object> noExtisMap = new HashMap<String,Object>();
//                    for(int i=0; i<pointids.length; i++){
//                        noExtisMap.put(pointids[i],-1);
//                        noExtisMap.put(pointids[i]+"_record","{\"type\":-1}");
//                    }
                    noExtisMap.put(pointids,-1);
                    noExtisMap.put(pointids+"_record","{\"type\":-1}");
                    noExtisMap.putAll(res);
                    res = noExtisMap;
                }else{
                    res.put(pointids,-1);
                    res.put(pointids+"_record","{\"type\":-1}");
                }
            }
            result.putAll(res);
        }
        log.info("Leave relationMonitorPointStatus map={}", result);
        return result;
    }

    @ApiOperation(value = "获取设备运行状态", notes = "根据设备ID来获取信息")
    @ApiImplicitParam(name = "deviceIds", value = "deviceIds", required = true, dataType = "string", paramType = "query")
    @GetMapping("/deviceRunStatus")
    @ResponseBody
    public Map getDeviceRunStatus(@RequestParam(value = "deviceIds")String[] deviceIds) {

        log.info("Enter getDeviceRunStatus deviceIds={}", deviceIds);

        Map<String,String> res = new HashMap<String,String>();

        if (deviceIds.length > 0) {

            List<String> deviceIdList = Arrays.asList(deviceIds);
            for (String deviceId : deviceIdList) {
                res.put(deviceId, "0");
            }

            List<Faultrecord> faultrecords = faultrecordService.getDeviceRunStatusList(deviceIdList);
            if ((faultrecords != null) && (faultrecords.size() > 0)) {
                for (Faultrecord faultrecord : faultrecords) {
                    if (StringUtils.equals(res.get(faultrecord.getDeviceid()), "2")) {
                        continue;
                    } else  {
                        res.put(faultrecord.getDeviceid(), faultrecord.getType());
                    }
                }
            }

        }

        log.info("Leave getDeviceRunStatus map={}", res);
        return res;
    }


    @ApiOperation(value = "故障/预警记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "faultrecordInVO", value = "故障/预警对象", required = false, paramType = "body", dataType = "FaultrecordInVO")
    })
    @PostMapping(value = "faultList", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String faultList(@RequestBody FaultrecordInVO faultrecordInVO, HttpServletRequest request){
        log.info("Enter faultList faultrecordInVO={}", faultrecordInVO);
        Result<Map<String,Object>> resBean = new Result<Map<String,Object>>(ResultCode.SUCCESS,"查询成功",null);
        //获取登录用户的相关信息
        User user=getUser(request);
        try {
            //判断该用户是哪个角色：1.超级用户、2.管理员、3.普通用户
            if(CodeUtil.isNotNull(user)){
                switch(user.getRolelevel()){
                    case "1":
                        faultrecordInVO.setOrgCode(user.getOrgcode());
                        break;
                    case "2":
                        faultrecordInVO.setOrgid(user.getOrgid());
                        break;
                    case "3":
                        faultrecordInVO.setUserid(user.getUserid());
                        break;
                }
            }
            resBean = faultrecordService.getfaultrecordList(faultrecordInVO);
        }catch (Exception e){
            log.error("faultList failed",e);
        }
        log.info("faultList={}",resBean);
        return JSON.toJSONString(resBean);
    }

//    @CiticValids({
//            @CiticValid(value={"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","故障/预警记录ID不能为空"},fieldName = "id",index = 1),
//            @CiticValid(value={"[01]{1}","状态必须为1或0"},fieldName = "status",index = 2)
//    })
    @ApiOperation(value="根据故障/预警id和status查询故障/预警记录", notes="根据id和status查询数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "status", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value="getFaultrecordObject", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getFaultrecordObject(@RequestParam(value = "id") String id,
                                       @RequestParam(value = "status") String status,
                                       HttpServletRequest request) {
        log.info("Enter getFaultrecordObject id={},status={}", id,status);
        Result<Map<String,Object>> resBean = new Result<Map<String,Object>>(ResultCode.SUCCESS,"查询成功",null);
        //获取登录用户的相关信息
        User user=getUser(request);
        Faultrecord faultrecord = new Faultrecord();
        faultrecord.setId(id);
        faultrecord.setStatus(status);
        try {
            if(status.equals("0")){
                resBean = faultrecordService.getFaultrecordObjectUnsolved(faultrecord,user);
            }else{
                resBean = faultrecordService.getFaultrecordObjectSolved(faultrecord);
            }
        }catch (Exception e){
            log.error("getFaultrecordObject failed",e);
        }
        log.info("getFaultrecordObject ={}" , resBean);
        return BaseJSON.toJSONString(resBean,SerializerFeature.WriteMapNullValue);
//        return resBean;
    }

    @CiticValids({
            @CiticValid(value={"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","故障/预警记录ID不能为空"},fieldName = "id",index = 1)
    })
    @ApiOperation(value="根据故障/预警id更改状态", notes="根据id更改状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value="batchDisposeStatus", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String batchDisposeStatus(@RequestParam(value = "id") String id) {
        Boolean result=false;
        log.info("Enter getFaultrecordObject id={}", id);
        try {  //如果deviceid为空，标记为已处理
                faultrecordService.batchDisposeStatus(id);
                result=true;
        } catch (Exception e) {
            log.error("Found Exception:", e);
        }
        return result.toString();
    }

    @CiticValids({
            @CiticValid(value={"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","故障/预警记录ID不能为空"},fieldName = "id",index = 1),
            @CiticValid(value={"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","现象编码不能为空"},fieldName = "code",index = 1),
            @CiticValid(value={"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","现象分析不能为空"},fieldName = "analyse",index = 1),
            @CiticValid(value={"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","解决方案不能为空"},fieldName = "solution",index = 1),
            @CiticValid(value={"[12]{1}","类型必须为1或2"},fieldName = "type",index = 1)
    })
    @ApiOperation(value="更改状态及加入知识库", notes="根据knowledge加入知识库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "knowledgeRelateFaultInVO", value = "故障/预警对象", required = false, paramType = "body", dataType = "KnowledgeRelateFaultInVO")
    })
    @PostMapping(value="updateStatusAndAddKnowledge", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateStatusAndAddKnowledge(@RequestBody KnowledgeRelateFaultInVO knowledgeRelateFaultInVO,
                                              HttpServletRequest request) {
        Boolean result=false;
        log.info("Enter updateStatusAndAddKnowledge knowledgeRelateFaultInVO={}", knowledgeRelateFaultInVO);
        //获取登录用户的相关信息
        User user=getUser(request);
        try {  //如果deviceid为空，标记为已处理
            if(StringUtils.isEmpty(knowledgeRelateFaultInVO.getDeviceid())){
                faultrecordService.updateStatus(knowledgeRelateFaultInVO);
            }else{  //如果deviceid不为空，标记为已处理并加入知识库
                faultrecordService.updateStatusAndAddKnowledge(knowledgeRelateFaultInVO,user);
            }
            result=true;
        } catch (Exception e) {
            log.error("Found Exception:", e);
        }
        return result.toString();
    }

    @ApiOperation(value="获取当前未处理预警/故障的数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceid", value = "deviceid", required = false,
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "monitorPointId", value = "monitorPointId", required = false,
                    dataType = "string", paramType = "query")
    })
    @GetMapping(value="getUnsolvedFaultrecordCount", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getUnsolvedFaultrecordCount(String deviceid,String monitorPointId,HttpServletRequest request) {
        Map<String,Integer> res = new HashMap<String,Integer>();
        //获取登录用户的相关信息
        User user=getUser(request);
        Faultrecord faultrecord=new Faultrecord();
        if(StringUtils.isNotEmpty(deviceid)){
            faultrecord.setDeviceid(deviceid);
        }
        if(StringUtils.isNotEmpty(monitorPointId)){
            faultrecord.setMonitorPointId(monitorPointId);
        }
        faultrecord.setStatus("0");
        try {
            //判断该用户是哪个角色：1.超级用户、2.管理员、3.普通用户
            if(CodeUtil.isNotNull(user)){
                switch(user.getRolelevel()){
                    case "1":
                        faultrecord.setOrgCode(user.getOrgcode());
                        break;
                    case "2":
                        faultrecord.setOrgid(user.getOrgid());
                        break;
                    case "3":
                        faultrecord.setUserid(user.getUserid());
                        break;
                }
            }
            res=faultrecordService.getUnsolvedFaultrecordCount(faultrecord);
        } catch (Exception e) {
            log.error("Found Exception:", e);
        }
        return JSON.toJSONString(res);
    }

    @ApiOperation(value="获取近七日总预警/故障的数量")
    @GetMapping(value="UnsolvedFaultrecordCountLast7", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String UnsolvedFaultrecordCountLast7(HttpServletRequest request) {
        Map<String,Object> res = new HashMap<String,Object>();
        long begin = System.currentTimeMillis();
        String result = cacheService.get("UnsolvedFaultrecordCountLast7");
        if (result != null){
            log.warn("Exec UnsolvedFaultrecordCountLast7 Cache costs ={}", System.currentTimeMillis() - begin);
            return result;
        }
        //获取登录用户的相关信息
        User user=getUser(request);
        log.warn("Exec getUser costs ={}", System.currentTimeMillis() - begin);
        Faultrecord faultrecord=new Faultrecord();
        //判断该用户是哪个角色：1.超级用户、2.管理员、3.普通用户
        if(CodeUtil.isNotNull(user)){
            switch(Integer.parseInt(user.getRolelevel())){
                case 1:
                    faultrecord.setOrgCode(user.getOrgcode());
                    break;
                case 2:
                    faultrecord.setOrgid(user.getOrgid());
                    break;
                case 3:
                    faultrecord.setUserid(user.getUserid());
                    break;
            }
        }
        log.warn("Exec switch costs ={}", System.currentTimeMillis() - begin);
        res=faultrecordService.getFaultrecordCountLast7(faultrecord);
        log.warn("Exec Outer getUnsolvedFaultrecordCountLast7 costs ={}", System.currentTimeMillis() - begin);
        result = JSON.toJSONString(res).trim();
        cacheService.set("UnsolvedFaultrecordCountLast7", result, 3);
        log.warn("Exec UnsolvedFaultrecordCountLast7 costs ={}", System.currentTimeMillis() - begin);
        log.warn("Set cache UnsolvedFaultrecordCountLast7={}", result);
        return result;
    }


}
