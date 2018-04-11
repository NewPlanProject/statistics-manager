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
import org.citic.iiot.app.core.util.UUIDUtil;
import org.citic.iiot.app.core.validator.CiticValid;
import org.citic.iiot.app.core.validator.CiticValids;
import org.citic.iiot.diagnosis.domain.Rule;
import org.citic.iiot.diagnosis.service.RuleService;
import org.citic.iiot.diagnosis.util.FileUtil;
import org.citic.iiot.diagnosis.vo.RuleInVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Mr.zheng
 * Date: 2017/8/1
 * Time: 14:00
 */
@Slf4j
@Controller
@RequestMapping("/rule")
public class RuleController implements SessionInfo{

    /** 型号（监控判断是否是单个设备） */
    private static final String MODEL = "model";
    /** 设备关联（监控判断是否是关联设备） */
    private static final String RELATION = "relation";

    @Autowired
    private RuleService ruleService;

    @ApiOperation(value = "规则列表接口", notes = "ResultCode.SUCCESS为成功；非零为错误")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleInVO", value = "规则vo", required = true, dataType = "RuleInVO", paramType = "Body")
    })
    @PostMapping(value = "/list",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result<Map<String, Object>> ruleList(@RequestBody RuleInVO ruleInVO,HttpServletRequest request) {
        Result<Map<String, Object>> resBean = new Result(ResultCode.ERROR_SERVICE,"查询失败",null);
        log.info("Enter ruleList ruleInVO={}",ruleInVO);
        if(CodeUtil.isNull(ruleInVO)){
            resBean.setMsg("参数不可为空");
            return resBean;
        }
        Rule rule = new Rule();
        BeanUtils.copyProperties(ruleInVO,rule);

        //获取并设置用户信息
        User user = getUser(request);
        if(CodeUtil.isNotNull(user)){
            switch(user.getRolelevel()){
                case "1":
                    rule.setOrgcode(user.getOrgcode());
                    break;
                case "2":
                    rule.setOrgid(user.getOrgid());
                    break;
                case "3":
                    rule.setUserid(user.getUserid());
                    break;
            }
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Rule> rules = ruleService.ruleList(rule);
        int totalSize = ruleService.ruleCount(rule);
        resultMap.put("rows",rules);
        resultMap.put("totalSize",totalSize);
        resBean.setCode(ResultCode.SUCCESS);
        resBean.setMsg("查询成功");
        resBean.setContent(resultMap);
        log.info("ruleList()->" + resBean);
        return resBean;
    }

//    @ApiOperation(value = "保存规则", notes = "0保存成功 其他保存失败")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "rule", value = "保存规则", required = true, dataType = "Rule", paramType = "body")
//    })
//    @PostMapping(value = "/save",produces = "application/json;charset=UTF-8")
//    @ResponseBody
//    @CiticValids({
//            @CiticValid(fieldName = "rulename",value = {"[\\u4e00-\\u9fa5_a-zA-Z0-9]+","规则名称不能为空"},index = 1),
//            @CiticValid(fieldName = "type",value = {"[12]{1}","类型参数为1或2"}),
//            @CiticValid(fieldName = "salience",value = {"(\\d{1})|([1][0])","等级为0-10"}),
//            @CiticValid(fieldName = "code",value = {"(([T][S][J])|([L][M])|([J][Q][R])|([M][J]))(([Y][J])|([G][Z]))[0-9]{2,6}","code参数异常"}),
//            @CiticValid(fieldName = "ruleConfig",value = {"^\\S+$","规则不能为空",}),
//            @CiticValid(fieldName = "drlname",value = {"^\\S+$","文件名称不能为空"}),
//            @CiticValid(fieldName = "monitorPointid",value = {"^\\S+$","监控点id不能为空"}),
//            @CiticValid(fieldName = "runrate",value = {"[1-9][0-9]{3,}","刷新间隔不能为空"})
//    })
//    public String createRule(@RequestBody Rule rule){
//        Result<Boolean> resBean = new Result(ResultCode.ERROR_SERVICE,"保存失败",null);
//        log.info("Enter createRule rule={}",rule);
//        rule.setDrlname(UUIDUtil.creatUUID());
//        //验证code 是否重复
//        Rule codeRule = ruleService.ruleDetailByCode(rule.getCode());
//        if(CodeUtil.isNotNull(codeRule)){
//            resBean.setMsg("code重复");
//            return BaseJSON.toJSONString(resBean);
//        }
//
//        //验证同一文件中规则名称 是否重复
//        Rule drlnameAndRulenameRule = new Rule();
//        drlnameAndRulenameRule.setDrlname(rule.getDrlname());
//        drlnameAndRulenameRule.setRulename(rule.getRulename());
//        Rule drlnameAndRulenameRuleDB = ruleService.ruleDetailByRule(drlnameAndRulenameRule);
//        if(CodeUtil.isNotNull(drlnameAndRulenameRuleDB)){
//            resBean.setMsg("规则名称重复");
//            return BaseJSON.toJSONString(resBean);
//        }
//        //生成规则
//        boolean result = false;
//        try {
//            //将规则转换为list
//            List<Map> list = (List<Map>) JSON.parse(rule.getRuleConfig());
//            //生成追加规则字符串
//            if(CodeUtil.isNotNullZero(list)){
//                String drlAppendBodyString = FileUtil.makeFileBody(rule,list);
//                rule.setRulecontent(drlAppendBodyString);
//                rule.setRuleid(UUIDUtil.creatUUID());
//                rule.setCreatetime(new Date());
//                rule.setUpdatetime(new Date());
//                result = ruleService.createRule(rule);
//                if(result){
//                    resBean.setCode(ResultCode.SUCCESS);
//                    resBean.setMsg("保存成功");
//                    resBean.setContent(result);
//                }
//            }
//        } catch (Exception e) {
//            log.error("createRule failed",e.getMessage());
//        }
//        log.info("Leave createRule resBean={}", resBean);
//        return BaseJSON.toJSONString(resBean);
//    }

    @ApiOperation(value = "保存规则", notes = "0保存成功 其他保存失败")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rule", value = "保存规则", required = true, dataType = "Rule", paramType = "body")
    })
    @PostMapping(value = "/save",produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "rulename",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","规则名称不能为空"},index = 1),
            @CiticValid(fieldName = "type",value = {"[12]{1}","类型参数为1或2"}),
            @CiticValid(fieldName = "code",value = {"[a-zA-Z0-9]{3}","现象代码为3位数字或英文"}),
            @CiticValid(fieldName = "ruleConfig",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","规则不能为空",}),
            @CiticValid(fieldName = "disp",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","描述不能为空"}),
            @CiticValid(fieldName = "monitorPointid",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","监控点id不能为空"})
    })
    public String createRule(@RequestBody Rule rule, HttpServletRequest request){

        log.info("RuleController_createRule_start={}", rule);

        Result<Boolean> resBean = new Result(ResultCode.ERROR_SERVICE,"保存失败",null);

        //生成自定义的规则CODE
        generateCode(rule);

        //验证code 是否重复
        Rule codeParamRule = new Rule();
        codeParamRule.setCode(rule.getCode());
        Rule codeRule = ruleService.ruleDetail(codeParamRule);
        if(CodeUtil.isNotNull(codeRule)){
            resBean.setMsg("规则代码重复");
            return BaseJSON.toJSONString(resBean);
        }

        //生成规则
        boolean result = false;
        try {
            //获取并设置用户信息
            User user = getUser(request);
            if(CodeUtil.isNotNull(user)){
                rule.setUserid(user.getUserid());
                rule.setOrgid(user.getOrgid());
                rule.setOrgcode(user.getOrgcode());
            }

            //将规则转换为list
//            List<Map> list = (List<Map>) JSON.parse(rule.getRuleConfig());
            //生成追加规则字符串
//            if(CodeUtil.isNotNullZero(list)){
            //rule.setSalience(salience);

            switch (rule.getType()) {
                case "1"://预警
                    rule.setSalience(10);
                    break;
                case "2"://故障
                    rule.setSalience(100);
                    break;
            }

            String drlAppendBodyString = FileUtil.makeFileBody(rule);
            rule.setRulecontent(drlAppendBodyString);
            rule.setRuleid(UUIDUtil.creatUUID());
            rule.setCreatetime(new Date());
            rule.setUpdatetime(new Date());
            rule.setRunrate(1000*5);

            result = ruleService.createRule(rule);
            if(result){
                resBean.setCode(ResultCode.SUCCESS);
                resBean.setMsg("保存成功");
                resBean.setContent(result);
            }
//            }
        } catch (Exception e) {
            log.error("createRule failed", e);
        }

        log.info("RuleController_createRule_end={}", resBean);

        return BaseJSON.toJSONString(resBean);
    }

    /**
     * 生成规则CODE(是否设备关联：设备种类：预警或者故障：日期时间：code)
     * @param rule
     * @return
     */
    private void generateCode(Rule rule) {

        StringBuilder code = new StringBuilder();
        code.append(CodeUtil.isNotNullEmpty(rule.getCategorycode()) ? rule.getCategorycode() : "XX");
        code.append(CodeUtil.isNotNullEmpty(rule.getModelcode()) ? rule.getModelcode() : "XX");
        code.append(CodeUtil.isNotNullEmpty(rule.getPartscode()) ? rule.getPartscode() : "XX");
        /**
        //第一段
        switch (rule.getFlag()) {
            case MODEL://单一设备
                code.append("01");

                //第二段
                String dictname = rule.getParentdictname();
                if(CodeUtil.isNotNullEmpty(dictname)){
                    if(dictname.contains("磨机")){
                        code.append("0001");
                    }else if(dictname.contains("立磨")){
                        code.append("0002");
                    }else if(dictname.contains("机器人")){
                        code.append("0003");
                    }else if(dictname.contains("提升机")){
                        code.append("0004");
                    }
                }

                break;
            case RELATION://设备关联
                code.append("02");

                //第二段
                code.append("0000");

                break;
        }
*/
        //第三段
        switch (rule.getType()) {
            case "1":
                code.append("Q");
                break;
            case "2":
                code.append("Z");
                break;
        }

        //第四段
//        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYYMMDDHHMMSS);
//        code.append(sdf.format(new Date()));

        //第五段
        code.append(rule.getCode());

        rule.setCode(code.toString());
    }

    @ApiOperation(value = "规则列表接口-知识库弹出框使用", notes = "ResultCode.SUCCESS为成功；非零为错误")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleInVO", value = "规则vo", required = true, dataType = "RuleInVO", paramType = "Body")
    })
    @PostMapping(value = "/knowledgeRuleList",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result<Map<String, Object>> knowledgeRuleList(@RequestBody RuleInVO ruleInVO,HttpServletRequest request) {
        Result<Map<String, Object>> resBean = new Result(ResultCode.ERROR_SERVICE,"查询失败",null);
        log.info("Enter ruleList ruleInVO={}",ruleInVO);
        if(CodeUtil.isNull(ruleInVO)){
            resBean.setMsg("参数不可为空");
            return resBean;
        }
        Rule rule = new Rule();
        BeanUtils.copyProperties(ruleInVO,rule);
        //获取并设置用户信息
        User user = getUser(request);
        if(CodeUtil.isNotNull(user)){
            switch(user.getRolelevel()){
                case "1":
                    rule.setOrgcode(user.getOrgcode());
                    break;
                case "2":
                    rule.setOrgid(user.getOrgid());
                    break;
                case "3":
                    rule.setUserid(user.getUserid());
                    break;
            }
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Rule> rules = ruleService.knowledgeRuleList(rule);
        int totalSize = ruleService.knowLedgeRuleCount(rule);
        resultMap.put("rows",rules);
        resultMap.put("totalSize",totalSize);
        resBean.setCode(ResultCode.SUCCESS);
        resBean.setMsg("查询成功");
        resBean.setContent(resultMap);
        log.info("ruleList()->" + resBean);
        return resBean;
    }

    @ApiOperation(value = "获取规则详细", notes = "0获取成功 其他获取失败")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleid", value = "规则id", required = false, dataType = "int", paramType = "path")
    })
    @GetMapping(value = "/view/{ruleid}",produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "ruleid",value = {"[a-z0-9A-Z]+","规则id不能为空"},index = 1)
    })
    public String ruleDetail(@PathVariable String ruleid){
        Result<Rule> resBean = new Result<Rule>(ResultCode.ERROR_DATA,"查询失败",null);
        log.info("Enter ruleDetail ruleid={}",ruleid);
        Rule ruleParam = new Rule();
        ruleParam.setRuleid(ruleid);
        Rule rule = this.ruleService.ruleDetail(ruleParam);
        resBean.setCode(ResultCode.SUCCESS);
        resBean.setMsg("查询成功");
        resBean.setContent(rule);
        log.info("Leave ruleDetail resBean={}", resBean);
        return BaseJSON.toJSONString(resBean, SerializerFeature.WriteMapNullValue);
    }

    @ApiOperation(value = "修改规则", notes = "0修改成功 其他修改失败")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rule", value = "保存规则", required = true, dataType = "Rule", paramType = "body"),
            @ApiImplicitParam(name = "ruleid", value = "规则id", required = true, dataType = "int", paramType = "path")
    })
    @PutMapping(value = "/update/{ruleid}",produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "ruleid",value = {"[a-z0-9A-Z]+","规则id不能为空"},index = 2),
            @CiticValid(fieldName = "monitorPointid",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","监控点id不能为空"})
    })
    public String updateRule(@RequestBody Rule rule,@PathVariable String ruleid,HttpServletRequest request){
        Result<Boolean> resBean = new Result(ResultCode.ERROR_SERVICE,"修改失败",false);
        log.info("Enter updateRule rule={}",rule);
        boolean result = false;
        try {
            //获取并设置用户信息
            if(CodeUtil.isNotNullEmpty(rule.getRuleConfig())){
                //将规则转换为list
//                List<Map> list = (List<Map>) JSON.parse(rule.getRuleConfig());
                //生成追加规则字符串
//                if(CodeUtil.isNotNullZero(list)) {
//                    String drlAppendBodyString = FileUtil.makeFileBody(rule, list);
                String drlAppendBodyString = FileUtil.makeFileBody(rule);
                rule.setRulecontent(drlAppendBodyString);
            }
//            }
            rule.setRuleid(ruleid);
            result = ruleService.updateRule(rule,getUser(request));
            if(result){
                resBean.setCode(ResultCode.SUCCESS);
                resBean.setMsg("修改成功");
                resBean.setContent(result);
            }
        } catch (Exception e) {
            log.error("updateRule failed",e);
        }
        log.info("Leave updateRule resBean={}", resBean);
        return BaseJSON.toJSONString(resBean);
    }

    @ApiOperation(value = "删除规则", notes = "0删除成功 其他删除失败")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rule", value = "保存规则", required = true, dataType = "Rule", paramType = "body"),
            @ApiImplicitParam(name = "ruleid", value = "规则id", required = true, dataType = "int", paramType = "path")
    })
    @DeleteMapping(value = "/del/{ruleid}",produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "code",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","现象代码不能为空"}),
            @CiticValid(fieldName = "ruleid",value = {"[a-z0-9A-Z]+","规则id不能为空"},index = 2),
            @CiticValid(fieldName = "monitorPointid",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","监控点id不能为空"})
    })
    public String deleteRule(@RequestBody Rule rule,@PathVariable String ruleid,HttpServletRequest request){
        Result<Boolean> resBean = new Result(ResultCode.ERROR_SERVICE,"删除失败",null);
        log.info("Enter deleteRule rule={}",rule);
        boolean result = true;
        try {
            rule.setRuleid(ruleid);
            result = ruleService.deleteRule(rule);
            if(result){
                resBean.setCode(ResultCode.SUCCESS);
                resBean.setMsg("删除成功");
                resBean.setContent(result);
            }
        } catch (Exception e) {
            log.error("deleteRule failed",e);
        }
        log.info("Leave deleteRule resBean={}", resBean);
        return BaseJSON.toJSONString(resBean);
    }

    @ApiOperation(value="规则库批量删除", notes="根据集合元素删除数据")
    @DeleteMapping(value="/dels", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @CiticValids({
            @CiticValid(fieldName = "code",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","现象代码不能为空"}),
            @CiticValid(fieldName = "ruleid",value = {"[a-z0-9A-Z]+","规则id不能为空"}),
            @CiticValid(fieldName = "monitorPointid",value = {"(^[\\S+][\\s\\S]*[\\S+]$)|(^[\\S+]$)","监控点id不能为空"})
    })
    public String dels(@RequestBody List<Rule> list) {
        Result<Boolean> resBean = new Result<Boolean>(ResultCode.ERROR_SERVICE,"删除失败",false);
        log.info("Enter dels list={}", list);
        for(Rule rule : list){
            ruleService.deleteRule(rule);
        }
        resBean.setCode(ResultCode.SUCCESS);
        resBean.setContent(true);
        resBean.setMsg("删除成功");
        log.info("Leave dels resBean={}", resBean);
        return BaseJSON.toJSONString(resBean);
    }
}
