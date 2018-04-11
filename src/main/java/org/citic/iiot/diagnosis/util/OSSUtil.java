package org.citic.iiot.diagnosis.util;

import com.aliyun.oss.OSSClient;
import lombok.extern.slf4j.Slf4j;
import org.citic.iiot.app.core.data.Result;
import org.citic.iiot.app.core.data.ResultCode;
import org.citic.iiot.diagnosis.dao.MonitorPointDao;
import org.citic.iiot.diagnosis.domain.MonitorPoint;
import org.citic.iiot.diagnosis.domain.Rule;
import org.citic.iiot.diagnosis.feignclient.DiagnosisExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;

/**
 * User: Mr.zheng
 * Date: 2017/8/23
 * Time: 11:10
 */
@Component
@Slf4j
public class OSSUtil {

    @Value("${dpm.endpoint}")
    private String endpoint;

    @Value("${dpm.accessKeyId}")
    private String accessKeyId;

    @Value("${dpm.accessKeySecret}")
    private String accessKeySecret;

    @Value("${dpm.bucketName}")
    private String bucketName;

    @Value("${dpm.img.prdfix}")
    private String prdfix;

//    @Value("${diagnosis_executor_ruleStartUrl}")
//    private String ruleStartUrl;

//    @Autowired
//    private RestTemplate restTemplate;

    @Autowired
    private DiagnosisExecutor diagnosisExecutor;

    @Autowired
    private MonitorPointDao monitorPointDao;

//    @Autowired
//    private RuleEngine ruleEngine;


    /**
     * 上传文件到阿里云服务器
     * @param content
     * @param ossFilePath
     * @param fileName
     * @return
     */
    @Async
    public Result<String> OSSUploadUtil(String content, String ossFilePath, String fileName,Rule rule){
        Result<String> result = new Result<String>(ResultCode.SUCCESS,"上传成功",null);
        String appDrl = "appDrl";
        ossFilePath = ossFilePath ==null?"":ossFilePath;
        //创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(bucketName, appDrl+"/"+ossFilePath+"/"+fileName, new ByteArrayInputStream(content.getBytes()));
            Date expiration = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 4);//设置URL过期时间为4年  3600 * 1000 * 24
            URL url = ossClient.generatePresignedUrl(bucketName, appDrl+"/"+ossFilePath+"/"+fileName, expiration);
            result.setContent(url.toString().replace("\\","//"));

            MonitorPoint monitorPoint = new MonitorPoint();
            monitorPoint.setPointid(rule.getMonitorPointid());
            monitorPoint.setDrlname(result.getContent());
            monitorPoint.setRunrate(60*1000);
            monitorPoint.setFlag(rule.getFlag());
            this.monitorPointDao.update(monitorPoint);
            //启用规则
//            restTemplate.postForObject(ruleStartUrl,monitorPoint,Result.class);
//            ruleEngine.updateRule(monitorPoint);
            monitorPoint.setDeviceid(rule.getDeviceuuid());
            monitorPoint.setForeginid(rule.getDevicemodelid());
            diagnosisExecutor.ruleStart(monitorPoint);
        }catch(Exception e){
            log.error("upload faild",e);
            result.setCode(ResultCode.ERROR_SERVICE);
            result.setMsg("上传失败");
        }finally {
            ossClient.shutdown();
        }
        return result;
    }
}
