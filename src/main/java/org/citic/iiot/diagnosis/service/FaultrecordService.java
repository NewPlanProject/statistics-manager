package org.citic.iiot.diagnosis.service;

import org.citic.iiot.app.core.data.Result;
import org.citic.iiot.app.core.session.User;
import org.citic.iiot.diagnosis.domain.Faultrecord;
import org.citic.iiot.diagnosis.vo.FaultrecordInVO;
import org.citic.iiot.diagnosis.vo.KnowledgeRelateFaultInVO;

import java.util.List;
import java.util.Map;

/**
 * User: Mr.zheng
 * Date: 2017/8/16
 * Time: 11:39
 */
public interface FaultrecordService {

    /**
     * 根据设备id 获取设备是否有故障
     * @param deviceid
     * @return
     */
    public List<Faultrecord> getDeviceRunStatus(String deviceid);

    /**
     * 根据设备id 获取设备是否有故障
     * @param deviceIds
     * @return
     */
    public List<Faultrecord> getDeviceRunStatusList(List<String> deviceIds);

    Result<Map<String,Object>> getfaultrecordList(FaultrecordInVO faultrecordInVO);

   // Result<Map<String,Object>> getFaultrecordObject(Faultrecord faultrecord);

    void updateStatusAndAddKnowledge(KnowledgeRelateFaultInVO knowledgeRelateFaultInVO,User user);

    Result<Map<String,Object>> getFaultrecordObjectUnsolved(Faultrecord faultrecord,User user);

    Result<Map<String,Object>> getFaultrecordObjectSolved(Faultrecord faultrecord);

    void updateStatus(KnowledgeRelateFaultInVO knowledgeRelateFaultInVO);

    Map<String,Integer> getUnsolvedFaultrecordCount(Faultrecord faultrecord);

    Map<String,Object> getUnsolvedFaultrecordCountLast7(Faultrecord faultrecord);

    Map<String,Object> getFaultrecordCountLast7(Faultrecord faultrecord);

    void batchDisposeStatus(String id);
}
