package org.citic.iiot.diagnosis.service;

import org.citic.iiot.app.core.session.User;
import org.citic.iiot.diagnosis.domain.Rule;

import java.util.List;

/**
 * User: Mr.zheng
 * Date: 2017/8/1
 * Time: 14:26
 */
public interface RuleService {

    /**
     * 保存规则
     *
     * @param rule
     * @return
     */
    Boolean createRule(Rule rule);

    /**
     * 规则列表
     * @param rule
     * @return
     */
    List<Rule> ruleList(Rule rule);

    /**
     * 规则数量
     * @param rule
     * @return
     */
    Integer ruleCount(Rule rule);

    /**
     * 知识库所需的规则列表
     * @param rule
     * @return
     */
    List<Rule> knowledgeRuleList(Rule rule);

    /**
     * 知识库所需的规则数量
     * @param rule
     * @return
     */
    Integer knowLedgeRuleCount(Rule rule);

    /**
     * 获取规则详细
     * @param rule
     * @return
     */
    Rule ruleDetail(Rule rule);

    /**
     * 修改规则
     * @param rule
     * @return
     */
    Boolean updateRule(Rule rule, User user);

    /**
     * 删除规则
     * @param rule
     * @return
     */
    Boolean deleteRule(Rule rule);

    /**
     * 通过文件名称获取所有规则
     * @param pointid
     * @return
     */
    List<Rule> findRulesByPointid(String pointid);

    /**
     * 根据pointid 修改monitorPoint表的相关规则
     * @param pointid
     * @return
     */
    Boolean updateMonitorPointRule(String pointid);

    /**
     * 根据code 修改知识库id
     * @param code
     * @param knowledegeid
     * @return
     */
    Boolean updateKnowledgeidByCode(String code, String knowledegeid);

    /**
     * 根据codes 修改知识库id
     * @param code
     * @return
     */
    Boolean updateKnowledgeidByCodes(String code);
}
