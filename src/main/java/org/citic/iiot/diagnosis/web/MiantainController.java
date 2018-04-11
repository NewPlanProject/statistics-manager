package org.citic.iiot.diagnosis.web;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.citic.iiot.app.core.data.Result;
import org.citic.iiot.app.core.data.ResultCode;
import org.citic.iiot.app.core.session.SessionInfo;
import org.citic.iiot.app.core.session.User;
import org.citic.iiot.app.core.util.CodeUtil;
import org.citic.iiot.app.core.validator.CiticValid;
import org.citic.iiot.app.core.validator.CiticValids;
import org.citic.iiot.diagnosis.domain.Maintain;
import org.citic.iiot.diagnosis.service.CacheService;
import org.citic.iiot.diagnosis.service.MaintainService;
import org.citic.iiot.diagnosis.util.ExcelUtils;
import org.citic.iiot.diagnosis.vo.MaintainByIdOutVO;
import org.citic.iiot.diagnosis.vo.MaintainInVO;
import org.citic.iiot.diagnosis.vo.MaintainSubOutVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxw on 2017/7/17.
 */
@Slf4j
@Controller
@RequestMapping("maintain")
public class MiantainController implements SessionInfo{

    @Autowired
    private MaintainService maintainService;

    @Autowired
    private CacheService cacheService;

