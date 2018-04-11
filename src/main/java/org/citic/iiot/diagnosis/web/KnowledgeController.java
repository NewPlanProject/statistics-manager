package org.citic.iiot.diagnosis.web;

import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.citic.iiot.app.core.data.Result;
import org.citic.iiot.app.core.data.ResultCode;
import org.citic.iiot.app.core.json.BaseJSON;
import org.citic.iiot.app.core.session.SessionInfo;
import org.citic.iiot.app.core.session.User;
import org.citic.iiot.app.core.util.CodeUtil;
import org.citic.iiot.app.core.validator.CiticValid;
import org.citic.iiot.app.core.validator.CiticValids;
import org.citic.iiot.diagnosis.domain.Knowledge;
import org.citic.iiot.diagnosis.service.KnowledgeService;
import org.citic.iiot.diagnosis.vo.KnowledgeInVO;
import org.citic.iiot.diagnosis.vo.KnowledgeOutVO;
import org.citic.iiot.diagnosis.vo.KnowledgeSubOutVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yxw on 2017/7/17.
 */
@Slf4j
@Controller
@RequestMapping("/knowledge")
public class KnowledgeController implements SessionInfo{

    @Autowired
    private KnowledgeService knowledgeService;

    @ApiOperation(value = "知识库列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "knowledgeInVO", value = "维修/维护对象", required = true, paramType = "body", dataType = "KnowledgeInVO")
    })
    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result<Map<String,Object>> list(@RequestBody KnowledgeInVO knowledgeInVO,HttpServletRequest request){
        log.info("Enter list knowledgeInVO={}", knowledgeInVO);
        Result<Map<String,Object>> resBean = new Result<Map<String,Object>>(ResultCode.SUCCESS,"查询成功",null);
        List<KnowledgeOutVO> knowledgeOutVOS = new LinkedList<KnowledgeOutVO>();
        try {
            //获取并设置用户信息
            User user = getUser(request);
            if(CodeUtil.isNotNull(user)){
                switch(user.getRolelevel()){
                    case "1":
                        knowledgeInVO.setOrgcode(user.getOrgcode());
                        break;
                    case "2":
                        knowledgeInVO.setOrgid(user.getOrgid());
                        break;
                    case "3":
                        knowledgeInVO.setUserid(user.getUserid());
                        break;
                }
            }
            List<Knowledge> knowledgeList = knowledgeService.getKnowledgeList(knowledgeInVO);
            int selectCount = knowledgeService.selectCount(knowledgeInVO);
            //vo 转换
            for (Knowledge knowledge : knowledgeList) {
                KnowledgeOutVO knowledgeOutVO = new KnowledgeOutVO();
                BeanUtils.copyProperties(knowledge, knowledgeOutVO);
                knowledgeOutVOS.add(knowledgeOutVO);
            }
            Map<String,Object> resultMap = new HashMap<String,Object>();
            resultMap.put("rows",knowledgeOutVOS);
            resultMap.put("totalSize",selectCount);
            resBean.setContent(resultMap);
        } catch (Exception e) {
            log.error("Found Exception", e);
            resBean.setCode(ResultCode.ERROR_SERVICE);
            resBean.setMsg("查询失败");
        }
        log.info("Get knowledgeOutVO list size={}", knowledgeOutVOS != null ? knowledgeOutVOS.size() : 0);
        return resBean;
    }

    @ApiOperation(value="知识库保存", notes="根据knowledge保存数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "knowledge", value = "知识库对象", required = true, dataType = "Knowledge",paramType = "body")
    })
    @PostMapping(value="/save", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "type",value = {"[12]{1}","类型参数位1或2"}),
            @CiticValid(fieldName = "code",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","现象代码不能为空"}),
            @CiticValid(fieldName = "msgJson",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","现象分析与解决方案不能为空",}),
            @CiticValid(fieldName = "knowname",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","现象名称不能为空"})
    })
    public String save(@RequestBody Knowledge knowledge, HttpServletRequest request) {
        log.info("Enter save knowledge={}", knowledge);
        Result<Boolean> resBean = new Result<Boolean>(ResultCode.ERROR_SERVICE,"保存失败",false);
        try {
            Knowledge knowledgeCodeParam = new Knowledge();
            knowledgeCodeParam.setCode(knowledge.getCode());
            knowledgeCodeParam.setParentid("father");
            Knowledge knowledgeCodeDB = this.knowledgeService.detail(knowledgeCodeParam);
            if(CodeUtil.isNotNull(knowledgeCodeDB)){
                resBean.setMsg("知识条目重复");
                return BaseJSON.toJSONString(resBean);
            }

            Knowledge knowledgeNameParam = new Knowledge();
            knowledgeNameParam.setKnowname(knowledge.getKnowname());
            knowledgeNameParam.setParentid("father");
            Knowledge knowledgeNameDB = this.knowledgeService.detail(knowledgeNameParam);
            if(CodeUtil.isNotNull(knowledgeNameDB)){
                resBean.setMsg("现象名称重复");
                return BaseJSON.toJSONString(resBean);
            }

            //获取并设置用户信息
            User user = getUser(request);
            if(CodeUtil.isNotNull(user)){
                knowledge.setUserid(user.getUserid());
                knowledge.setOrgid(user.getOrgid());
                knowledge.setOrgcode(user.getOrgcode());
            }

            //知识库添加
            knowledgeService.saveKnowledge(knowledge);
            resBean.setContent(true);
            resBean.setMsg("保存成功");
            resBean.setCode(ResultCode.SUCCESS);
        } catch (Exception e) {
            log.error("Found Exception", e);
        }
        log.info("leave save resBean={}", resBean);
        return BaseJSON.toJSONString(resBean);
    }

    @ApiOperation(value="知识库修改", notes="根据knowledge修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "knowid", value = "知识库id", required = true, dataType = "string",paramType = "path"),
            @ApiImplicitParam(name = "knowledge", value = "知识库对象", required = true, dataType = "Knowledge",paramType = "body")
    })
    @PutMapping(value="/update/{knowid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "knowid",value = {"[a-zA-Z0-9]+","知识库id不能为空"})
    })
    public String update(@PathVariable String knowid, @RequestBody Knowledge knowledge) {
        Result<Boolean> resBean = new Result<Boolean>(ResultCode.ERROR_SERVICE,"修改失败",false);
        log.info("Enter update knowledge={}", knowledge);
        try {
            if(CodeUtil.isNotNullEmpty(knowledge.getKnowname())){
                Knowledge knowledgeParam = new Knowledge();
                knowledgeParam.setKnowname(knowledge.getKnowname());
                knowledgeParam.setParentid("father");
                Knowledge knowledgeDB = this.knowledgeService.detail(knowledgeParam);
                if(CodeUtil.isNotNull(knowledgeDB) && !knowid.equals(knowledgeDB.getKnowid())){
                    resBean.setMsg("现象名称重复");
                    return BaseJSON.toJSONString(resBean);
                }
            }
            knowledge.setKnowid(knowid);
            //知识库修改
            knowledgeService.updateKnowledge(knowledge);
            resBean.setContent(true);
            resBean.setMsg("修改成功");
            resBean.setCode(ResultCode.SUCCESS);
        } catch (Exception e) {
            log.error("Found Exception", e);
        }
        log.info("leave update resBean={}", resBean);
        return BaseJSON.toJSONString(resBean);
    }

    @ApiOperation(value="获取knowid查询知识库信息", notes="根据knowid查询对应knowledges")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "knowid", value = "知识库id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value="/knowledgesByKnowId/{knowid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "knowid",value = {"[a-zA-Z0-9]+","知识库id不能为空"})
    })
    public String knowledgesByKnowId(@PathVariable String knowid) {
        log.info("Enter knowledgesByKnowId knowid={}", knowid);
        List<Knowledge> knowledges = knowledgeService.knowledgesByKnowId(knowid);
        List<KnowledgeSubOutVO> knowledgeSubOutVOs = new LinkedList<KnowledgeSubOutVO>();
        for(Knowledge knowledge : knowledges){
            KnowledgeSubOutVO knowledgeSubOutVO = new KnowledgeSubOutVO();
            BeanUtils.copyProperties(knowledge,knowledgeSubOutVO);
            knowledgeSubOutVOs.add(knowledgeSubOutVO);
        }
        log.info("Get knowledge list size={}", knowledges != null ? knowledges.size() : 0);
        return BaseJSON.toJSONString(knowledgeSubOutVOs, SerializerFeature.WriteMapNullValue);
    }

    @ApiOperation(value = "获取知识库详细", notes = "0获取成功 其他获取失败")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "knowid", value = "知识id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/view/{knowid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "knowid",value = {"[a-zA-Z0-9]+","知识库id不能为空"})
    })
    public String detail(@PathVariable String knowid){
        Result<KnowledgeOutVO> resBean = new Result<KnowledgeOutVO>(ResultCode.ERROR_DATA,"查询失败",null);
        log.info("Enter detail ruleid={}",knowid);
        Knowledge knowledgeParam = new Knowledge();
        knowledgeParam.setKnowid(knowid);
        knowledgeParam.setParentid("father");
        Knowledge knowledge = this.knowledgeService.detail(knowledgeParam);
        KnowledgeOutVO knowledgeOutVO = new KnowledgeOutVO();
        if(CodeUtil.isNotNull(knowledge)){
            BeanUtils.copyProperties(knowledge,knowledgeOutVO);
            resBean.setContent(knowledgeOutVO);
        }
        resBean.setCode(ResultCode.SUCCESS);
        resBean.setMsg("查询成功");
        log.info("Leave ruleDetail resBean={}", resBean);
        return BaseJSON.toJSONString(resBean, SerializerFeature.WriteMapNullValue);
    }

    @ApiOperation(value="知识库批量删除", notes="根据codes删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "codes", value = "codes", required = true, dataType = "string", paramType = "query")
    })
    @DeleteMapping(value="/dels", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "codes",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","知识库codes不能为空"})
    })
    public String dels(@RequestParam(value = "codes") String codes) {
        Result<Boolean> resBean = new Result<Boolean>(ResultCode.ERROR_SERVICE,"删除失败",false);
        log.info("Enter dels codes={}", codes);
        Boolean result = false;
        try {
            result = knowledgeService.dels(codes);
            resBean.setCode(ResultCode.SUCCESS);
            resBean.setContent(result);
            resBean.setMsg("删除成功");
        } catch (Exception e) {
            log.error("Found Exception:", e);
        }
        log.info("Leave dels resBean={}", resBean);
        return BaseJSON.toJSONString(resBean);
    }

    @ApiOperation(value="知识库删除", notes="根据knowid删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "knowid", value = "知识库id", required = true, dataType = "string", paramType = "path")
    })
    @DeleteMapping(value="/del/{knowid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "knowid",value = {"[a-zA-Z0-9]+","知识库id不能为空"})
    })
    public String del(@PathVariable String knowid) {
        Result<Boolean> resBean = new Result<Boolean>(ResultCode.ERROR_SERVICE,"删除失败",false);
        log.info("Enter del knowid={}", knowid);
        Boolean result = false;
        try {
            result = knowledgeService.del(knowid);
            resBean.setCode(ResultCode.SUCCESS);
            resBean.setContent(result);
            resBean.setMsg("删除成功");
        } catch (Exception e) {
            log.error("Found Exception:", e);
        }
        log.info("Leave del resBean={}", resBean);
        return BaseJSON.toJSONString(resBean);
    }
}
