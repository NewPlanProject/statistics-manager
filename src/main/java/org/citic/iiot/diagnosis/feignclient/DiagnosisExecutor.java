package org.citic.iiot.diagnosis.feignclient;

import org.citic.iiot.app.core.data.Result;
import org.citic.iiot.diagnosis.domain.MonitorPoint;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * User: Mr.zheng
 * Date: 2017/9/19
 * Time: 19:32
 */
@FeignClient("diagnosis-executor")
public interface DiagnosisExecutor {

    //调用规则启动接口
    @RequestMapping(value="/rule/ruleStart", method= RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public Result<Boolean> ruleStart(@RequestBody MonitorPoint monitorPoint);

    //调用规则停止接口
    @RequestMapping(value="/rule/ruleStop", method= RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public Result<Boolean> ruleStop(@RequestParam("pointid") String pointid);
}
