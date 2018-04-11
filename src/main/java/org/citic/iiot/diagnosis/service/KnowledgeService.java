package org.citic.iiot.diagnosis.service;

import org.citic.iiot.diagnosis.domain.Knowledge;
import org.citic.iiot.diagnosis.vo.KnowledgeInVO;

import java.util.List;

/**
 * Created by yxw on 2017/7/19.
 */
public interface KnowledgeService {

    /**
     * 根据知识库id获取相关知识库
     * @param knowid
     * @return
     */
    List<Knowledge> knowledgesByKnowId(String knowid);

    /**
     * 获取详细
     * @param knowledge
     * @return
     */
    Knowledge detail(Knowledge knowledge);

    /**
     * 保存知识库
     * @param knowledge
     * @return
     */
    void saveKnowledge(Knowledge knowledge) throws Exception;

    /**
     * 修改知识库
     * @param knowledge
     * @return
     */
    void updateKnowledge(Knowledge knowledge)throws Exception;

    /**
     * 列表显示分页
     * @return
     */
    List<Knowledge> getKnowledgeList(KnowledgeInVO knowledgeInVO);


    /**
     * 总数count
     * @param knowledgeInVO
     * @return
     */
    int selectCount(KnowledgeInVO knowledgeInVO);

    /**
     * 批量删除
     * @param knowids
     */
    Boolean dels(String knowids);

    /**
     * 根据knowid删除知识库
     * @param knowid
     */
    Boolean del(String knowid);

}