    @CiticValids({
            @CiticValid(value={"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","标题不能为空"},fieldName = "title",index = 1),
            @CiticValid(value={"[12]{1}","类型必须为1或2"},fieldName = "type",index = 1),
            @CiticValid(value={"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","设备uuid不能为空"},fieldName = "deviceid",index = 1),
            @CiticValid(value={"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","设备名称不能为空"},fieldName = "devicename",index = 1),
            @CiticValid(value={"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","维修/维护详情不能为空"},fieldName = "detail",index = 1)
    })
    @ApiOperation(value="维修/维护保存", notes="根据maintain保存数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maintain", value = "信息对象", required = true, dataType = "Maintain",paramType = "body")
    })
    @PostMapping(value="save", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String save(@RequestBody Maintain maintain,HttpServletRequest request) {
        Boolean result=false;
        //获取登录用户的相关信息
        User user=getUser(request);
        try {
            if(CodeUtil.isNotNull(user)){
                maintain.setUserid(user.getUserid());
                maintain.setOrgid(user.getOrgid());
                maintain.setOrgCode(user.getOrgcode());
            }
            maintainService.save(maintain);
            result=true;
        } catch (Exception e) {
            log.error("Found Exception", e);
        }
        return result.toString();
    }


    @ApiOperation(value = "维修/维护记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maintainInVO", value = "维修/维护对象", required = true, paramType = "body", dataType = "MaintainInVO"),
    })
    @PostMapping(value = "list", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String list(@RequestBody MaintainInVO maintainInVO,HttpServletRequest request){
        log.info("Enter maintain list maintainInVO={}", maintainInVO);
        Result<Map<String,Object>> resBean = new Result<Map<String,Object>>(ResultCode.SUCCESS,"查询成功",null);
        //获取登录用户的相关信息
        User user=getUser(request);
        try {
            //判断该用户是哪个角色：1.超级用户、2.管理员、3.普通用户
            if(CodeUtil.isNotNull(user)){
                switch(user.getRolelevel()){
                    case "1":
                        maintainInVO.setOrgCode(user.getOrgcode());
                        break;
                    case "2":
                        maintainInVO.setOrgid(user.getOrgid());
                        break;
                    case "3":
                        maintainInVO.setUserid(user.getUserid());
                        break;
                }
            }
            resBean = maintainService.getMaintainList(maintainInVO);
        }catch (Exception e){
            log.error("maintain list failed",e);
        }
        log.info("maintain list={}" , resBean);
        return JSON.toJSONString(resBean);
    }

    @CiticValids({
            @CiticValid(value={"^\\S+$","维修/维护记录id不能为空"},fieldName = "ids",index = 1)
    })
    @ApiOperation(value="维修/维护记录批量删除", notes="根据ids删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "ids", required = true, dataType = "string", paramType = "query")
    })
    @DeleteMapping(value="del", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String del(@RequestParam(value = "ids") String ids) {
        Boolean result=false;
        log.info("Enter del ids={}", ids);
        try {
            String[] split = ids.split(",");
            maintainService.del(split);
            result=true;
        } catch (Exception e) {
            log.error("Found Exception:", e);
        }
        return result.toString();
    }

    @CiticValids({
            @CiticValid(value={"^\\S+$","维修/维护记录id不能为空"},fieldName = "id",index = 1)
    })
    @ApiOperation(value="根据ID查询维修/维护记录", notes="根据id查询数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value="getMaintainById", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getMaintainById(@RequestParam(value = "id") String id) {
        log.info("Enter getMaintainById id={}", id);
        Maintain maintain = null;
        MaintainByIdOutVO maintainByIdOutVO = new MaintainByIdOutVO();
        try {
            maintain = maintainService.getMaintainById(id);
            BeanUtils.copyProperties(maintain, maintainByIdOutVO);
        } catch (Exception e) {
            log.error("Found Exception:", e);
        }
        return JSON.toJSONString(maintainByIdOutVO);
    }


    @CiticValids({
            @CiticValid(value={"^\\S+$","维修/维护记录id不能为空"},fieldName = "ids",index = 1)
    })
    @ApiOperation(value="导出Excel", notes="根据ids读取数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "ids", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value="exportExcel")
    public String exportExcel(@RequestParam(value = "ids") String ids, HttpServletResponse response, HttpServletRequest request){
        List<MaintainSubOutVO> maintainList= maintainService.getMaintainByIdExport(ids.split(","));
        try {
            InputStream excelStream = ExcelUtils.getExcelContent(maintainList);
            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + new String("维修维护记录.xls".getBytes("gbk"), "iso-8859-1") + "\"");
            ServletOutputStream out = response.getOutputStream();
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(excelStream);
                bos = new BufferedOutputStream(out);
                byte[] buff = new byte[2048];
                int bytesRead;
                while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                    bos.write(buff, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            }
        } catch (IOException e) {
            log.error("Found Exception:", e);
        }
        return "";
    }


    @ApiOperation(value="获取维修/维护的设备数量")
    @GetMapping(value="maintainCount", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String maintainCount(HttpServletRequest request) {
        Map<String,Integer> res = new HashMap<String,Integer>();
        Maintain maintain=new Maintain();
        //获取登录用户的相关信息
        User user=getUser(request);
        try {
            //判断该用户是哪个角色：1.超级用户、2.管理员、3.普通用户
            if(CodeUtil.isNotNull(user)){
                switch(user.getRolelevel()){
                    case "1":
                        maintain.setOrgCode(user.getOrgcode());
                        break;
                    case "2":
                        maintain.setOrgid(user.getOrgid());
                        break;
                    case "3":
                        maintain.setUserid(user.getUserid());
                        break;
                }
            }
            res=maintainService.getMaintainCount(maintain);
        } catch (Exception e) {
            log.error("Found Exception:", e);
        }
        return JSON.toJSONString(res);
    }

    @ApiOperation(value="获取近三月维修/维护的设备数量")
    @GetMapping(value="UnsolvedFaultrecordCountLast7", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String maintainCountLast3Month(HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        String result = cacheService.get("maintainCountLast3Month");
        if (result != null){
            log.warn("Exec maintainCountLast3Month from Cache costs ={}", System.currentTimeMillis() - begin);
            return result;
        }
        Map<String,Object> res = new HashMap<String,Object>();
        Maintain maintain=new Maintain();
        //获取登录用户的相关信息
        User user=getUser(request);
        try {
            //判断该用户是哪个角色：1.超级用户、2.管理员、3.普通用户
            if(CodeUtil.isNotNull(user)){
                switch(user.getRolelevel()){
                    case "1":
                        maintain.setOrgCode(user.getOrgcode());
                        break;
                    case "2":
                        maintain.setOrgid(user.getOrgid());
                        break;
                    case "3":
                        maintain.setUserid(user.getUserid());
                        break;
                }
            }
            res=maintainService.findMaintainCountLast3Month(maintain);
        } catch (Exception e) {
            log.error("Found Exception:", e);
        }
        log.warn("Exec Outer maintainCountLast3Month costs ={}", System.currentTimeMillis() - begin);
        result = JSON.toJSONString(res).trim();
        cacheService.set("maintainCountLast3Month", result , 3);
        log.warn("Set cache maintainCountLast3Month={}", result);
        return result;
    }

}
