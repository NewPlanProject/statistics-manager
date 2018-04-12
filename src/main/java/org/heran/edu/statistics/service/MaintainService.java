package org.heran.edu.statistics.service;

import org.citic.iiot.app.core.data.Result;
import org.heran.edu.statistics.domain.Maintain;
import org.heran.edu.statistics.vo.MaintainInVO;
import org.heran.edu.statistics.vo.MaintainSubOutVO;

import java.util.List;
import java.util.Map;

/**
 * Created by yxw on 2017/7/17.
 */
public interface MaintainService {


    /**
     * 维修/维护记录列表
     * @param maintainInVO
     * @return
     */
    Result<Map<String,Object>> getMaintainList(MaintainInVO maintainInVO);


    /**
     * 保存
     * @param maintain
     * @return
     */
    int save(Maintain maintain);


    /**
     * 批量删除
     * @param ids
     */
    void del(String[] ids);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    Maintain getMaintainById(String id);


    /**
     * 根据ID查询数据并导出
     * @param ids
     * @return
     */
    List<MaintainSubOutVO> getMaintainByIdExport(String[] ids);

    Map<String,Integer> getMaintainCount(Maintain maintain);

    Map<String,Object> getMaintainCountLast3Month(Maintain maintain);

    Map<String,Object> findMaintainCountLast3Month(Maintain maintain);
}
